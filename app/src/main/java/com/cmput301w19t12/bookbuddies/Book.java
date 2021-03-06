package com.cmput301w19t12.bookbuddies;

/**Book represents a book object
 * A book has book details, an owner, and a borrow status
 *
 * @author Team 12
 * @version 1.0*/

public class Book {
    private String status;
    private BookDetails bookDetails;
    private String owner;
    private String currentBorrower;

    /**Base constructor for a book
     * @param owner String
     * @param bookDetails BookDetails
     * @param status String*/
    public Book(String owner, BookDetails bookDetails, String status, String currentBorrower){
        this.owner = owner;
        this.bookDetails = bookDetails;
        this.status = status;
        this.currentBorrower = currentBorrower;
    }

    public Book() {
        this.status = null;
        this.bookDetails = null;
        this.status = null;
        this.currentBorrower = null;
    }

    /**returns the current borrower of this book
     * @return currentBorrower String*/
    public String getCurrentBorrower() {
        return currentBorrower;
    }

    /**sets the current borrower of this book
     * @param currentBorrower String*/
    public void setCurrentBorrower(String currentBorrower) {
        this.currentBorrower = currentBorrower;
    }

    /**Gets owner name
     * @return owner String*/
    public String getOwner() {
        return this.owner;
    }

    /**Sets owner of the book
     * @param owner String*/
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**Gets book details
     * @return bookDetails*/
    public BookDetails getBookDetails() {
        return this.bookDetails;
    }

    /**Sets the details of the book
     * @param bookDetails BookDetails*/
    public void setBookDetails(BookDetails bookDetails) {
        this.bookDetails = bookDetails;
    }

    /**Gets the borrowing status of the book
     * @return status String*/
    public String getStatus() {
        return this.status;
    }

    /**Sets borrowing status of the book
     * @param status String*/
    public void setStatus(String status) {
        this.status = status;
    }
}
