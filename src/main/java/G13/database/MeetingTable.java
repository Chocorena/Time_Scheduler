package G13.database;

import java.util.Date;

public class MeetingTable {
    String title, startTime, endTime, location, priority, reminder, duration;
    public Date startDate;
    public Date endDate;
    int id, accountid;

    /**
     * A constructor with the following parameters is created when the class is opened.
     * The values of the variables are taken from the parameters.
     * Getter and Setter declaration.
     * @param id
     * @param title
     * @param startDate
     * @param startTime
     * @param location
     * @param priority
     * @param reminder
     * @param accountid
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {return startDate;}

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getAccountid() {return accountid; }

    public void setAccountid(int accountid) {this.accountid = accountid; }

    @Override
    public String toString() { return priority; }
}
