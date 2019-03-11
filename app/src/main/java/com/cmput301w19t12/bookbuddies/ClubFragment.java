/**
 * ClubFragment
 *
 * March 9/2019
 */
package com.cmput301w19t12.bookbuddies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClubFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference clubsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private OnFragmentInteractionListener mListener;
    private User owner;
    private ListView clubsListView;
    private ArrayList<String> myClubNames;
    private String username;
    private Book book;
    private FloatingActionButton addButton;
    private Context context;

    public ClubFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClubFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubFragment newInstance(String param1, String param2) {
        ClubFragment fragment = new ClubFragment();
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
        return inflater.inflate(R.layout.fragment_club, container, false);
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

    /**
     * Called after the view is created. Initializes activity fields and adds on click listener
     * to the button.
     * @param view:View
     * @param savedInstanceState:Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clubsListView = (ListView) view.findViewById(R.id.clubsListView);
        myClubNames = new ArrayList<String>();
        authorizeUser();
        configureListView();
        populateClubsList();

        addButton = (FloatingActionButton) view.findViewById(R.id.addClubButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClubFragment.this.getActivity(), AddClubActivity.class);
                startActivity(i);
            }
        });
        context = this.getContext();
    }

    /**
     * Called before the view is displayed. Calls populateClubsList to fill listview with the users
     * clubs
     */
    @Override
    public void onResume() {
        super.onResume();
        populateClubsList();
    }

    /**
     * Iterates through the clubs from the database. If the user is either the owner or a member
     * of the club the club is added to the listview for the user to see.
     */
    public void populateClubsList() {
        clubsRef = FirebaseDatabase.getInstance().getReference("Clubs");
        clubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Club club;
                myClubNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    club = snapshot.getValue(Club.class);
                    Log.i("Club names", ""+club.getName()+" | "+club.getOwner().getUsername()+" | "+username);
                    if (((club.getOwner().getUsername()).equals(username)) || (club.getMembers().contains(username))) {
                        myClubNames.add(club.getName());
                    }
                }
                clubsListView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, myClubNames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Sets the click listener for the club names in the list. If a club name is long pressed a dialog
     * appears asking the user whether they want to delete the club.
     */
    public void configureListView() {

        clubsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String clubName = clubsListView.getAdapter().getItem(position).toString();
                getDeleteConfirmation(clubName).show();
                return false;
            }
        });
    }

    /**
     * Sets the current authorized as the user field and sets the username field with the user's
     * username.
     */
    public void authorizeUser() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                owner = dataSnapshot.getValue(User.class);
                username = owner.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates a dialog which prompts the user to verify if they want to delete the club. If so
     * the club is removed from the database and if not the dialog is closed.
     * @param name:String
     * @return Delete confirmation dialog:AlertDialog
     */
    private AlertDialog getDeleteConfirmation(final String name) {
        return new AlertDialog.Builder(getContext())
                .setTitle("Delete Club")
                .setMessage("Are you sure you want to delete this club?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeClub(name);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    /**
     * Deletes a club owned by the user from the database.
     * @param clubName:String
     */
    private void removeClub(final String clubName) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Clubs");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Club club = snapshot.getValue(Club.class);
                    Log.i("Club owner", ""+club.getOwner().getUsername()+" | "+club.getName()+" | ");
                    if (club.getOwner().getUsername().equals(username) && club.getName().equals(clubName)) {
                        ref.child(snapshot.getKey()).removeValue();
                        populateClubsList();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
