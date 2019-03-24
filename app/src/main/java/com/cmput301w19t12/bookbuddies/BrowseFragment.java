package com.cmput301w19t12.bookbuddies;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.HashSet;
import java.util.Set;


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
    private Set<Book> books;
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
        books = new HashSet<>();
        adapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,new String[]{"Book"},new int[] {android.R.id.text1});

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // TODO Actually get the checkbox to work
        checkBox = view.findViewById(R.id.showUnavailableCheck);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
                Log.i("STUFF","IT GOT CLICKED");
            }
        });

        // Get search view and set searchable attributes
        searchBar = (SearchView) view.findViewById(R.id.bookSearch);
        searchBar.setQueryHint("Search for books");
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchBar.setSuggestionsAdapter(adapter);
        searchBar.setIconifiedByDefault(false);

        // set query listener to handle user typing detection and final submission
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // once the user decides to submit a query, pass that query to the results activity
                Intent intent = new Intent(BrowseFragment.this.getContext(),SearchResultsActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Whenever the user changes the contents of the query field, create a list of
                // suggestions that match the current contents of the query field
                getSuggestions(newText);
                return false;
            }
        });


        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                /*if the user clicks a suggestion, get the text from that suggestion
                * and use it to fill the query field*/
                Cursor cursor = searchBar.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(1);
                searchBar.setQuery(suggestion,true);
                return true;
            }
        });

    }

    private void populateSuggestions(){
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID,"Book"});
        Book[] bookArray = books.toArray(new Book[books.size()]);
        // Make rows containing book titles
        for (int i = 0; i < books.size(); i++){
            c.addRow(new Object[]{i,bookArray[i].getBookDetails().getTitle()});
        }
        // change adapter to reflect the changes
        adapter.changeCursor(c);
    }

    private void getSuggestions(final String text){
        books.clear();
        // Get a ref to available books to suggest to the user
        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("Books").child("Available");
        availableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                // iterate over all the books stored in this part of the database
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Book book = snap.getValue(Book.class);
                    try {
                        BookDetails details = book.getBookDetails();
                        String author = details.getAuthor().toLowerCase();
                        String title = details.getTitle().toLowerCase();
                        String ISBN = details.getISBN().toLowerCase();
                        // check if the book title or author contains the query text
                        if (author.contains(text) || title.contains(text) || ISBN.contains(text)) {
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
                Log.e("FirebaseError",databaseError.getMessage());
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
