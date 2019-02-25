package com.cmput301w19t12.bookbuddies;
/**
  * This Unit Test includes all necessary checks for Book class.
  * Tests for validity of the books we are adding to or drawing from the library.
  * <p>
  * @param String: Whichever field we are testing. Examples: Owner, Status...
  * @return Boolean: Correct or Incorrect.
  */

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


@Test
public void Book_Insertion_Check(String testOwner, String testStatus){ //Tests whether our setting methods are doing what they are supposed to do, as well as the functionality of getters().
	Book book = new Book();
	book.setOwner(testOwner); 
	book.setStatus(testStatus);
	assertEquals(book.getOwner(), testOwner);
	assertEquals(book.getStatus(), testStatus);
}

/**
  * This Unit Test extends Book class and tests methods in BookDetails class.
  * Tests for its constructor's structure.
  * In any case, "title, author, ISBN, description" are required.
  * "Picture and Genre" have defaults. 
  * <p>
  * @param String: any existing private variables from BookDetails class.
  * ...
  * @return Boolean: Correct or Incorrect.
  */
class BookTest {
	@Test
	public void Book_Basic_Details_Check(String title, String author, String description, String Picture, String Genre) {
		if (Picture != "") {
			private final String pic = Picture;
			Book_Pic_Details_Check(pic);
		} else {
			private String pic = "NoImage";
		}
		if (Genre != "") {
			private final String gen = Genre;
			Book_Genre_Details_Check(gen);
		} else {
			private String gen = "Not Specified";
		}
		BookDetails details = new BookDetails();
		//Basic matching checks
		details.setTitle(title);
		details.setAuthor(author);
		details.setDescription(description);
		assertEquals(details.getTitle(), title);
		assertEquals(details.getAuthor(), author);
		assertEquals(details.getDescription(), description);
// This test extends from Book_Basic_Details_Check by implementing advanced matching checks on nullable parameters. 
		@Test
		public void Book_Genre_Details_Check (String gen){
			BookDetails details = new BookDetails();
			details.setGenre(gen);
			assertEquals(details.getGenre(), gen);
		}
//This test also extends from Book_Basic_Details_Check by implementing advanced matching checks on nullable parameters.
		@Test
		public void Book_Img_Details_Check (String pic)
	}
}