package com.cmput301w19t12.bookbuddies;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TitleContainer {
    private ArrayList<String> bookTitlesAvailable;
    private ArrayList<String> bookTitlesBorrowing;
    private ArrayList<String> bookTitlesAccepted;
    private ArrayList<String> bookTitlesBorrowed;
    private ArrayList<String> bookTitlesRequested;

    public TitleContainer() {
        bookTitlesAvailable = new ArrayList<String>();
        bookTitlesBorrowing = new ArrayList<String>();
        bookTitlesAccepted = new ArrayList<String>();
        bookTitlesBorrowed = new ArrayList<String>();
        bookTitlesRequested = new ArrayList<String>();
    }

    public ArrayList<String> getBookTitlesAvailable() {
        return bookTitlesAvailable;
    }


    public void setBookTitlesAvailable(ArrayList<String> bookTitlesAvailable) {
        this.bookTitlesAvailable = bookTitlesAvailable;
    }

    public ArrayList<String> getBookTitlesAccepted() {
        return bookTitlesAccepted;
    }

    public void setBookTitlesAccepted(ArrayList<String> bookTitlesAccepted) {
        this.bookTitlesAccepted = bookTitlesAccepted;
    }

    public ArrayList<String> getBookTitlesBorrowed() {
        return bookTitlesBorrowed;
    }

    public void setBookTitlesBorrowed(ArrayList<String> bookTitlesBorrowed) {
        this.bookTitlesBorrowed = bookTitlesBorrowed;
    }

    public ArrayList<String> getBookTitlesRequested() {
        return bookTitlesRequested;
    }

    public void setBookTitlesRequested(ArrayList<String> bookTitlesRequested) {
        this.bookTitlesRequested = bookTitlesRequested;
    }

    public ArrayList<String> getBookTitlesBorrowing() {
        return bookTitlesBorrowing;
    }

    public void setBookTitlesBorrowing(ArrayList<String> bookTitlesBorrowing) {
        this.bookTitlesBorrowing = bookTitlesBorrowing;
    }
}
