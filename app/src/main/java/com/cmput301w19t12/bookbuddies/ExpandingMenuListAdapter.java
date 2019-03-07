package com.cmput301w19t12.bookbuddies;

//https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandingMenuListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> MenuHeader;
    private HashMap<String, List<String>> MenuHeadersChildHeaders;

    public ExpandingMenuListAdapter(Context context, List<String> MenuHeader, HashMap<String, List<String>> MenuChildHeaders){
        this.context = context;
        this.MenuHeader = MenuHeader;
        this.MenuHeadersChildHeaders = MenuChildHeaders;
    }

    @Override
    public int getGroupCount() {
        return MenuHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return MenuHeadersChildHeaders.get(MenuHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return MenuHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return MenuHeadersChildHeaders.get(MenuHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

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

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childHeaderTitle = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, parent,false);
        }

        TextView childHeader = (TextView) convertView.findViewById(R.id.ChildHeader);
        childHeader.setText(childHeaderTitle);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



}
