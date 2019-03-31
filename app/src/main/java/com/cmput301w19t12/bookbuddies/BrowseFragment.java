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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout suggestedScroll;
    private LinearLayout suggestedScroll2;
    private OnFragmentInteractionListener mListener;
    private Set<Book> books;
    private SimpleCursorAdapter adapter;
    private ImageButton sug1;
    private ImageButton sug2;
    private ImageButton sug3;
    private ImageButton sug4;


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

        //At Last, populate the two scroll views.
        suggestedScroll = (LinearLayout) view.findViewById(R.id.sugScroll);
        suggestedScroll2 = (LinearLayout) view.findViewById(R.id.sugScroll2);
        sug3 = (ImageButton) view.findViewById(R.id.sug3);
        sug4 = (ImageButton) view.findViewById(R.id.sug4);
        sug3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), Ad.class);
                Bundle bundle=new Bundle();
                bundle.putInt("image",R.drawable.coca);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        sug4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), Ad.class);
                Bundle bundle=new Bundle();
                bundle.putInt("image",R.drawable.pepsi);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        /*DatabaseReference sref = FirebaseDatabase.getInstance().getReference().child("Books").child("Available");
        sref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    for (DataSnapshot snap : item.getChildren()) {
                        Book book = snap.getValue(Book.class);
                        BookDetails details = book.getBookDetails();
                        String author = details.getAuthor().toLowerCase();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });*/
        /*String srefName = sref.orderByChild("owner").limitToFirst(2).toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images");
        Book book = new Gson().fromJson(srefName,Book.class);
        String browsebook = book.getBookDetails().getUniqueID();
*/
        //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
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
