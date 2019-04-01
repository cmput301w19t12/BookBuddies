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
        assertEquals(request.getRequesterID(),"123");
    }

    @Test
    public void requestedBookTest(){
        request.getRequestedBook().setOwner("Owner");
        assertEquals(request.getRequestedBook().getOwner(),"Owner");
    }

    @Test
    public void requestIDTest(){
        assertEquals(request.getRequestID(),requestID);
        request.setRequestID("789");
        assertEquals(request.getRequestID(),"789");
    }

    @Test
    public void requesterUsernameTest(){
        assertEquals(request.getRequesterUsername(),username);
        request.setRequesterUsername("Tony1");
        assertEquals(request.getRequesterUsername(),"Tony1");
    }
}
