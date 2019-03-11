package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import com.ceylonlabs.imageviewpopup.ImagePopup;
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


/**NewBookActivity allows user to create a new book in the database and add a picture to the book
 *
 * @author bgrenier
 * @version 1.0*/


public class NewBookActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private DatabaseReference userLibRef;
    private DatabaseReference allBooksRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private FloatingActionButton addButton;
    private EditText titleField;
    private EditText authorField;
    private EditText ISBNField;
    private EditText desField;
    private Button editImage;
    private ImageView bookImage;

    private static final int SELECT_PICTURE = 1;

    private static final int REQUEST_IMAGE_CAPTURE = 2;


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

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setBackgroundColor(Color.BLACK);
        imagePopup.setFullScreen(true);
        imagePopup.setImageOnClickClose(true);

        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.initiatePopup(bookImage.getDrawable());
                imagePopup.viewPopup();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()) {
                    addBookToDatabase();
                }

            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

    }

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

   public void showMenu(View v){
       PopupMenu popupMenu = new PopupMenu(this,v);

       popupMenu.setOnMenuItemClickListener(this);
       popupMenu.inflate(R.menu.image_edit_main);
       popupMenu.show();
   }

   @Override
   public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.actionNewImage:
                takePicture();
                return true;
            case R.id.actionFromStorage:
                getImageFromStorage();
                return true;
            case R.id.actionDeleteImage:
                return true;
            default:
                return false;

        }
   }


   private void takePicture() {
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
       }

   }


   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Log.i("STUFF","STUFF");
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                bookImage.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Log.e("Image Load Error",e.getMessage());
            }
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");
            bookImage.setImageBitmap(imageBitMap);
        }
   }

   private void addPhotoToDatabase(String key){
       Bitmap bitmap = ((BitmapDrawable) bookImage.getDrawable()).getBitmap();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
       byte[] data = baos.toByteArray();

       UploadTask uploadTask = mStorageRef.child("images").child(key).putBytes(data);
       uploadTask.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception exception) {
               // Handle unsuccessful uploads
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
               // ...
           }
       });

       //Needed for testing:
       startActivity(new Intent(this, MainActivity.class));
       //-----------------------------------------
   }


    private void getImageFromStorage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
    }



    public void addBookToDatabase(){
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();
        String ISBN = ISBNField.getText().toString();
        String description = desField.getText().toString();
        String key = userLibRef.push().getKey();
        BookDetails details = new BookDetails(title,author,ISBN,description,key);
        String status = "Available";
        Book newBook = new Book(user.getUid(),details,status);

        userLibRef.child(status).child(key).setValue(newBook);
        allBooksRef.child(status).child(key).setValue(newBook);
        addPhotoToDatabase(key);
        Intent intent = new Intent(this, MyLibraryFragment.class);
        startActivity(intent);
    }
}
