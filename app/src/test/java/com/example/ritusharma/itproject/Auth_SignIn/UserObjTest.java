package com.example.ritusharma.itproject.Auth_SignIn;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserObjTest {

    private UserObj validUser;
    private UserObj nullUser;
    private String validUID         = "User ID";
    private String validUsername    = "ThursdayBrazil";
    private String validEmail       = "test@gmail.com";
    private String validFname       = "Thursday";
    private String validLname       = "Brazil";
    private String validCaringStat  = "CARER";
    private String validMobNum      = "+16505551234";

    @Before
    public void setUp() throws Exception {
        validUser = new UserObj(
            validUID,
            validUsername,
            validEmail,
            validFname,
            validLname,
            CaringStatus.valueOf(validCaringStat),
            validMobNum
        );

        nullUser = new UserObj(
                null, null, null, null, null, CaringStatus.CARED_FOR, null
        );
    }

    @Test
    public void equalsTrue() {
        UserObj validUser2 = new UserObj(validUID, validUsername, validEmail, validFname, validLname,
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertTrue(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnUID() {
        UserObj validUser2 = new UserObj("DifferingUID", validUsername, validEmail, validFname, validLname,
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnUserName() {
        UserObj validUser2 = new UserObj(validUID, "NotMyUserName", validEmail, validFname, validLname,
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnEmail() {
        UserObj validUser2 = new UserObj(validUID, validUsername, "DifferingEmail", validFname, validLname,
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnFname() {
        UserObj validUser2 = new UserObj(validUID, validUsername, validEmail, "NotMyName", validLname,
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnLname() {
        UserObj validUser2 = new UserObj(validUID, validUsername, validEmail, validFname, "NotMyName",
                CaringStatus.valueOf(validCaringStat), validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnCaringStat() {
        UserObj validUser2 = new UserObj(validUID, validUsername, validEmail, validFname, validLname,
                CaringStatus.CARED_FOR, validMobNum);
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnMobNum() {
        UserObj validUser2 = new UserObj(validUID, validUsername, validEmail, validFname, validLname,
                CaringStatus.valueOf(validCaringStat), "+61455545248");
        assertFalse(validUser.equals(validUser2));
    }

    @Test
    public void equalsFalseOnNullUser() {
        assertFalse(validUser.equals(nullUser));
    }

    @Test
    public void getUIDValid() {
        assertEquals(validUID, validUser.getUID());
    }

    @Test
    public void getUIDNull() {
        assertNull("UID Not Null!", nullUser.getUID());
    }

    @Test
    public void setUIDValid() {
        String newUid = "NewUserID";
        validUser.setUID(newUid);
        assertEquals(newUid, validUser.getUID());
    }

    @Test
    public void setUIDNull() {
        validUser.setUID(null);
        assertNull("NewUID Wasn't Null!", validUser.getUID());
    }

    @Test
    public void getUsernameValid() {
        assertEquals(validUsername, validUser.getUsername());
    }

    @Test
    public void getUsernameNull() {
        assertNull("Username Wasn't Null!", nullUser.getUsername());
    }

    @Test
    public void setUsernameValid() {
        String newUsername = "ThisIsNewUserName";
        validUser.setUsername(newUsername);
        assertEquals(newUsername, validUser.getUsername());
    }

    @Test
    public void setUsernameNull() {
        validUser.setUsername(null);
        assertNull("New Username Wasn't Null!", validUser.getUsername());
    }

    @Test
    public void getEmailValid() {
        assertEquals(validEmail, validUser.getEmail());
    }

    @Test
    public void getEmailNull() {
        assertNull("Email Wasn't Null!", nullUser.getEmail());
    }

    @Test
    public void setEmailValid() {
        String newEmail = "newTest@gmail.com";
        validUser.setEmail(newEmail);
        assertEquals(newEmail, validUser.getEmail());
    }

    @Test
    public void setEmailNull() {
        validUser.setEmail(null);
        assertNull("New Email Wasn't Null!", validUser.getEmail());
    }

    @Test
    public void getFnameValid() {
        assertEquals(validFname, validUser.getFname());
    }

    @Test
    public void getFnameNull() {
        assertNull(nullUser.getFname());
    }

    @Test
    public void setFnameValid() {
        String newFname = "NewFirstName";
        validUser.setFname(newFname);
        assertEquals(newFname, validUser.getFname());
    }

    @Test
    public void setFnameNull() {
        validUser.setFname(null);
        assertNull(validUser.getFname());
    }

    @Test
    public void getLnameValid() {
        assertEquals(validLname, validUser.getLname());
    }

    @Test
    public void getLnameNull() {
        assertNull(nullUser.getLname());
    }

    @Test
    public void setLnameValid() {
        String newLname = "NewLastName";
        validUser.setLname(newLname);
        assertEquals(newLname, validUser.getLname());
    }

    @Test
    public void setLnameNull() {
        validUser.setLname(null);
        assertNull(validUser.getLname());
    }

    @Test
    public void getCaringStatCarer() {
        assertEquals(CaringStatus.CARER.toString(), validUser.getCaringStat());
    }

    @Test
    public void getCaringStatCaredFor() {
        assertEquals(CaringStatus.CARED_FOR.toString(), nullUser.getCaringStat());
    }

    @Test
    public void setCaringStatCaredFor() {
        validUser.setCaringStat(CaringStatus.valueOf("CARED_FOR"));
        assertEquals(CaringStatus.CARED_FOR.toString(), validUser.getCaringStat());
    }

    @Test
    public void setCaringStatCarer() {
        nullUser.setCaringStat(CaringStatus.valueOf("CARER"));
        assertEquals(CaringStatus.CARER.toString(), nullUser.getCaringStat());
    }

    @Test
    public void getMobNumValid() {
        assertEquals(validMobNum, validUser.getMobNum());
    }

    @Test
    public void getMobNumNull() {
        assertNull(nullUser.getMobNum());
    }

    @Test
    public void setMobNumValid() {

        String newMobNum = "+61408555123";
        validUser.setMobNum(newMobNum);
        assertEquals(newMobNum, validUser.getMobNum());

    }

    @Test
    public void setMobNum() {
        validUser.setMobNum(null);
        assertNull(validUser.getMobNum());

    }
}