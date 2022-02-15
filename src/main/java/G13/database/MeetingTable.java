package G13.database;

import java.util.Date;

/**
 * responsible for meeting information
 */
public class MeetingTable {
    String title, startTime, endTime, location, priority, reminder, duration;
    public Date startDate;
    public Date endDate;
    int id, accountid;


    /**
     * A constructor with the following parameters is created when the class is opened.
     * The values of the variables are taken from the parameters.
     * Getter and Setter declaration.
     * @param id of the meeting as an int
     * @param title of the meeting as a String
     * @param startDate needs to be a date
     * @param endDate needs to be a date
     * @param startTime of the meeting as a String
     * @param endTime of the meeting as a String
     * @param location of the meeting as a String
     * @param priority of the meeting as a String, low, medium or high
     * @param reminder of the meeting as a String, 1 week, 3 hours, 1 hour, 10 minutes, no remidner
     * @param duration of the meeting as a String
     * @param accountid of the meeting as a int
     * @author Emre, Morena
     */
    public MeetingTable(int id, String title, Date startDate,Date endDate, String startTime, String endTime, String location, String priority, String reminder, String duration, int accountid) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.priority = priority;
        this.reminder = reminder;
        this.duration = duration;
        this.accountid = accountid;
    }

    /**
     * @return id of the meeting
     */
    public int getId() {
        return id;
    }

    /**
     * @param id set give id as the id of the meeting
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return title of the meeting
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title sets given string as the title of the meeting
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return startDate of the meeting
     */
    public Date getStartDate() {return startDate;}

    /**
     * @param startDate sets given date as the startDate of the meeting
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return endDate of the meeting
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate sets given date as the endDate of the meeting
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return startTime of the meeting
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime sets given String as the startTime of the meeting
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return endTime of the meeting
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime sets given String as the endTime of the meeting
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return location as String of the meeting
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location sets given String as the location of the meeting
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return priority as a String of the meeting
     */
    public String getPriority() {
        return priority;
    }

    /**
     * @param priority sets given String as the priority of the meeting
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return reminder of the meeting as a String
     */
    public String getReminder() {
        return reminder;
    }

    /**
     * @param reminder sets given String as the reminder of the meeting
     */
    public void setReminder(String reminder) {
        this.reminder = reminder;
    }


    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }


    /**
     * @return priority as a String
     */
    @Override
    public String toString() { return priority; }
}
