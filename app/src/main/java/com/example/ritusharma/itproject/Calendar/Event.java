package com.example.ritusharma.itproject.Calendar;

/**
 *
 * This class represents an Event object that a user creates when interacting with the Calendar.
 *
 * And event object is composed of the following attributes, most of which are self explanatory
 *
 * StartDate
 * EndDate
 * StartTime
 * EndTime
 * EventName
 * EventDesc - A brief optional description of the event.
 * UserID - The unique ID of the creator of this event.
 *
 *
 * */

public class Event {

    /*
    * Attributes of the Event class.
    * */

    private String StartDate;
    private String EndDate;
    private String StartTime;
    private String EndTime;
    private String EventName;
    private String EventDesc;
    private String UserID;

    /*
    * The expanded property of a event card in the RecyclerView determines whether or not the
    * card stays collapsed - to display brief events.
    * */
    private boolean expanded;

    /*Constructor*/
    public Event(String sd, String ed, String st, String et, String en, String evd, String uid) {
        this.StartDate = sd;
        this.EndDate = ed;
        this.StartTime = st;
        this.EndTime = et;
        this.EventName = en;
        this.EventDesc = evd;
        this.UserID = uid;
    }

    public Event (){
        /* Empty constructor needed for FireBase */
    }


    /************8 Getters and Setters 8************/

    /**
     * Get the start date of an Event object
     *
     * @return : The start date of an Event object (String)
     * */
    public String getStartDate() { return StartDate; }

    /*********************************************************************************************/

    /**
     * Set the start date of an Event object
     *
     * @param startDate:  the new date of the event
     * */

    public void setStartDate(String startDate) {StartDate = startDate;}

    /*********************************************************************************************/

    /**
     * Get the end date of an Event object
     *
     * @return : The end date of an Event object (String)
     * */

    public String getEndDate() {return EndDate;}

    /*********************************************************************************************/

    /**
     * Set the end date of an Event object
     *
     * @param endDate : The end date of an Event object (String)
     * */
    public void setEndDate(String endDate) { EndDate = endDate;}

    /*********************************************************************************************/

    /**
     * Get the start time of an Event object
     *
     * @return : The end date of an Event object (String)
     * */
    public String getStartTime() {return StartTime; }

    /*********************************************************************************************/

    /**
     * Set the start time of an Event object
     *
     * @param  startTime : The start time of an Event object (String)
     * */

    public void setStartTime(String startTime) {StartTime = startTime;}

    /*********************************************************************************************/

    /**
     * Get the end time of an Event object
     *
     * @return : The end time of an Event object (String)
     * */
    public String getEndTime() { return EndTime;  }

    /*********************************************************************************************/

    /**
     * Set the end time of an Event object
     *
     * @param endTime : The end time of an Event object (String)
     * */
    public void setEndTime(String endTime) { EndTime = endTime;  }

    /*********************************************************************************************/

    /**
     * Get the name of the Calendar Event
     *
     * @return : The event name
     * */
    public String getEventName() { return EventName; }

    /*********************************************************************************************/

    /**
     * Set the name of the Calendar Event
     *
     * @param eventName : The event name
     * */
    public void setEventName(String eventName) { EventName = eventName; }

    /*********************************************************************************************/

    /**
     * Get the description of the Calendar Event
     *
     * @return : The event description
     * */
    public String getEventDesc() { return EventDesc;  }

    /*********************************************************************************************/


    /**
     * Set the description of the Calendar Event
     *
     * @param eventDesc : The event description
     * */
    public void setEventDesc(String eventDesc) {EventDesc = eventDesc; }

    /*********************************************************************************************/


    /**
     * Get the userId of the creator of the Calendar Event
     *
     * @return : The userID
     * */
    public String getUserID() {return UserID; }

    /*********************************************************************************************/


    /**
     * Change the creator of the Calendar Event
     *
     * @param userID : The new creator of the Event
     * */
    public void setUserID(String userID) { UserID = userID; }

    /*********************************************************************************************/

    public boolean isExpanded() { return expanded; }

    /*********************************************************************************************/

    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    /*********************************************************************************************/

}

