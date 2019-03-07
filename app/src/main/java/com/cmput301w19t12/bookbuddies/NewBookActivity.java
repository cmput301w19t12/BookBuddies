package com.cmput301w19t12.bookbuddies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**NewBookActivity allows user to create a new book in the database and add a picture to the book
 *
 * @author bgrenier
 * @version 1.0*/


public class NewBookActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private DatabaseReference userLibRef;
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
    private String selectedImagePath;
    private String fileManagerString;

    private static final int REQUEST_IMAGE_CAPTURE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        // init firebase attributes
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned");

        // get all the input fields
        titleField = findViewById(R.id.titleEdit);
        authorField = findViewById(R.id.authorEdit);
        ISBNField = findViewById(R.id.ISBNEdit);
        addButton = findViewById(R.id.addButton);
        desField = findViewById(R.id.DesEdit);
        editImage = findViewById(R.id.editImage);
        bookImage = findViewById(R.id.bookImage);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToDatabase();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

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


   private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           // Create the File where the photo should go
           File photoFile = null;
           try {
               photoFile = createImageFile();
           } catch (IOException ex) {
               // Error occurred while creating the File
               Log.e("File Error","Error occured while creating image file");
           }
           // Continue only if the File was successfully created
           if (photoFile != null) {
               Uri photoURI = FileProvider.getUriForFile(this,
                       "com.example.android.fileprovider",
                       photoFile);
               takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
           }
       }
   }

   private File createImageFile() throws IOException{
        String currentPhotoPath;
       // Create an image file name
       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
       String imageFileName = "JPEG_" + timeStamp + "_";
       File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       File image = File.createTempFile(
               imageFileName,  /* prefix */
               ".jpg",         /* suffix */
               storageDir      /* directory */
       );

       // Save a file: path for use with ACTION_VIEW intents
       currentPhotoPath = image.getAbsolutePath();
       return image;
   }



   private void getImageFromStorage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Log.i("STUFF","STUFF");
        if(requestCode == SELECT_PICTURE){
            Uri selectedImageUri = data.getData();
            fileManagerString = getPath(selectedImageUri);
            Log.i("STUFF","STUFF"+fileManagerString);
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");
            bookImage.setImageBitmap(imageBitMap);

        }
   }


    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }




    public void addBookToDatabase(){
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();
        String ISBN = ISBNField.getText().toString();
        String description = desField.getText().toString();
        BookDetails details = new BookDetails(title,author,ISBN,description);
        String status = "Available";
        Book newBook = new Book(user.getEmail(),details,status);

        userLibRef.child(status).child(newBook.getBookDetails().getISBN()).setValue(newBook);

    }
}
