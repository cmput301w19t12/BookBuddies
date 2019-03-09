/**
 * MyLibraryFragment
 *
 * March 8/2019
 *
 * @Author Ayub Ahmed
 *
 *
 */
package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyLibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *----------------------------------------------------------------------
 *
 * MyLibraryFragment displays the books that the user owns in a drop down menu filtered by their status.
 * It also displays the books that the user is currently borrowing from someone else. If a book is pressed
 * more details about the book are shown.
 *
 * --------------------------------------------------------------------
 */

public class MyLibraryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userLibRef;
    private TitleContainer titleContainer;
    private ArrayList<String> MenuHeaders;
    private ExpandableListView Menu;
    private HashMap<String, List<String>> menuChildHeaders;
    private Button addNew;
    private String ttl;
    private String author;
    private String isbn;
    private String owner;
    private String status;
    private String desc;

    private OnFragmentInteractionListener mListener;

    public MyLibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyLibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyLibraryFragment newInstance(String param1, String param2) {
        MyLibraryFragment fragment = new MyLibraryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the system is creating the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /*addNew = (Button) getView().findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), NewBookActivity.class);
                startActivity(intent);}
        });*///This doesn't work for some reasons.
    }

    /**
     * Draw the user interface
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_library, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Called when onCreateView is completed. An ExpandableListView (Menu) is instantiated with a
     * ExpandableListView layout item.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Menu = (ExpandableListView) view.findViewById(R.id.ExpandingMenu);
    }

    /**
     * Called before the Fragment is displayed on the screen. The adapter is set for the ExpandableListView
     * and a onclicklistener is initialized to handle when the user clicks on a book title.
     * If a book title is clicked, more details on the book are displayed.
     */
    @Override
    public void onResume() {
        super.onResume();
        makeMenu();
        Menu.setAdapter(new ExpandingMenuListAdapter(getContext(), MenuHeaders, menuChildHeaders));

        //TODO: Add the on click listener code here for when a book is clicked
        Menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*ttl = parent.getExpandableListAdapter().getChild(groupPosition, childPosition).getid();
                author = ;
                isbn = ;
                owner = ;
                status = ;
                desc = parent.getExpandableListAdapter().getChild(groupPosition, childPosition)
                Intent intent = new Intent(, book_details.class);
                intent.putExtra(EXTRA_ttl, ttl);
                intent.putExtra(EXTRA_auth, author);
                intent.putExtra(EXTRA_isbn, isbn);
                intent.putExtra(EXTRA_owner, owner);
                intent.putExtra(EXTRA_status, status);
                intent.putExtra(EXTRA_desc, desc);
                startActivity(intent);*/
                return false;
            }
        });

    }

    /**
     * Initializes the MenuHeaders which is the expandable header on the ExpandableListView with the
     * different book statuses. It also gets the respective book titles from the database and maps them
     * to their header in the menuChildHeaders field.
     */
    public void makeMenu() {
        MenuHeaders = new ArrayList<String>();
        titleContainer = new TitleContainer();
        MenuHeaders.add("Available");
        MenuHeaders.add("Accepted");
        MenuHeaders.add("Requested");
        MenuHeaders.add("Currently borrowed");
        MenuHeaders.add("Borrowed from buddy");
        menuChildHeaders = new HashMap<String, List<String>>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Borrowing");
        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    titleContainer.getBookTitlesBorrowing().add(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned").child("Available");
        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    titleContainer.getBookTitlesAvailable().add(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned").child("Accepted");
        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    titleContainer.getBookTitlesAccepted().add(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned").child("Requested");
        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    titleContainer.getBookTitlesRequested().add(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned").child("Borrowed");
        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    titleContainer.getBookTitlesBorrowed().add(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menuChildHeaders.put(MenuHeaders.get(0), titleContainer.getBookTitlesAvailable());
        menuChildHeaders.put(MenuHeaders.get(1), titleContainer.getBookTitlesAccepted());
        menuChildHeaders.put(MenuHeaders.get(2), titleContainer.getBookTitlesRequested());
        menuChildHeaders.put(MenuHeaders.get(3), titleContainer.getBookTitlesBorrowed());
        menuChildHeaders.put(MenuHeaders.get(4), titleContainer.getBookTitlesBorrowing());

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
