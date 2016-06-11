package com.crooks;

/**
 * Created by johncrooks on 6/9/16.
 */
public class DiveEntry {
    String location,buddy,comments;
    int maxDepth, duration,id;

    public DiveEntry(String location, String buddy, String comments, int maxDepth, int duration, int id) {
        this.location = location;
        this.buddy = buddy;
        this.comments = comments;
        this.maxDepth = maxDepth;
        this.duration = duration;
        this.id= id;
    }

    public DiveEntry() {
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBuddy(String buddy) {
        this.buddy = buddy;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setId(int id){
        this.id=id;
    }

    @Override
    public String toString() {
        return "DiveEntry{" +
                "location='" + location + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
