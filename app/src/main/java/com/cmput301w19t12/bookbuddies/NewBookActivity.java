package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**NewBookActivity allows user to create a new book in the database and add a picture to the book
 *
 * @author bgrenier
 * @version 1.0*/


public class NewBookActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    // firebase related variables
    private DatabaseReference userLibRef;
    private DatabaseReference allBooksRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    // views we need to access
    private FloatingActionButton addButton;
    private EditText titleField;
    private EditText authorField;
    private EditText ISBNField;
    private EditText desField;
    private Button editImage;
    private ImageView bookImage;

    // request codes for camera and image selection activities
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;




    /**onCreate method inits instance variables and sets click listeners
     * @param savedInstanceState Bundle*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        // init firebase attributes
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        allBooksRef = FirebaseDatabase.getInstance().getReference("Books");
        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned");


        // get all the input fields
        titleField = findViewById(R.id.titleEdit);
        authorField = findViewById(R.id.authorEdit);
        ISBNField = findViewById(R.id.ISBNEdit);
        addButton = findViewById(R.id.addButton);
        desField = findViewById(R.id.DesEdit);
        editImage = findViewById(R.id.editImage);
        bookImage = findViewById(R.id.bookImage);


        // set click listener for confirm button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()) {
                    addBookToDatabase();
                    finish();
                }

            }
        });

        // set click listener for image editing button
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

    }

    /**checks that non of the book details fields are empty
     * @return isValid boolean*/
    private boolean checkFields(){
        boolean isValid = true;
        if(titleField.getText().toString().trim().length() == 0){
            titleField.setError("Must Enter Title");
            isValid = false;
        }
        if(authorField.getText().toString().trim().length() == 0){
            authorField.setError("Must Enter Author");
            isValid = false;
        }
        if(ISBNField.getText().toString().trim().length() == 0){
            ISBNField.setError("Must Enter ISBN");
            isValid = false;
        }
        if(desField.getText().toString().trim().length() == 0){
            desField.setError("Must Enter Description");
            isValid = false;
        }

        return isValid;

    }

    /**shows the image edit options menu
     * @param v View*/
   public void showMenu(View v){
       PopupMenu popupMenu = new PopupMenu(this,v);

       popupMenu.setOnMenuItemClickListener(this);
       popupMenu.inflate(R.menu.image_edit_main);
       popupMenu.show();
   }


   /**gets which menu item has been clicked and takes action accordingly
    * @param item MenuItem
    * @return boolean*/
   @Override
   public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            // open camera app
            case R.id.actionNewImage:
                takePicture();
                return true;
            // get the image from internal storage
            case R.id.actionFromStorage:
                getImageFromStorage();
                return true;
            case R.id.actionDeleteImage:
                deleteImage();
                return true;
            default:
                return false;

        }
   }


   /**Remove the image from the book image view*/
   private void deleteImage(){
       bookImage.setImageDrawable(null);
   }

   /**opens built in camera app for taking a photo of the book*/
   private void takePicture() {
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
       }

   }


   /**onActivityResult method gets either the image taken by the camera app, or the image chosen
    * storage and sets it to the book image view
    * @param requestCode int
    * @param resultCode int
    * @param data Intent*/
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        // if the user selected a picture from storage
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK){
            // get image uri
            Uri selectedImageUri = data.getData();
            try {
                // get the image from storage and put it in the image view
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                bookImage.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Log.e("Image Load Error",e.getMessage());
            }
        }
        // if the user has taken a new photo
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            // get bitmap from extras bundle passed by the camera app
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");
            Bitmap newBitmap = doRotation(imageBitMap);
            bookImage.setImageBitmap(newBitmap);
        }
   }

    /**Rotates the photo 90 degrees
     * @param sourceBitmap Bitmap*/
   private Bitmap doRotation(Bitmap sourceBitmap){
       Matrix matrix = new Matrix();
       matrix.preRotate(90);
       sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
       return sourceBitmap;
   }



    /**Adds a photo to the database associate with the book
    * @param key String*/
   private void addPhotoToDatabase(String key){
       // if no image has been set, skip this process
        if(bookImage.getDrawable() == null){
            return;
        }
        // get the bitmap for the picture and convert it into a byte array
        Bitmap bitmap = ((BitmapDrawable) bookImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // try to upload it to firebase storage
        UploadTask uploadTask = mStorageRef.child("images").child(key).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception exception) {
               Log.i("Book Picture",exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               Log.e("Book Picture","Book picture upload successful");
            }
       });
   }


   /**Opens up storage browser to allow the user to select a photo*/
    private void getImageFromStorage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
    }


    /**Gets the contents of all the book details fields, creates a new book and stores it in
     * the database*/
    public void addBookToDatabase(){
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();
        String ISBN = ISBNField.getText().toString();
        String description = desField.getText().toString();
        String key = userLibRef.push().getKey();
        BookDetails details = new BookDetails(title,author,ISBN,description,key);
        String status = "Available";
        Book newBook = new Book(user.getUid(),details,status);

        // store under user library
        userLibRef.child(status).child(key).setValue(newBook);
        // store under all books node
        allBooksRef.child(status).child(key).setValue(newBook);
        addPhotoToDatabase(key);

    }
}
