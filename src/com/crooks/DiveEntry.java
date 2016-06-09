package com.crooks;

/**
 * Created by johncrooks on 6/9/16.
 */
public class DiveEntry {
    String location,buddy,comments;
    int maxDepth, duration;

    public DiveEntry(String location, String buddy, String comments, int maxDepth, int duration, int id) {
        this.location = location;
        this.buddy = buddy;
        this.comments = comments;
        this.maxDepth = maxDepth;
        this.duration = duration;
    }

    public DiveEntry() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBuddy() {
        return buddy;
    }

    public void setBuddy(String buddy) {
        this.buddy = buddy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "DiveEntry{" +
                "location='" + location + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
