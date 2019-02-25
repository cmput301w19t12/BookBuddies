public class ClubTest {

	Club testClub = new Club(testUser, "clubname");
	
	public void ownerTest(){
		User testUser = new User("username", "password");
		Club testClub = new Club(testUser, "clubname");
		assertEquals(testClub.getOwner(), testUser);
		User testUser2 = new User("username2", "password2");
		testClub.setOwner(testUser2);
		assertEquals(testClub.getOwner(), testUser2);	
	}
	
	public void clubNameTest(){
		User testUser = new User("username", "password");
		Club testClub = new Club(testUser, "clubname");
		assertEquals(testClub.getName(), "clubname");
		testClub.setName("newclubname");
		assertEquals(testClub.getName(), "newclubname");	
	}
	
	public void membersTest(){
		User testUser = new User("username", "password");
		User testUser2 = new User("username2", "password2");
		Club testClub = new Club(testUser, "clubname");
		assertTrue(testClub.getMembers().contains(testUser));
		testClub.addMember(testUser2);
		assertEquals(testClub.getMembers().size(), 2);
		assertTrue(testClub.getMembers().contains(testUser2));
		testClub.deleteMember("username2");
		assertEquals(testClub.getMembers().size(), 1);
		assertFalse(testClub.getMembers().contains(testUser2));		
	}
	
	public void bookTest(){
		User testUser = new User("username", "password");
		Club testClub = new Club(testUser, "clubname");
		assertEquals(testClub.getCurrentBook(), void);
		Book testBook = new Book;
		testClub.setCurrentBook(testBook);
		assertEquals(testClub.getCurrentBook(), testBook);
	}
	
	public void eventsTest(){
		User testUser = new User("username", "password");
		Club testClub = new Club(testUser, "clubname");
		assertEquals(testClub.getEvents().size(), 0);
		Location testLocation = new Location();
		Date testTime = new Date();
		testClub.createEvent(testLocation, testTime);
		assertEquals(testClub.getEvents().size(), 1);
		testClub.deleteEvent(1);
		assertEquals(testClub.getEvents().size(), 0);
	}

}