package com.cmput301w19t12.bookbuddies;
import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import org.junit.Test;
public class ClubTest {

	User testUser = new User("username", "password");
	String ID = "3";
	Club testClub = new Club(testUser, "clubname",new ArrayList<User>(),ID);
	User testUser2 = new User("username2", "password2");
	Location testLocation = new Location("");
	Calendar cal = Calendar.getInstance();
	Date date = cal.getTime();
	Event testEvent = new Event(testLocation,date);

	@Test
	public void ownerTest() {
		assertEquals(testClub.getOwner(), testUser);
		testClub.setOwner(testUser2);
		assertEquals(testClub.getOwner(), testUser2);
	}

	@Test
	public void clubNameTest() {
		assertEquals(testClub.getName(), "clubname");
		testClub.setName("newclubname");
		assertEquals(testClub.getName(), "newclubname");
	}


	@Test
	public void bookTest() {
		assertEquals(testClub.getCurrentBook(), null);
		Book testBook = new Book();
		testClub.setCurrentBook(testBook);
		assertEquals(testClub.getCurrentBook(), testBook);
	}

	@Test
	public void eventsTest(){
		testClub.createEvent(testEvent);
		assertEquals(testClub.getEvents().size(), 1);
		testClub.deleteEvent(testEvent);
		assertEquals(testClub.getEvents().size(), 0);
	}

	@Test
    public void IDTest(){
	    assertEquals(testClub.getClubID(),ID);
	    testClub.setClubID("5");
	    assertEquals(testClub.getClubID(),"5");
    }
}

