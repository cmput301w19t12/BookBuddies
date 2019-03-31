package com.cmput301w19t12.bookbuddies;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;


/**Presents the user with their profile information
 *
 * @verion 1.0
 *
 * @see User*/

public class MyProfileActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private TextView fullName;
    private TextView username;
    private TextView phoneNum;
    private TextView email;
    private User user;
    private User userViewed;
    private String usernameToShow;
    private String userId;
    private Button viewPendingTransactions;
    private Button viewMyRequestsButton;
    private Button viewMyBorrowingBooks;
    private ImageView profileImage;
    private StorageReference mStorageRef;

    // activity result codes
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        fullName = findViewById(R.id.profileFullName);
        username = findViewById(R.id.profileUsername);
        phoneNum = findViewById(R.id.profilePhoneNum);
        email = findViewById(R.id.profileEmailAddr);
        profileImage = findViewById(R.id.profileImage);
        FirebaseUser userDB = mAuth.getCurrentUser();
        userId = userDB.getUid();
        user = new User();
        viewPendingTransactions = findViewById(R.id.viewPendingTransactionsButton);
        viewMyRequestsButton = findViewById(R.id.viewPendingRequests);
        viewMyBorrowingBooks = findViewById(R.id.viewBooksBorrowing);


        Bundle b = getIntent().getExtras();
        usernameToShow = b.getString("username");

        viewPendingTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTransactionsList();
            }
        });

        viewMyRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRequestsList();
            }
        });

        viewMyBorrowingBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBorrowingList();
            }
        });

        getUserData();

    }

    private void viewBorrowingList() {
        startActivity(new Intent(MyProfileActivity.this, MyBorrowingBooksActivity.class));
    }

    private void viewRequestsList() {
        startActivity(new Intent(MyProfileActivity.this, MyRequestsActivity.class));
    }

    private void viewTransactionsList() {
        startActivity(new Intent(MyProfileActivity.this, PendingTransactionsActivity.class));
    }

    public void setEditListeners() {
        if (userViewed.getUsername().equals(user.getUsername())) {
            enableButtons();
            fullName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(0);
                    return true;
                }
            });
            phoneNum.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(1);
                    return true;
                }
            });
            email.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openEditMenu(2);
                    return true;
                }
            });
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(v);
                }
            });
        }
    }


    private void getImage(String ID){
        // get the image for this book from firebase storage and put it in the image view
        // uses glide library
        try {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(ID);

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri.toString())
                            .into((ImageView) findViewById(R.id.profileImage));
                }
            });

        }catch (Exception e){
            Log.e("BookImageGet",e.getMessage());
        }
    }


    /**
     * shows options for editing photo
     */
    public void showMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.image_edit_main);
        popupMenu.show();
    }

    private void removeImage() {
        profileImage.setImageDrawable(null);
        mStorageRef.child("images").child(userViewed.getUsername()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Picture","Picture successfully deleted");
            }
        });
    }

    // call the built in camera app to take a new photo
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
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

    private void getImageFromStorage(){
        // open the built in photo gallery app
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
    }

    private void addPhotoToDatabase(String key){
        // prepare for upload by converting bitmap to byte array
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
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

        private void enableButtons() {
            viewPendingTransactions.setVisibility(View.VISIBLE);
            viewPendingTransactions.setClickable(true);
            viewMyRequestsButton.setVisibility(View.VISIBLE);
            viewMyRequestsButton.setClickable(true);
            viewMyBorrowingBooks.setVisibility(View.VISIBLE);
            viewMyBorrowingBooks.setClickable(true);
        }

        public void getUserData() {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseUser userDB = mAuth.getCurrentUser();
                    String userId = userDB.getUid();
                    user = dataSnapshot.child(userId).getValue(User.class);
                    for (DataSnapshot allUsers : dataSnapshot.getChildren()) {
                        User tempUser = allUsers.getValue(User.class);
                        try {
                            String usernameViewed = tempUser.getUsername();
                            if (usernameViewed.equals(usernameToShow)) {
                                userViewed = tempUser;
                                Log.i("USER", userViewed.getUsername());
                                setTextViews();
                                setEditListeners();
                                getImage(usernameViewed);

                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        // gets the results of any of the activities for result that we may call
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
                // if a photo has been chosen from storage, get the bitmap and attach it to the book image view
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    profileImage.setImageBitmap(bitmap);
                    addPhotoToDatabase(user.getUsername());
                } catch (Exception e) {
                    Log.e("Image Load Error", e.getMessage());
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                // if a new photo has been taken, extract it from the returned intent and attach to the
                // book image view
                Bundle extras = data.getExtras();
                Bitmap imageBitMap = (Bitmap) extras.get("data");
                // rotate by 90 degrees to compensate for taking vertical photos
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imageBitMap = Bitmap.createBitmap(imageBitMap, 0, 0, imageBitMap.getWidth(), imageBitMap.getHeight(), matrix, true);
                profileImage.setImageBitmap(imageBitMap);
                addPhotoToDatabase(user.getUsername());
            }
        }

        /**Populates text views*/
        public void setTextViews(){
            if (userViewed.getFullName().equals("")) {
                if (userViewed.getUsername().equals("")) {
                    fullName.setText("John Doe");
                } else {
                    fullName.setText(userViewed.getUsername());
                }
            } else {
                fullName.setText(userViewed.getFullName());
            }
            if (userViewed.getUsername().equals("")) {
                username.setText("no_username");
            } else {
                username.setText(userViewed.getUsername());
            }
            phoneNum.setText("Phone: " + userViewed.getPhoneNumber());
            email.setText("Email: " + userViewed.getEmailAddress());
        }

        /**Opens menu to edit profile information*/
        public void openEditMenu(final int field){

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompts, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.promptUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    switch (field) {
                                        case 0:
                                            user.setFullName(userInput.getText().toString());
                                            userRef.child(userId).setValue(user);
                                            break;
                                        case 1:
                                            user.setPhoneNumber(userInput.getText().toString());
                                            userRef.child(userId).setValue(user);
                                            break;
                                        case 2:
                                            user.setEmailAddress(userInput.getText().toString());
                                            userRef.child(userId).setValue(user);
                                            break;
                                    }

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }
