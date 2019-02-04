package com.example.ritusharma.itproject.Auth_SignIn;

/**
 * User Object - Defines a User's main attributes & Form for the DB
 */
public class UserObj {
    /* Member Variables */
    private String UID;
    private String username;
    private String email;
    private String fname;
    private String lname;
    private String caringStat;
    private String mobNum;

    public UserObj() {
        /*
         * Default constructor required for calls to
         * DataSnapshot.getValue(UserObj.class)
         */
    }

    /*********************************************************************************************/
    /** Constructors **/

    /**
     * Constructors for User objects
     * 
     * @param UID        - The Firebase-generated unique User ID string.
     * @param username   - The user's chosen username.
     * @param email      - The user's email
     * @param fname      - The user's First Name
     * @param lname      - The user's Last Name
     * @param caringStat - The user's caring status (carer or cared-for)
     * @param mobNum     - The user's mobile number
     */
    public UserObj(String UID, String username, String email, String fname, 
                    String lname, CaringStatus caringStat, String mobNum) {
        this.UID = UID;
        this.username = username;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.caringStat = caringStat.toString();
        this.mobNum = mobNum;
    }

    /*********************************************************************************************/

    /**
     * Constructors for User objects Sans Email
     * 
     * @param UID        - The Firebase-generated unique User ID string.
     * @param username   - The user's chosen username.
     * @param fname      - The user's First Name
     * @param lname      - The user's Last Name
     * @param caringStat - The user's caring status (carer or cared-for)
     * @param mobNum     - The user's mobile number
     */
    public UserObj(String UID, String username, String fname, String lname, 
                    CaringStatus caringStat, String mobNum) {
        this.UID = UID;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.caringStat = caringStat.toString();
        this.mobNum = mobNum;
    }

    /*********************************************************************************************/

    /**
     * Compares Equality of 2 User Objects
     * @param u2 - The User Object to compare against.
     * @return True if the UserObjs are the same.
     */
    public boolean equals(UserObj u2) {
        if(!this.UID.equals(u2.getUID())) {
            return false;
        } else if(!this.username.equals(u2.getUsername())) {
            return false;
        } else if(!this.fname.equals(u2.getFname())) {
            return false;
        } else if(!this.lname.equals(u2.getLname())) {
            return false;
        } else if(!this.email.equals(u2.getEmail())) {
            return false;
        } else if(!this.caringStat.equals(u2.getCaringStat())) {
            return false;
        } else if(!this.mobNum.equals(u2.getMobNum())) {
            return false;
        }

        return true;
    }

    /*********************************************************************************************/
    /** Getters and Setters */

    /**
     * Getter for the User ID
     * 
     * @return The Firebase-generated unique user ID string
     */
    public String getUID() {
        return UID;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's UID
     * 
     * @param UID - The ID to give the user.
     */
    public void setUID(String UID) {
        this.UID = UID;
    }

    /*********************************************************************************************/

    /**
     * Gets the user's Username
     * 
     * @return The user's chosen Username
     */
    public String getUsername() {
        return username;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's Username to a new value
     * 
     * @param username - The user's new chosen Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /*********************************************************************************************/

    /**
     * Gets the user's email address
     * 
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's email address
     * 
     * @param email - The user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*********************************************************************************************/

    /**
     * Gets the user's First Name
     * 
     * @return The user's First Name
     */
    public String getFname() {
        return fname;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's First Name
     * 
     * @param fname - The user's First Name
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /*********************************************************************************************/

    /**
     * Gets the user's Last Name
     * 
     * @return The user's Last Name
     */
    public String getLname() {
        return lname;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's Last Name
     * 
     * @param lname - The user's Last Name
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /*********************************************************************************************/

    /**
     * Gets the user's Caring Status
     * 
     * @return CARER if user is carer, otherwise CARED_FOR
     */
    public String getCaringStat() {
        return this.caringStat;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's Caring Status
     * 
     * @param caringStat - The new caring status to assign
     */
    public void setCaringStat(CaringStatus caringStat) {
        this.caringStat = caringStat.toString();
    }

    /*********************************************************************************************/

    /**
     * Gets the user's mobile number
     * 
     * @return The user's mobile number as a String
     */
    public String getMobNum() {
        return mobNum;
    }

    /*********************************************************************************************/

    /**
     * Sets the user's mobile number
     * 
     * @param mobNum - Changes the user's current mobile number
     */
    public void setMobNum(String mobNum) {
            this.mobNum = mobNum;
    }

    /*********************************************************************************************/
}
