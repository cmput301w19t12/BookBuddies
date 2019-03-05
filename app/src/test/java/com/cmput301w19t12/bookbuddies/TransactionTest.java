package com.cmput301w19t12.bookbuddies;

import com.google.firebase.database.Transaction;

import static junit.framework.TestCase.*;

public class TransactionTest {
	
	public void transactionTest(){
		User testUser = new User("username", "password");
		User testUser2 = new User("username2", "password2");
		User testUser3 = new User("username3", "password3");
		Book testBook = new Book();
		Book testBook2 = new Book();		
		Transaction testTransaction = new Transaction(testUser, testUser2, testBook);
		assertEquals(testTransaction.getOwner(), testUser);
		assertEquals(testTransaction.getBorrower(), testUser2);
		testTransaction.setOwner(testUser3);
		testTransaction.setBorrower(testUser);
		assertEquals(testTransaction.getOwner(), testUser3);
		assertEquals(testTransaction.getBorrower(), testUser);
		assertEquals(testTransaction.getBook(), testBook);	
		testTransaction.setBook(testBook2);
		assertEquals(testTransaction.getBook(), testBook2);			
		assertTrue(testTransaction.getTransactionDate() instanceof java.util.Date);
	}

}