package com.cmput301w19t12.bookbuddies;

import android.location.Location;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import static junit.framework.TestCase.*;

public class TransactionTest {
    User testUser = new User("username", "password");
    User testUser2 = new User("username2", "password2");
    User testUser3 = new User("username3", "password3");
    Book testBook = new Book("Owner",new BookDetails("","","","",""),"Available","");
    Book testBook2 = new Book("Owner2",new BookDetails("","","","",""),"Available","");
    Location location = new Location("");
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    Transaction testTransaction = new Transaction(testUser,testUser2,testBook,"","");

    @Test
	public void ownerTest() {
        assertEquals(testTransaction.getOwner().getUsername(), testUser.getUsername());
    }

    @Test
    public void borrowerTest() {
        assertEquals(testTransaction.getBorrower().getUsername(), testUser2.getUsername());
    }

    @Test
    public void getterSetterTest(){
		testTransaction.setOwner(testUser3);
		testTransaction.setBorrower(testUser);
		assertEquals(testTransaction.getOwner().getUsername(), testUser3.getUsername());
		assertEquals(testTransaction.getBorrower().getUsername(), testUser.getUsername());
		assertEquals(testTransaction.getBook().getOwner(), testBook.getOwner());
		testTransaction.setBook(testBook2);
		assertEquals(testTransaction.getBook().getOwner(), testBook2.getOwner());
		Assert.assertEquals(testTransaction.getTime(),date);
	}

	@Test
    public void transactionIdTest() {
        String transactionID = "TransactionID";
        testTransaction.setTransactionID(transactionID);
        assertEquals(testTransaction.getTransactionID(), transactionID);
    }

    @Test
    public void transactionTypeTest() {
        String transactionType = "TransactionType";
        testTransaction.setTransactionType(transactionType);
        assertEquals(testTransaction.getTransactionType(), transactionType);
    }

}