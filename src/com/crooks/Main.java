package com.crooks;

import com.sun.org.apache.xpath.internal.operations.Div;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userHash = new HashMap<>();


    public static void main(String[] args) {
        addTestUsers();
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","HUGE Sharks everywhere",80,15, 0));

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap<>();
                    Session session = request.session();
                    String username = session.attribute("username");

                    if(username==null){
                        return new ModelAndView(m, "index.html");
                    }else {
                        ArrayList<DiveEntry> diveEntryArray = userHash.get(username).diveLog;

                        m.put("name", username);
                        m.put("diveList", diveEntryArray);
                        return new ModelAndView(m, "dive.html");
                    }

                },
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/addentry",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    if (username==null){
                        response.redirect("/");

                    } else {

                        DiveEntry de = new DiveEntry();

                        m.put("location", de.location);
                        m.put("buddy", de.buddy);
                        m.put("comments", de.comments);
                        m.put("maxdepth", de.maxDepth);
                        m.put("duration", de.duration);

                    }
                    return new ModelAndView(m, "addEntry.html");

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String username =request.queryParams("username");
                    String password = request.queryParams("password");

                    if(username==null ||password==null){
                        Spark.halt("Name or Pass not sent through");
                    }

                    User user = userHash.get(username);
                    if(user==null){
                        user = new User(username,password);
                        userHash.put(username,user);
                    }else if (!password.equals(user.password)){
                        Spark.halt("Wrong Password!");
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                }

        );
        Spark.post(
                "/addentry",
                (request, response) -> {
                    Session session =request.session();
                    String username = session.attribute("username");

                    String location = request.queryParams("location");
                    String buddy = request.queryParams("buddy");
                    String comments = request.queryParams("comments");
                    int maxdepth = Integer.valueOf( request.queryParams("maxdepth"));
                    int duration = Integer.valueOf(request.queryParams("duration"));
                    int id= 0;

                    User user = userHash.get(username);
                    if (username==null) {
                        response.redirect("/");
                    }
                    DiveEntry de = new DiveEntry();
                    ArrayList<DiveEntry> emptyDiveLog = new ArrayList<>();
                    if (user.diveLog==null){
                        user.diveLog = emptyDiveLog;
                    }
                    de.setLocation(location);
                    de.setBuddy(buddy);
                    de.setComments(comments);
                    de.setMaxDepth(maxdepth);
                    de.setDuration(duration);
                    de.setId(id = userHash.get(username).diveLog.size());

                    userHash.get(username).diveLog.add(new DiveEntry(location,buddy,comments,maxdepth,duration,id));

                    response.redirect("/");
                    return"";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();

                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/delete",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        Spark.halt("Sorry you can't delete this entry if you are not logged in.");
                    }
                    User user = userHash.get(username);
                    int id = Integer.valueOf(request.queryParams("id"));
                    if (id < 0 || id >= user.diveLog.size()) {           //after retrieving ID number check to make sure it's valid - Which it should be since the user can't alter it
                        Spark.halt("Invalid Entry ID ##");
                    }
                    user.diveLog.remove(id);

                    int index = 0;
                    if (user.diveLog.size() > 1) {              //after removing an entry, if the log isn't empty reset the id numbers.
                        for (DiveEntry de : user.diveLog) {
                            de.id = index;
                            index++;
                        }
                    }
                    response.redirect("/");
                    return"";
                }

        );

    }

    static void addTestUsers(){
        userHash.put("Alice", new User("Alice", "asd", new ArrayList<DiveEntry>()));
        userHash.put("Bob", new User("Bob", "123"));
        userHash.put("Charlie", new User("Charlie", "qwe"));

    }

}
