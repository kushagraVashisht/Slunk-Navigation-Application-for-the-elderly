package com.example.ritusharma.itproject.ChatModules;

import java.util.Date;

/**
 * Some code elements from here: https://github.com/eddydn/ChatApp In
 * particular, displaying messages correctly.
 *
 * Represents a message for easy writing and retrieval to and from the database.
 */
public class Message {

    private String senderID;
    private String recverID;
    private long timeSent;
    private String message;

    /**********************************************************************************************/

    /* Constructors */

    /**
     * Constructor to display dataSnapshot
     */
    public Message() {
        // Needed for Firebse
    }

    /*********************************************************************************************/

    /**
     * Message Constructor for retrieving a message (Takes a timestamp)
     * 
     * @param senderID - The sending User
     * @param recverID - The receiving User
     * @param timeSent - The time the message was sent (as a String)
     * @param message  - The message that was sent
     */
    public Message(String senderID, String recverID, long timeSent, String message) {
        this.senderID = senderID;
        this.recverID = recverID;
        this.timeSent = timeSent;
        this.message = message;
    }

    /*********************************************************************************************/

    /**
     * Message Constructor for creating a new message (Inserts a new timestamp from
     * user time)
     * 
     * @param senderID - The sending User
     * @param recverID - The receiving User
     * @param message  - The message that was sent
     */
    public Message(String senderID, String recverID, String message) {
        this.senderID = senderID;
        this.recverID = recverID;
        this.timeSent = new Date().getTime();
        this.message = message;
    }

    /**********************************************************************************************/

    /**
     * Tests Equality of 2 Messages
     * @param m2 - The message to compare against
     * @return - True if the messages are the same
     */
    public boolean equals(Message m2) {
        if(!this.senderID.equals(m2.getSenderID()) || !this.recverID.equals(m2.getRecverID())
            || !(this.timeSent == m2.getTimeSent()) || !this.message.equals(m2.getMessage())) {
            return false;
        }

        return true;
    }

    /**********************************************************************************************/

    /* Getters and Setters */

    /**
     * Gets the senderID object of the message.
     * 
     * @return The Sender
     */
    public String getSenderID() {
        return senderID;
    }

    /*********************************************************************************************/

    /**
     * Gets the receiver object of the message
     * 
     * @return The Receiver
     */
    public String getRecverID() {
        return recverID;
    }

    /*********************************************************************************************/

    /**
     * Gets the time the message was sent at
     * 
     * @return The time of sending via the senderID's local time
     */
    public long getTimeSent() {
        return timeSent;
    }

    /*********************************************************************************************/

    /**
     * Gets the message ;)
     * 
     * @return The Message
     */
    public String getMessage() {
        return message;
    }

    /*********************************************************************************************/

    /**
     * Sets the sender ID of the message
     * 
     * @param senderID - The ID of the Sender
     */
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    /*********************************************************************************************/

    /**
     * Sets the receiver ID of the message
     * 
     * @param recverID - The ID of the receiver
     */
    public void setRecverID(String recverID) {
        this.recverID = recverID;
    }

    /*********************************************************************************************/

    /**
     * Sets the time of the message
     * 
     * @param timeSent - The time the message was sent
     */
    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    /*********************************************************************************************/

    /**
     * Sets the text of the message
     * 
     * @param message - The message text!
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /*********************************************************************************************/
}
