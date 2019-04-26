package org.headroyce.sean.link;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Sean Cavalieri
 * @since 11/26/18
 */

public class Event implements Comparable<Event>{

    private String title;
    private String description;
    private int month;
    private int day;
    private int year;
    private int hour;
    private int minute;
    private LatLng location;

    //for distinguishing between events
    private String id;
    //Event ID as assigned by server
    private String IDNumber;

    //account which created the event -- used to allow permission to delete
    private String username;

    public Event(String title, String description, int month, int day, int year, int hour, int minute, LatLng location){
        this.title = title;
        this.description = description;
        this.month = month;
        this.day = day;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.location = location;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getYear() { return year; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public LatLng getLocation() { return location; }

    //for destinguishing events
    public void setID(String id) { this.id = id; }
    public String getID() { return id; }

    //Server Assigned ID's
    public String getIDNumber() { return IDNumber; }
    public void setIDNumber(String newID) { IDNumber = newID;}

    //username for authenticating deletions
    public void setUsername(String user) { username = user; }
    public String getUsername() { return username; }

    //O(1)
    @Override
    public int compareTo(@NonNull Event o) {
        //returns 1 is object is sooner, -1 if it is later
        if(year > o.getYear()){
            return 1;
        } else if(year < o.getYear()){
            return -1;
        } else {
            if(month > o.getMonth()){
                return 1;
            } else if(month < o.getMonth()){
                return -1;
            } else {
                if(day > o.getDay()){
                    return 1;
                } else if(day < o.getDay()){
                    return -1;
                } else {
                    if(hour > o.getHour()){
                        return 1;
                    } else if(hour < o.getHour()){
                        return -1;
                    } else {
                        if(minute > o.getMinute()){
                            return 1;
                        } else if(minute < o.getMinute()){
                            return -1;
                        } else {
                            return 1;   //if equal, first one is called as earlier
                        }
                    }
                }
            }
        }
    }


}
