/*
Citations:
    https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */

/**
 * ExpandingMenuListAdapter
 *
 * March 8/2019
 *
 * @Author Ayub Ahmed
 *
 */
package com.cmput301w19t12.bookbuddies;


import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Expanding List Adapter which inherits from BaseExpandableListAdapter placing the
 * headers as the groupview and each of the titles as a childview within the group. The result
 * being a header with the status of the book and the books with that status listed within the header
 * when expanded.
 */
public class ExpandingMenuListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> MenuHeader;
    private HashMap<String, List<String>> headerChildPairs;
    private HashMap<String, List<Book>> childBooks;

    /**
     * Initializes the fields context, MenuHeader, Childheaders with the values that
     * are passed in.
     * @param context:Context
     * @param MenuHeader:ArrayList<String>
     * @param ChildHeaders:HashMap<String, List<String>>
     */
    public ExpandingMenuListAdapter(Context context, ArrayList<String> MenuHeader, HashMap<String, List<String>> ChildHeaders,HashMap<String,List<Book>> bookList){
        this.context = context;
        this.MenuHeader = MenuHeader;
        this.headerChildPairs = ChildHeaders;
        this.childBooks = bookList;
    }

    /**
     * Returns the number of headers, in this case the number of book statuses.
     * @return int
     */
    @Override
    public int getGroupCount() {
        return MenuHeader.size();
    }

    /**
     * Returns the number of children that a certain header/group has. In this case, the number
     * of books that have a certain status.
     * @param groupPosition:int
     * @return Number of headers
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return headerChildPairs.get(MenuHeader.get(groupPosition)).size();
    }

    /**
     * Returns the name of the header (a book status) retrieved from the given index
     * of groupPosition
     * @param groupPosition:int
     * @return Book Status Header : int
     */
    @Override
    public Object getGroup(int groupPosition) {
        return MenuHeader.get(groupPosition);
    }

    /**
     * Returns a book title at the index childPosition within the header (book status) group found at the
     * index groupPosition.
     * @param groupPosition:int
     * @param childPosition:int
     * @return Book Title : String
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return headerChildPairs.get(MenuHeader.get(groupPosition)).get(childPosition);
    }

    /**
     * Returns a book object with the given title
     * @param title String
     * @return book Book*/
    public Book getChildBook(String title){
        for(Map.Entry<String,List<Book>> entry  : childBooks.entrySet()){
            for (Book book : entry.getValue()){
                if (book.getBookDetails().getTitle().equals(title)){
                    return book;
                }
            }

        }
        return null;
    }

    /**
     * Returns the index of the group
     * @param groupPosition:int
     * @return groupPosition:int
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Returns the index of the child
     * @param groupPosition:int
     * @param childPosition:int
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Sets the text of the expandable header and inflates the header view.
     * @param groupPosition:int
     * @param isExpanded:boolean
     * @param convertView:View
     * @param parent:ViewGroup
     * @return convertView:View
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, parent, false);
        }

        TextView header = (TextView) convertView.findViewById(R.id.Header);
        header.setText(headerTitle);
        header.setTypeface(null, Typeface.BOLD);
        return convertView;
    }

    /**
     * Sets the text of the children and returns the view as well as inflates the child view.
     * @param groupPosition:int
     * @param childPosition:int
     * @param isLastChild:boolean
     * @param convertView:View
     * @param parent:ViewGroup
     * @return convertView:View
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childHeaderTitle = (String) getChild(groupPosition, childPosition);
        Book book = getChildBook(childHeaderTitle);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
            convertView.setTag(book);
        }
        TextView text = (TextView) convertView.findViewById(R.id.ChildHeader);
        text.setText(childHeaderTitle);
        text.setTag(book);
        return convertView;
    }

    /**
     * Returns true if the user is allowed to select a child.
     * @param groupPosition:int
     * @param childPosition:int
     * @return boolean
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean searchForTitle(int groupPosition, String text) {
        if (headerChildPairs.get(MenuHeader.get(0)).contains(text)) {
            return true;
        }
        return false;
    }

}
