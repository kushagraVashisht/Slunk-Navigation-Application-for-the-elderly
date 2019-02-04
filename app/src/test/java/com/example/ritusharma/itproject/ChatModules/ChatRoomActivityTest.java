package com.example.ritusharma.itproject.ChatModules;

import com.example.ritusharma.itproject.Auth_SignIn.CaringStatus;
import com.example.ritusharma.itproject.Auth_SignIn.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


/**
 * Based partly on:
 * https://github.com/FrangSierra/RxFirebase/blob/master/app/src/test/java/durdinapps/rxfirebase2/RxFirebaseDatabaseTest.java
 */
public class ChatRoomActivityTest {

    private HashMap<String, Message> messageMap;

    private UserObj thisUser;
    private UserObj otherUser;
    private UserObj outOfScopeUser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        thisUser = new UserObj("ThisUserID", "TestChick1", "test1@gmail.com", "Diane", "Nguyen",
                CaringStatus.valueOf("CARER"), "+16505551234");

        otherUser = new UserObj("OtherUserID", "TestDude1", "test2@gmail.com", "Mister", "PeanutButter",
                CaringStatus.CARED_FOR, "+16804655512");

        outOfScopeUser = new UserObj("OutOfScopeUserID", "TestHorse1", "test10@gmail.com", "BoJack", "HorseMan",
                CaringStatus.CARED_FOR, "+16468955598");

        createDataSnapshotMocks();

    }

    /**
     * Creates a mock of useable data to test on
     */
    private void createDataSnapshotMocks() {
        ArrayList<Message> messageList = new ArrayList<>();
        messageMap = new HashMap<>();
        /* Hash keys don't matter but they can't clash */
        Random r = new Random();

        Message message = new Message(thisUser.getUID(), otherUser.getUID(), 1111111, "Hey, how's it going?");
        messageList.add(message);
        messageMap.put(Integer.valueOf(r.nextInt(1000000)).toString(), message);

        message = new Message(otherUser.getUID(), thisUser.getUID(), 1111113, "Grreat! Never been better! *Pants*");
        messageList.add(message);
        messageMap.put(Integer.valueOf(r.nextInt(1000000)).toString(), message);

        message = new Message(thisUser.getUID(), outOfScopeUser.getUID(), 1111115, "Are you doing okay BoJack?");
        messageList.add(message);
        messageMap.put(Integer.valueOf(r.nextInt(1000000)).toString(), message);

        message = new Message(outOfScopeUser.getUID(), thisUser.getUID(), 1111117, "Suuuuuure");
        messageList.add(message);
        messageMap.put(Integer.valueOf(r.nextInt(1000000)).toString(), message);
    }

    /**
     * Test that the filter is only getting the messages between this user and the selected friend
     */
    @Test
    public void filterUserMessagesTrue() {
        ArrayList<Message> filteredMessages = ChatRoomActivity.filterUserMessages(messageMap, thisUser, otherUser);

        ArrayList<Message> expected = new ArrayList<>();
        expected.add(new Message(thisUser.getUID(), otherUser.getUID(), 1111111, "Hey, how's it going?"));
        expected.add(new Message(otherUser.getUID(), thisUser.getUID(), 1111113, "Grreat! Never been better! *Pants*"));


        boolean equalLists = true;
        for(int i = 0; i < expected.size(); i++) {
            for(int j = 0; j <= filteredMessages.size(); j++) {
                if(j == filteredMessages.size()) {
                    equalLists = false;
                }
                if(expected.get(i).equals(filteredMessages.get(j))) {
                    break;
                }
            }
        }

        assertTrue(equalLists);
        assertEquals(expected.size(), filteredMessages.size());
    }

    @Test
    public void filterUserMessagesFalseOnContent() {
        ArrayList<Message> filteredMessages = ChatRoomActivity.filterUserMessages(messageMap, thisUser, otherUser);

        ArrayList<Message> wrong = new ArrayList<>();
        wrong.add(new Message(thisUser.getUID(), outOfScopeUser.getUID(), 1111111, "Hey, how's it going?"));
        wrong.add(new Message(otherUser.getUID(), thisUser.getUID(), 1111113, "Grreat! Never been better! *Pants*"));


        boolean equalLists = true;
        for(int i = 0; i < wrong.size(); i++) {
            for(int j = 0; j <= filteredMessages.size(); j++) {
                if(j == filteredMessages.size()) {
                    equalLists = false;
                    break;
                }
                if(wrong.get(i).equals(filteredMessages.get(j))) {
                    break;
                }
            }
        }
        assertFalse(equalLists);
    }

    @Test
    public void filterUserMessagesFalseOnSize() {
        ArrayList<Message> filteredMessages = ChatRoomActivity.filterUserMessages(messageMap, thisUser, otherUser);

        ArrayList<Message> wrong = new ArrayList<>();
        wrong.add(new Message(thisUser.getUID(), otherUser.getUID(), 1111111, "Hey, how's it going?"));
        wrong.add(new Message(otherUser.getUID(), thisUser.getUID(), 1111113, "Grreat! Never been better! *Pants*"));
        wrong.add(new Message(otherUser.getUID(), outOfScopeUser.getUID(), 1111113, "This Message Shouldn't Be Here"));

        assertNotEquals(wrong.size(), filteredMessages.size());
    }

}