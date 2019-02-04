package com.example.ritusharma.itproject.ChatModules;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Tests for Message Object
 * 
 * @author Luke Hedt
 */
public class MessageTest {


    private Message validMessage;
    private Message nullMessage;
    private String validSender  = "SenderID";
    private String validRecvr   = "RecvrID";
    private long validTimeSent  = 11111111;
    private String validText    = "Wednesday is growing up so fast, she'll be Thursday before we know it!";

    @Before
    public void before() {
        validMessage = new Message(validSender, validRecvr, validTimeSent, validText);
        nullMessage = new Message(null, null, 0, null);
    }

    @Test
    public void equalsTrue() {
        Message validMessage2 = new Message(validSender, validRecvr, validTimeSent, validText);
        assertTrue(validMessage.equals(validMessage2));
    }

    @Test
    public void equalsFalseOnSender() {
        Message validMessage2 = new Message("WrongSender", validRecvr, validTimeSent, validText);
        assertFalse(validMessage.equals(validMessage2));
    }

    @Test
    public void equalsFalseOnRecver() {
        Message validMessage2 = new Message(validSender, "WrongRecver", validTimeSent, validText);
        assertFalse(validMessage.equals(validMessage2));
    }

    @Test
    public void equalsFalseOnTime() {
        Message validMessage2 = new Message(validSender, validRecvr, 111, validText);
        assertFalse(validMessage.equals(validMessage2));
    }

    @Test
    public void equalsFalseOnText() {
        Message validMessage2 = new Message(validSender, validRecvr, validTimeSent, "This Message is Wrong");
        assertFalse(validMessage.equals(validMessage2));
    }

    @Test
    public void equalsFalseOnNullMessage() {
        assertFalse(validMessage.equals(nullMessage));
    }

    @Test
    public void getSenderIDValid() {
        String actual = validMessage.getSenderID();
        String expected = validSender;
        assertEquals(expected, actual);
    }

    @Test
    public void getSenderIDNull() {
        String output = nullMessage.getSenderID();
        assertNull("Sender wasn't Null!", output);
    }

    @Test
    public void getRecverIDValid() {
        String actual = validMessage.getRecverID();
        String expected = validRecvr;
        assertEquals(expected, actual);
    }

    @Test
    public void getRecverIDNull() {
        String output = nullMessage.getRecverID();
        assertNull("Receiver wasn't Null!", output);
    }

    @Test
    public void getTimeSentValid() {
        long actual = validMessage.getTimeSent();
        long expected = validTimeSent;
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeSentZero() {
        long output = nullMessage.getTimeSent();
        long expected = 0;
        assertEquals(expected, output);
    }

    @Test
    public void getMessageValid() {
        String actual = validMessage.getMessage();
        String expected = validText;
        assertEquals(expected, actual);
    }

    @Test
    public void getMessageNull() {
        String output = nullMessage.getMessage();
        assertNull("Message wasn't Null!", output);
    }

    @Test
    public void setSenderIDValid() {
        String newValidSender = "NewSenderID";
        validMessage.setSenderID(newValidSender);
        assertEquals(newValidSender, validMessage.getSenderID());
    }

    @Test
    public void setSenderIDNull() {
        String newValidSender = null;
        validMessage.setSenderID(newValidSender);
        assertNull("Sender Wasn't Null!", validMessage.getSenderID());
    }

    @Test
    public void setRecverIDValid() {
        String newValidRecver = "NewRecverID";
        validMessage.setRecverID(newValidRecver);
        assertEquals(newValidRecver, validMessage.getRecverID());
    }

    @Test
    public void setRecverIDNull() {
        String newValidRecver = null;
        validMessage.setRecverID(newValidRecver);
        assertNull("Receiver wasn't null!", validMessage.getRecverID());
    }

    @Test
    public void setTimeSent() {
        long newValidTimeSent = 2222222;
        validMessage.setTimeSent(newValidTimeSent);
        assertEquals(newValidTimeSent, validMessage.getTimeSent());
    }

    @Test
    public void setMessageValid() {
        String newValidText = "This is a valid text";
        validMessage.setMessage(newValidText);
        assertEquals(newValidText, validMessage.getMessage());
    }

    @Test
    public void setMessageNull() {
        String newValidText = null;
        validMessage.setMessage(newValidText);
        assertNull("Message not null!", validMessage.getMessage());
    }
}
