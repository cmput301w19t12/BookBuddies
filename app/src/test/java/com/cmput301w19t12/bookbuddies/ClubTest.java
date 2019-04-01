package com.cmput301w19t12.bookbuddies;
import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
public class ClubTest {

	User testUser = new User("username", "password");
	String ID = "3";
	Club testClub = new Club(testUser, "clubname",new ArrayList<User>(),ID);
	User testUser2 = new User("username2", "password2");

	@Test
	public void ownerTest() {
		assertEquals(testClub.getOwner(), testUser);
		testClub.setOwner(testUser2);
		assertEquals(testClub.getOwner(), testUser2);
	}

	@Test
	public void memberTest() {
		User testUser1 = new User("username1", "password1",
									"780-111-1111", "testUser1@gmail.com",
								null);
		User testUser2 = new User("username2", "password2",
									"780-222-2222", "testUser2@gmail.com", null);

		testClub.getMembersList().add(testUser1);
		testClub.getMembersList().add(testUser2);
		assertEquals(testClub.getMembersList().get(0), testUser1);
		assertEquals(testClub.getMembersList().get(1), testUser2);
	}

	@Test
	public void clubIDTest() {
		String testId = "1234";
		testClub.setClubID(testId);
		assertEquals(testClub.getClubID(), testId);
	}

	@Test
	public void chatTest() {
		Chat chat = new Chat();
		testClub.setGroupChat(chat);
		assertEquals(testClub.getGroupChat(), chat);
	}

	@Test
	public void clubNameTest() {
		assertEquals(testClub.getName(), "clubname");
		testClub.setName("newclubname");
		assertEquals(testClub.getName(), "newclubname");
	}

	@Test
	public void currentBookTest() {
		String bookTitle = "testBook";
		testClub.setCurrentBook(bookTitle);
		assertEquals(bookTitle, testClub.getCurrentBook());
	}


	@Test
    public void IDTest(){
	    assertEquals(testClub.getClubID(),ID);
	    testClub.setClubID("5");
	    assertEquals(testClub.getClubID(),"5");
    }
}

