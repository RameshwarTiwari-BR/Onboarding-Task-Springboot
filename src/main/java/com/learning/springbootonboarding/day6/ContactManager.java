package com.learning.springbootonboarding.day6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactManager {

    // Stores Contact ID as key and Contact object as value
    private final HashMap<Integer, Contact> contacts = new HashMap<>();

    // Add a new contact
    public void createContact(Contact contact) {

        int id = contact.getId();

        // Check whether ID already exists
        if (contacts.containsKey(id)) {
            throw new IllegalArgumentException("Contact ID already exists: " + id);
        }

        // Add contact to HashMap
        contacts.put(id, contact);
    }

    // Find contact using ID
    public Contact getContactById(int id) {
        return contacts.get(id);
    }

    // Get all contacts
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts.values());
    }

    // Delete contact using ID
    public void deleteContact(int id) {

        if (!contacts.containsKey(id)) {
            throw new IllegalArgumentException("Contact not found: " + id);
        }

        contacts.remove(id);
    }

    // Check whether contact exists
    public boolean contactExists(int id) {
        return contacts.containsKey(id);
    }
}