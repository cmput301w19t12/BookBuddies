package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * New Book Fragment allows the user to add a new book to their library
 */
public class NewBookFragment extends Fragment {


    private DatabaseReference userLibRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FloatingActionButton addButton;
    EditText titleField;
    EditText authorField;
    EditText ISBNField;
    EditText desField;


    private OnFragmentInteractionListener mListener;

    public NewBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewBookFragment newInstance(String param1, String param2) {
        NewBookFragment fragment = new NewBookFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init firebase attributes
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_new_book, container, false);
        // get all the input fields
        titleField = root.findViewById(R.id.titleEdit);
        authorField = root.findViewById(R.id.authorEdit);
        ISBNField = root.findViewById(R.id.ISBNEdit);
        addButton = root.findViewById(R.id.addButton);
        desField = root.findViewById(R.id.DesEdit);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToDatabase();
            }
        });

        return root;
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
