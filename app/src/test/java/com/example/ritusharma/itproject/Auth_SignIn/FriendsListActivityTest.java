package com.example.ritusharma.itproject.Auth_SignIn;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FriendsListActivityTest {

    private UserObj thisUser;
    private ArrayList<String> confirmedFriendIDs;
    private ArrayList<UserObj> confirmedFriendObjs;
    private HashMap<String, UserObj> allUsers;
    private HashMap<String, ArrayList<String>> friendsNode;

    private UserObj friend1;
    private UserObj friend2;
    private UserObj non_friend1;
    private UserObj non_friend2;

    @Before
    public void setUp() {
        confirmedFriendIDs = new ArrayList<>();
        confirmedFriendObjs = new ArrayList<>();
        friendsNode = new HashMap<>();
        allUsers = new HashMap<>();

        thisUser = new UserObj("User00", "TheHandMan", "test12@gmail.com", "Handy", "Tooood",
                CaringStatus.CARED_FOR, "+16488956598");
        friend1 = new UserObj("User01", "TestChick1", "test1@gmail.com", "Diane", "Nguyen",
                CaringStatus.valueOf("CARER"), "+16505551234");
        friend2 = new UserObj("User02", "TestDude1", "test2@gmail.com", "Mister", "PeanutButter",
                CaringStatus.CARED_FOR, "+16804655512");
        non_friend1 = new UserObj("User03", "TestHorse1", "test10@gmail.com", "BoJack", "HorseMan",
                CaringStatus.CARED_FOR, "+16468955598");
        non_friend2 = new UserObj("User04", "TestZebra1", "test11@gmail.com", "BoJingles", "ZebraGirl",
                CaringStatus.CARED_FOR, "+16468956598");


        allUsers.put(thisUser.getUID(), thisUser);
        allUsers.put(friend1.getUID(), friend1);
        allUsers.put(friend2.getUID(), friend2);
        allUsers.put(non_friend1.getUID(), non_friend1);
        allUsers.put(non_friend2.getUID(), non_friend2);

        confirmedFriendObjs.add(friend1);
        confirmedFriendObjs.add(friend2);

        for(int i = 0; i < confirmedFriendObjs.size(); i++) {
            confirmedFriendIDs.add(confirmedFriendObjs.get(i).getUID());
        }

        friendsNode.put(thisUser.getUID(), confirmedFriendIDs);

        ArrayList<String> friendCloister1 = new ArrayList<>();
        friendCloister1.add(thisUser.getUID());
        friendCloister1.add(non_friend2.getUID());
        friendsNode.put(friend1.getUID(), friendCloister1);

        ArrayList<String> friendCloister2 = new ArrayList<>();
        friendCloister2.add(thisUser.getUID());
        friendCloister2.add(non_friend1.getUID());
        friendsNode.put(friend2.getUID(), friendCloister2);

        ArrayList<String> friendCloister3 = new ArrayList<>();
        friendCloister3.add(friend2.getUID());
        friendCloister3.add(non_friend2.getUID());
        friendsNode.put(non_friend1.getUID(), friendCloister3);

        ArrayList<String> friendCloister4 = new ArrayList<>();
        friendCloister4.add(friend1.getUID());
        friendCloister4.add(non_friend1.getUID());
        friendsNode.put(non_friend2.getUID(), friendCloister4);
    }

    @Test
    public void filterUsersTrue() {
        ArrayList<UserObj> filteredUsers = FriendsListActivity.filterUsers(allUsers, confirmedFriendIDs);

        boolean equalLists = true;
        for(int i = 0; i < confirmedFriendObjs.size(); i++) {
            for(int j = 0; j <= filteredUsers.size(); j++) {
                if(j == filteredUsers.size()) {
                    equalLists = false;
                }
                if(confirmedFriendObjs.get(i).equals(filteredUsers.get(j))) {
                    break;
                }
            }
        }

        assertTrue(equalLists);
        assertEquals(confirmedFriendObjs.size(), filteredUsers.size());
    }

    @Test
    public void filterUserMessagesFalseOnContent() {
        ArrayList<UserObj> filteredMessages = FriendsListActivity.filterUsers(allUsers, confirmedFriendIDs);

        ArrayList<UserObj> wrong = new ArrayList<>();
        wrong.add(new UserObj("User01", "TestChick1", "test1@gmail.com", "Diane", "Nguyen",
                CaringStatus.valueOf("CARER"), "+16505551234"));
        wrong.add(new UserObj("User03", "TestHorse1", "test10@gmail.com", "BoJack", "HorseMan",
                CaringStatus.CARED_FOR, "+16468955598"));


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
        ArrayList<UserObj> filteredMessages = FriendsListActivity.filterUsers(allUsers, confirmedFriendIDs);

        ArrayList<UserObj> wrong = new ArrayList<>();
        wrong.add(new UserObj("User01", "TestChick1", "test1@gmail.com", "Diane", "Nguyen",
                CaringStatus.valueOf("CARER"), "+16505551234"));
        wrong.add(new UserObj("User02", "TestDude1", "test2@gmail.com", "Mister", "PeanutButter",
                CaringStatus.CARED_FOR, "+16804655512"));
        wrong.add(new UserObj("User03", "TestHorse1", "test10@gmail.com", "BoJack", "HorseMan",
                CaringStatus.CARED_FOR, "+16468955598"));

        assertNotEquals(wrong.size(), filteredMessages.size());
    }

    @Test
    public void getMutualFriendsTrue() {
        ArrayList<String> mutualFriends = FriendsListActivity.getMutualFriends(friendsNode, thisUser.getUID());

        assertEquals(confirmedFriendIDs, mutualFriends);
        assertEquals(confirmedFriendIDs.size(), mutualFriends.size());
    }

    @Test
    public void getMutualFriendsFalseOnContent() {
        ArrayList<String> mutualFriends = FriendsListActivity.getMutualFriends(friendsNode, thisUser.getUID());
        ArrayList<String> wrongFriendIDs = new ArrayList<>();

        wrongFriendIDs.add(friend1.getUID());
        wrongFriendIDs.add(non_friend1.getUID());

        assertNotEquals(wrongFriendIDs, mutualFriends);
    }

    @Test
    public void getMutualFriendsFalseOnSize() {
        ArrayList<String> mutualFriends = FriendsListActivity.getMutualFriends(friendsNode, thisUser.getUID());
        ArrayList<String> wrongFriendIDs = new ArrayList<>();

        wrongFriendIDs.add(friend1.getUID());
        wrongFriendIDs.add(friend2.getUID());
        wrongFriendIDs.add(non_friend1.getUID());

        assertNotEquals(wrongFriendIDs.size(), mutualFriends.size());
    }
}