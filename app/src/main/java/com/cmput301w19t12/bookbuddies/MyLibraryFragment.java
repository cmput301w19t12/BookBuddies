package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyLibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
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
    private StorageReference mStorageRef;
    private DatabaseReference userLibRef;
    private TitleContainer titleContainer;
    private Context context;

    private ArrayList<String> MenuHeaders;
    private ExpandingMenuListAdapter adapter;
    private ExpandableListView Menu;
    private HashMap<String, List<String>> menuChildHeaders;



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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeMenu();
        Menu = (ExpandableListView) view.findViewById(R.id.ExpandingMenu);

        Menu.setAdapter(new ExpandingMenuListAdapter(getContext(), MenuHeaders, menuChildHeaders));
        Menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //TODO: Code for when the book is clicked in the menu
                return true;
            }
        });

    }

    public void makeMenu() {
        MenuHeaders = new ArrayList<String>();
        titleContainer = new TitleContainer();
        // TODO: Code for inserting the child
        MenuHeaders.add("Owned by me");
        MenuHeaders.add("Borrowed from buddy");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Owned").child("Available");
        userLibRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    titleContainer.getBookTitlesAvailable().add(dataSnapshot.child("BookDetails").getValue(BookDetails.class).getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menuChildHeaders.put(MenuHeaders.get(0), titleContainer.getBookTitlesAvailable());

        userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Books").child("Borrowing");
        userLibRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    titleContainer.getBookTitlesBorrowing().add(dataSnapshot.child("BookDetails").getValue(BookDetails.class).getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menuChildHeaders.put(MenuHeaders.get(1), titleContainer.getBookTitlesBorrowing());

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
