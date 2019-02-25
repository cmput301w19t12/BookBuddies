package com.cmput301w19t12.bookbuddies;

import java.util.ArrayList;

public class Club {
    private User owner;
    private String name;
    private ArrayList<User> membersList;
    private Book currentBook;

    Club(User owner, String name){
        this.owner = owner;
        this.name = name;
        membersList = new ArrayList<>();
    }

    public Book getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    public ArrayList<User> getMembers() {
        return membersList;
    }

    public void addMember(User member) {
        membersList.add(member);
    }
    public void deleteMember(String name){
        membersList.remove(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        owner = owner;
    }
}
