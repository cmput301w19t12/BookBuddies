package com.cmput301w19t12.bookbuddies;

import java.util.ArrayList;

/**
 * Club class represents a club object
 * A Club contains an owner, has a name, a list of members,
 * a book the members are currently reading, and a list of club related events
 *
 * @author Team 12
 * @version 1.0*/

public class Club {
    private User owner;
    private String name;
    private ArrayList<User> membersList;
    private Book currentBook;
    private ArrayList<Event> events;

    public ArrayList<User> getMembersList() {
        return membersList;
    }

    public void setMembersList(ArrayList<User> membersList) {
        this.membersList = membersList;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public Club() {
        owner = null;
        name = null;
        membersList = null;
        currentBook = null;
        events = null;
    }

    /**Baseline constructor for the Club class
     * Assigns club owner and name, initializes both lists
     * @param owner User
     * @param name String*/
    Club(User owner, String name, ArrayList<User> membersList){
        this.owner = owner;
        this.name = name;
        this.membersList = membersList;
        this.events = new ArrayList<>();
    }

    /**Gets book being currently read by the club
     * @return currentBook Book*/
    public Book getCurrentBook() {
        return this.currentBook;
    }

    /**Sets the book that the club is reading
     * @param currentBook Book*/
    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    /**Gets a list of all members in a club
     * @return membersList ArrayList<User>*/
    public ArrayList<User> getMembers() {
        return this.membersList;
    }

    /**Adds a member to the club
     * @param member User*/
    public void addMember(User member) {
        this.membersList.add(member);
    }

    /**Removes a member from the group
     * @param user User*/
    public void deleteMember(User user){
        this.membersList.remove(user);
    }

    /**Gets name of the club
     * @return name String*/
    public String getName() {
        return this.name;
    }

    /**Sets the name of the club
     * @param name String*/
    public void setName(String name) {
        this.name = name;
    }

    /**Get the owner user of the club
     * @return owner User*/
    public User getOwner() {
        return this.owner;
    }

    /**Set the owner user of the club
     * @param owner User*/
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**Adds a new event to the club
     * @param newEvent Event*/
    public void createEvent(Event newEvent) {
        this.events.add(newEvent);
    }
    */
    /**Deletes an event from the club
     * @param event Event*/
    
    public void deleteEvent(Event event){
        this.events.remove(event);
    }
    
    /**Gets a list of all events associated with a club
     * @return events ArrayList<Event>*/
    
    public ArrayList<Event> getEvents(){
        return this.events;
    }
    
}
