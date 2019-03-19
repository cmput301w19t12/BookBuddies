package com.cmput301w19t12.bookbuddies;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BrowseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView searchBar;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Book> books;
    private SimpleCursorAdapter adapter;
    private CheckBox checkBox;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
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

        adapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,new String[]{"Book"},new int[] {android.R.id.text1});
        books = new ArrayList<>();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        checkBox = view.findViewById(R.id.showUnavailableCheck);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
                Log.i("STUFF","IT GOT CLICKED");
            }
        });

        searchBar = (SearchView) view.findViewById(R.id.bookSearch);
        searchBar.setQueryHint("Search for books");
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchBar.setSuggestionsAdapter(adapter);
        searchBar.setIconifiedByDefault(false);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //getAllMatches(newText);
                getAvailableMatches(newText);
                return false;
            }
        });

    }

    private void populateSuggestions(){
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID,"Book"});
        for (int i = 0; i < books.size(); i++){
            c.addRow(new Object[]{i,books.get(i).getBookDetails().getTitle()});
        }
        adapter.changeCursor(c);
    }

    private void getAvailableMatches(final String text){
        books.clear();
        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("Books").child("Available");
        availableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Book book = snap.getValue(Book.class);
                    try {
                        if (book.getOwner().equals(id) && book.getBookDetails().getTitle().contains(text)) {
                            books.add(book);
                            Log.i("STUFF", snap.getKey());
                        }
                    }catch (Exception e){
                        // ignore
                    }
                }
                populateSuggestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllMatches(final String text){
        books.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UID = FirebaseAuth.getInstance().getUid();
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    for (DataSnapshot bookData : category.getChildren()) {
                        Book book = bookData.getValue(Book.class);
                        try {
                            if (book.getOwner().equals(UID) && book.getBookDetails().getTitle().contains(text)) {
                                books.add(book);
                                Log.i("STUFF", bookData.getKey());
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
                populateSuggestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
