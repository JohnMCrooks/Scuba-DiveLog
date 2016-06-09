package com.crooks;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userHash = new HashMap<>();
    static ArrayList<DiveEntry> diveEntryArray = new ArrayList<>();

    public static void main(String[] args) {
        addTestEntries();
        addTestUsers();

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
                        m.put("name", username);
                        m.put("diveList", diveEntryArray);
                        return new ModelAndView(m, "dive.html");
                    }

                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                (request, response) -> {
                    String username =request.queryParams("username");
                    String password = request.queryParams("password");
                    User user = userHash.get(username);
                    if(user==null){
                        user = new User(username,password);
                        userHash.put(username,user);
                    }
                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
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





    }

    static void addTestUsers(){
        userHash.put("Alice", new User("Alice", "asd", diveEntryArray));
        userHash.put("Bob", new User("Bob", "123"));
        userHash.put("Charlie", new User("Charlie", "qwe"));

    }

    static void addTestEntries(){
        diveEntryArray.add(new DiveEntry("Bali", "Bob", "Saw a 12 foot Tiger Shark!!", 85, 12));
        diveEntryArray.add(new DiveEntry("Bali", "James", "got my BC stuck on some coral, Whoops :/", 40, 60));
        diveEntryArray.add(new DiveEntry("Jamaica", "Bob", "A damn Octopus stole my dive knife!", 35, 70));
    }
}
