package com.crooks;

import java.util.ArrayList;

/**
 * Created by johncrooks on 6/9/16.
 */
public class User {
    int id;
    String userName;
    String password;
    ArrayList<DiveEntry> diveLog = new ArrayList<>();

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(int id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public User(int id, String userName, String password, ArrayList<DiveEntry> diveLog) {
        this.id=id;
        this.userName = userName;
        this.password = password;
        this.diveLog = diveLog;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<DiveEntry> getDiveLog() {
        return diveLog;
    }

    public void setDiveLog(ArrayList<DiveEntry> diveLog) {
        this.diveLog = diveLog;
    }
}
