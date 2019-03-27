package com.cmput301w19t12.bookbuddies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;



/**NewBookActivity allows user to create a new book in the database and add a picture to the book
 *
 * @author bgrenier
 * @version 1.0
 *
 * @see Book
 * @see MyLibraryFragment*/


public class NewBookActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private DatabaseReference allBooksRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private FloatingActionButton addButton;
    private FloatingActionButton scanButton;
    private EditText titleField;
    private EditText authorField;
    private EditText ISBNField;
    private EditText desField;
    private Button editImage;
    private ImageView bookImage;

    // activity result codes
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int SCAN_ISBN = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        // init firebase attributes
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        allBooksRef = FirebaseDatabase.getInstance().getReference("Books");

        // get all the input fields
        titleField = findViewById(R.id.titleEdit);
        authorField = findViewById(R.id.authorEdit);
        ISBNField = findViewById(R.id.ISBNEdit);
        addButton = findViewById(R.id.addButton);
        desField = findViewById(R.id.DesEdit);
        editImage = findViewById(R.id.editImage);
        bookImage = findViewById(R.id.bookImage);

        // define popup for pressing book photo
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

        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open ISBN scanning activity
                Intent intent = new Intent(NewBookActivity.this, LivePreviewActivity.class);
                startActivityForResult(intent,SCAN_ISBN);
            }
        });

    }

    // Check that all fields have some sort of input
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

    /**shows options for editing photo*/
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
                removeImage();
                return true;
            default:
                return false;

        }
   }

   private void removeImage(){
       bookImage.setImageDrawable(null);
   }

    // call the built in camera app to take a new photo
   private void takePicture() {
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
       }

   }


   // gets the results of any of the activities for result that we may call
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK){
            // if a photo has been chosen from storage, get the bitmap and attach it to the book image view
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
            // if a new photo has been taken, extract it from the returned intent and attach to the
            // book image view
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");
            // rotate by 90 degrees to compensate for taking vertical photos
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            imageBitMap =  Bitmap.createBitmap(imageBitMap, 0, 0, imageBitMap.getWidth(), imageBitMap.getHeight(), matrix, true);
            bookImage.setImageBitmap(imageBitMap);
        }
        else if(requestCode == SCAN_ISBN && resultCode == RESULT_OK){
            // get returned isbn from scanner and put it in ISBN field, and retrieve the rest of the
            // book details based on the isbn
            String result = data.getStringExtra("result");
            ISBNField.setText(result);
            getBookDetails(result);
        }
   }

    private void getBookDetails(String ISBN){
       // url to make a request to the google books api to get the information for this book
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + ISBN;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // parse the JSON response from the api
                            JSONObject json = new JSONObject(response);
                            JSONArray jArray = json.getJSONArray("items");
                            JSONObject volumeInfo = jArray.getJSONObject(0).getJSONObject("volumeInfo");
                            String title = volumeInfo.getString("title");
                            String description = volumeInfo.getString("description");
                            JSONArray authors = volumeInfo.getJSONArray("authors");
                            String author = authors.getString(0);
                            // ensure the fields were included before trying to add them to avoid null pointers
                            if(!(title.isEmpty())) {
                                titleField.setText(title);
                            }
                            if(!(author.isEmpty())) {
                                authorField.setText(author);
                            }
                            if(!(description.isEmpty())) {
                                desField.setText(description);
                            }
                        }catch (Exception e){
                            Log.e("GoogleBooksAPI",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

   private void addPhotoToDatabase(String key){
       // prepare for upload by converting bitmap to byte array
       Bitmap bitmap = ((BitmapDrawable) bookImage.getDrawable()).getBitmap();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
       byte[] data = baos.toByteArray();

       // upload photo to firebase storage using book key
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
       // open the built in photo gallery app
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
    }



    private void addBookToDatabase(){
       // construct a temp book object and upload to firebase and exit activity
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();
        String ISBN = ISBNField.getText().toString();
        String description = desField.getText().toString();
        String key = allBooksRef.push().getKey();
        BookDetails details = new BookDetails(title,author,ISBN,description,key);
        String status = "Available";
        Book newBook = new Book(user.getUid(),details,status,null);

        allBooksRef.child(status).child(key).setValue(newBook);
        addPhotoToDatabase(key);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
