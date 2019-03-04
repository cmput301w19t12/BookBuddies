package com.cmput301w19t12.bookbuddies;


/**Represents the details of an associated book
 * A set of book details contains a title, author, picture path, description and genre
 *
 * @author Team 12
 * @version 1.0*/

public class BookDetails {

    private String title;
    private String author;
    private String picturePath;
    private String description;
    private String genre;
    private String ISBN;

    /**Base constructor for book details
     * @param title String
     * @param author String
     * @param ISBN String
     * @param description String*/
    BookDetails(String title, String author, String ISBN, String description){
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.description = description;
    }

    /**Gets the genre of the book
     * @return genre String*/
    public String getGenre() {
        return this.genre;
    }

    /**Sets the genre of the book
     * @param genre String*/
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**Gets the description of the book
     * @return description String */
    public String getDescription() {
        return this.description;
    }

    /**Sets the description for the book
     * @param description String*/
    public void setDescription(String description) {
        this.description = description;
    }

    /**Gets the picture path for the book
     * @return picturePath String*/
    public String getPicturePath() {
        return this.picturePath;
    }

    /**Sets the picture path for the book
     * @param picturePath String*/
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**Gets the author for the book
     * @return author String*/
    public String getAuthor() {
        return this.author;
    }

    /**Sets the author for the book
     * @param author String*/
    public void setAuthor(String author) {
        this.author = author;
    }

    /**Gets the title for the book
     * @return title String*/
    public String getTitle() {
        return this.title;
    }

    /**Sets the title for the book
     * @param title String*/
    public void setTitle(String title) {
        this.title = title;
    }

}
