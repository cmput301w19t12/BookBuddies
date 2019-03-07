package com.cmput301w19t12.bookbuddies;

import java.util.ArrayList;

public class TitleContainer {
    private ArrayList<String> bookTitlesAvailable;
    private ArrayList<String> BookTitlesBorrowing;

    public ArrayList<String> getBookTitlesAvailable() {
        return bookTitlesAvailable;
    }

    public void setBookTitlesAvailable(ArrayList<String> bookTitlesAvailable) {
        this.bookTitlesAvailable = bookTitlesAvailable;
    }

    public ArrayList<String> getBookTitlesBorrowing() {
        return BookTitlesBorrowing;
    }

    public void setBookTitlesBorrowing(ArrayList<String> bookTitlesBorrowing) {
        BookTitlesBorrowing = bookTitlesBorrowing;
    }
}
