package com.cmput301w19t12.bookbuddies;

/**Book represents a book object
 * A book has book details, an owner, and a borrow status
 *
 * @author bgrenier
 * @version 1.0*/

public class Book {
    private String status;
    private BookDetails bookDetails;
    private String owner;

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

    public void setBookDetails(BookDetails bookDetails) {
        this.bookDetails = bookDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
