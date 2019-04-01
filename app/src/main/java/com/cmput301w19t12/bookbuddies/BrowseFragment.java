package com.cmput301w19t12.bookbuddies;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.MatrixCursor;
import android.media.Image;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
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

    private SearchView searchBar;
    private OnFragmentInteractionListener mListener;
    private Set<Book> books;
    private SimpleCursorAdapter adapter;
    private ArrayList<Book> availableBooks;
    private ListView availableListView;


    public BrowseFragment() {
        // Required empty public constructor
    }


    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        books = new HashSet<>();
        // simpleCursorAdapter deprecated but it works so I'm scared to change it
        adapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,new String[]{"Book"},new int[] {android.R.id.text1});

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Get search view and set searchable attributes
        searchBar = (SearchView) view.findViewById(R.id.bookSearch);
        searchBar.setQueryHint("Search for books");
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchBar.setSuggestionsAdapter(adapter);
        searchBar.setIconifiedByDefault(false);
        availableBooks = new ArrayList<>();
        availableListView = view.findViewById(R.id.recommendationList);

        makeBooksList();

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
                getSuggestions(newText.toLowerCase());
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

    private void makeBooksList(){
        // get the first 10 books in the database

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books").child("Available");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Book book = snap.getValue(Book.class);
                    if (!(book.getOwner().equals(FirebaseAuth.getInstance().getUid()))) {
                        availableBooks.add(book);
                        total += 1;
                    }
                    if(total == 10){
                        break;
                    }
                }
                addBooksToListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addBooksToListView(){
        ArrayList<String> entries = new ArrayList<>();

        for(Book book : availableBooks){
            BookDetails details = book.getBookDetails();
            String info = String.format("%s\n%s",details.getTitle(),details.getAuthor());
            entries.add(info);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,entries);
        availableListView.setAdapter(adapter);
        availableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = availableBooks.get(position);
                Intent intent = new Intent(getContext(),BookDetailsActivity.class);
                intent.putExtra("book",new Gson().toJson(book));
                startActivity(intent);
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
        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("Books");
        availableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                ArrayList<String> acceptedStatuses = new ArrayList<>();
                acceptedStatuses.add("Requested");
                acceptedStatuses.add("Available");
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    if(acceptedStatuses.contains(item.getKey())) {
                        // iterate over all the books stored in this part of the database
                        for (DataSnapshot snap : item.getChildren()) {
                            Book book = snap.getValue(Book.class);
                            try {
                                BookDetails details = book.getBookDetails();
                                String author = details.getAuthor().toLowerCase();
                                String title = details.getTitle().toLowerCase();
                                String ISBN = details.getISBN().toLowerCase();
                                // check if the book title or author contains the query text
                                if ((author.contains(text) || title.contains(text) || ISBN.contains(text))
                                        && (!(book.getOwner().equals(id)))) {
                                    books.add(book);
                                    Log.i("STUFF", snap.getKey());
                                }
                            } catch (Exception e) {
                                // ignore
                            }
                        }
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
        void onFragmentInteraction(Uri uri);
    }
}
