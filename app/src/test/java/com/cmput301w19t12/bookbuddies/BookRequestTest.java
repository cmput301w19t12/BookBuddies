package com.cmput301w19t12.bookbuddies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class BookRequestTest {
    private BookRequest matchRequest;
    private BookDetails testingDetails;
    private String requesterId;
    private Book testBook;
    private String requestId;
    private String requesterUsername;
    private BookRequest request;

    @Before
    public void setUp() throws Exception {
        requesterId = "testRequesterId";;
        testingDetails = new BookDetails("title", "author",
                "isbn", "desc", "1234");
        testBook = new Book("ownerUsername", testingDetails, "status", "testBorrower");
        requestId = "testRequestId";;
        requesterUsername = "requesterUsername";;
        request = new BookRequest(requesterId, testBook, requestId, requesterUsername);
    }



    @Test
    public void createBookRequestTest() {
        assertEquals(request.getRequestedBook(),testBook);
        assertEquals(request.getRequesterID(), requesterId);
        assertEquals(request.getRequesterUsername(), requesterUsername);
        assertEquals(request.getRequestID(), requestId);
    }

//    @Test
//    public void sendTest() {
//        request.Send();
//        matchRequest = null;
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
//                                .child("BookRequests");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    BookRequest request = snapshot.getValue(BookRequest.class);
//                    if (equalsRequest(request)) {
//                        matchRequest = request;
//                        ref.child(snapshot.getKey()).removeValue();
//                    }
//                }
//                assertEquals(request, matchRequest);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private boolean equalsRequest(BookRequest request1) {
//        return request1.getRequesterID().equals(requesterId) && request1.getRequesterUsername().equals(requesterUsername)
//                && request1.getRequestID().equals(requestId) && equalsBook(request1.getRequestedBook());
//    }
//
//    private boolean equalsBook(Book book) {
//        BookDetails details = book.getBookDetails();
//        BookDetails testDetails = testBook.getBookDetails();
//        return details.getUniqueID().equals(testDetails.getUniqueID());
//    }
//
//    @Test
//    public void deleteRequestTest() {
//        request.Send();
//        request.deleteThisRequest();
//        matchRequest = null;
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
//                .child("BookRequests");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    BookRequest request = snapshot.getValue(BookRequest.class);
//                    if (equalsRequest(request)) {
//                        matchRequest = request;
//                    }
//                }
//                assertNull(matchRequest);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
