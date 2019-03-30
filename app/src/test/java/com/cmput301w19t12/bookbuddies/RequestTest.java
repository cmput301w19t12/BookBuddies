package com.cmput301w19t12.bookbuddies;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestTest {
    String requesterID = "12345";
    Book requestedBook = new Book();
    String requestID = "23421";
    String username = "user";
    BookRequest request = new BookRequest(requesterID,requestedBook,requestID,username);

    @Test
    public void requesterIDTest(){
        assertEquals(request.getRequesterID(),requesterID);
        request.setRequesterID("123");
        assertEquals(requesterID,"123");
    }
}
