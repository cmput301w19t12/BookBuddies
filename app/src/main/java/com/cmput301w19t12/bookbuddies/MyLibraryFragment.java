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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

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
    private static String ARG_PARAM1 = "param1";
    private static String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userLibRef;
    private ArrayList<String> MenuHeaders;
    private ExpandableListView Menu;
    private HashMap<String, List<String>> menuChildHeaders;
    private FloatingActionButton addNew;

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> bookTitles;
    private HashMap<String, List<Book>> bookList;
    private ArrayList<Book> books;
    private Button expandAllButton;

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
     * @param savedInstanceState:Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /**
     * Draw the user interface
     * @param inflater:LayoutInflater
     * @param container:ViewGroup
     * @param savedInstanceState:Bundle
     * @return View
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
     * @param view:View
     * @param savedInstanceState:Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Menu = view.findViewById(R.id.ExpandingMenu);

//        addNew = view.findViewById(R.id.addNewBook);
//        addNew.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent intent = new Intent(getActivity(), NewBookActivity.class);
//                startActivity(intent);}
//        });
      
        bookTitles = new ArrayList<>();
        books = new ArrayList<>();
        Menu = (ExpandableListView) view.findViewById(R.id.ExpandingMenu);
        Menu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }

    /**
     * When a book status header is expanded, the expandable list view is expanded to accomadate for
     * the new length added when the children of the header is shown. Works by adding the length
     * of all the children (book titles) in the groupView specified by group:int to the listView.
     * Citation: https://thedeveloperworldisyours.com/android/expandable-listview-inside-scrollview/
     * @param listView:ExpandableListView
     * @param group:int
     */
    public void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        height+=100;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
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
        Menu.setAdapter(new ExpandingMenuListAdapter(getContext(), MenuHeaders, menuChildHeaders,bookList));
        //TODO: Add the on click listener code here for when a book is clicked
        Menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                switchToDetails((Book) v.getTag());
                return false;
            }
        });

        Menu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    //int childPosition = ExpandableListView.getPackedPositionChild(id);
                    getDeleteConfirmation((Book) view.getTag()).show();
                    return true;
                }

                return false;
            }
        });

    }

    private AlertDialog getDeleteConfirmation(final Book book) {
        return new AlertDialog.Builder(getContext())
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeBook(book);
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

    public void removeBook(final Book book){
        //toDO
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Books").child("Available");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book temp = snapshot.getValue(Book.class);
                    if (temp.getBookDetails().getTitle().equals(book.getBookDetails().getTitle())) {
                        ref.child(snapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void switchToDetails(Book book){
        Intent intent = new Intent(MyLibraryFragment.this.getContext(), BookDetailsActivity.class);
        intent.putExtra("book",new Gson().toJson(book));
        boolean isOwner = false;
        if(book.getOwner().equals(mAuth.getUid())){
            isOwner = true;
        }
        intent.putExtra("isOwner",isOwner);
        startActivity(intent);
    }
    /**
     * Maps the user's book titles to their status where the book titles are retrieved from the
     * firebase database.
     * @param status:String
     * @param index:int
     */
    public void addMyBookTitles(final String status, final int index) {
        userLibRef = FirebaseDatabase.getInstance().getReference("Books").child(status);
        userLibRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookTitles.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    String title = snapshot.getValue(Book.class).getBookDetails().getTitle();
                    try {
                        if (user.getUid().equals(book.getOwner())) {
                            bookTitles.add(title);
                            books.add(book);
                        }
                    }
                    catch (NullPointerException e) {
                        //ignore the invalid data
                    }
                }
                menuChildHeaders.put(MenuHeaders.get(index), getCopy(bookTitles));
                bookList.put(MenuHeaders.get(index),getCopyBooks(books));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Initializes the MenuHeaders which is the expandable header on the ExpandableListView with the
     * different book statuses. It also calls addMyBookTitles to get the respective book titles from the database
     * and maps them to their header in the menuChildHeaders field.
     */
    public void makeMenu() {
        MenuHeaders = new ArrayList<String>();
        MenuHeaders.add("Available");
        MenuHeaders.add("Accepted");
        MenuHeaders.add("Requested");
        MenuHeaders.add("Borrowed");
        menuChildHeaders = new HashMap<String, List<String>>();
        bookList = new HashMap<>();



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        addMyBookTitles("Available", 0);
        addMyBookTitles("Accepted", 1);
        addMyBookTitles("Requested", 2);
        addMyBookTitles("Borrowed", 3);

    }

    /**
     * Copies all the elements in the passed in parameter to a new ArrayList<String> before returning
     * the copied list.
     * @param myBookTitles:ArrayList<String>
     * @return ArrayList<String>
     */
    public ArrayList<String> getCopy(ArrayList<String> myBookTitles) {
        ArrayList<String> titles = new ArrayList<String>();
        titles.addAll(0, myBookTitles);
        return titles;
    }

    public ArrayList<Book> getCopyBooks(ArrayList<Book> myBookTitles) {
        ArrayList<Book> titles = new ArrayList<Book>();
        titles.addAll(0, myBookTitles);
        return titles;
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
