package com.cmput301w19t12.bookbuddies;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookAndBookDetailsTest {
    String title = "HarryPotter";
    String author = "J.K Rowling";
    String ISBN = "234";
    String description = "This is a book";
    String ID = "d235234t";
    BookDetails testBookDetatails = new BookDetails(title,author,ISBN,description,ID);
    String owner = "Me";
    String status = "available";
    String borrower = "John";
    Book testBook = new Book(owner,testBookDetatails,status,borrower);

    @Test
    public void getDetailsTest(){
        BookDetails details = testBook.getBookDetails();
        assertEquals(details.getTitle(),title);
        assertEquals(details.getAuthor(),author);
        assertEquals(details.getISBN(),ISBN);
        assertEquals(details.getDescription(),description);
        assertEquals(details.getUniqueID(),ID);

    }

    @Test
    public void setDetailsTest(){
        title = "50 Shades";
        author = "author";
        ISBN = "234534";
        description = "different book";
        ID = "325435";
        String path = "photos/bookPic.jpeg";
        BookDetails details = new BookDetails(title,author,ISBN,description,ID);
        details.setPicturePath(path);
        testBook.setBookDetails(details);
        assertEquals(details.getTitle(),title);
        assertEquals(details.getAuthor(),author);
        assertEquals(details.getISBN(),ISBN);
        assertEquals(details.getDescription(),description);
        assertEquals(details.getUniqueID(),ID);
        assertEquals(testBook.getBookDetails().getPicturePath(), path);
    }

    @Test
    public void getBookTest(){
        assertEquals(testBook.getOwner(),owner);
        assertEquals(testBook.getStatus(),status);
        assertEquals(testBook.getCurrentBorrower(),borrower);
    }

    @Test
    public void setBookTest(){
        owner = "new owner";
        status = "Borrowed";
        borrower = "Dallas";
        testBook.setOwner(owner);
        testBook.setStatus(status);
        testBook.setCurrentBorrower(borrower);
        assertEquals(testBook.getOwner(),owner);
        assertEquals(testBook.getStatus(),status);
        assertEquals(testBook.getCurrentBorrower(),borrower);

    }
}
