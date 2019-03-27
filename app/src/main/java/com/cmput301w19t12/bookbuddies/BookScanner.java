package com.cmput301w19t12.bookbuddies;

/**BookScanner class represents barcode scanning functionality
 * A scanned book has an ISBN
 *
 * MIGHT REFACTOR TO USE THIS BUT PROBABLY WON'T HAVE TIME
 *
 * @author Team 12
 * @version 1.0
 * */

public class BookScanner {
    private String ISBN;

    /**Base constructor*/
    BookScanner(){
        //Stuff
    }

    /**Get the scanned ISBN code*/
    public String getISBN(){
        return this.ISBN;
    }

    /**Scan the ISBN code on a book and return it
     * @return ISBN String*/
    public String scan(){

        return this.ISBN;
    }

}
