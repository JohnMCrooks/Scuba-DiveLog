package com.crooks;

import com.sun.org.apache.xpath.internal.operations.Div;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import static spark.Spark.staticFileLocation;

public class Main {

    static HashMap<String, User> userHash = new HashMap<>();


    public static void main(String[] args) {

        //adding some pre-seeded data for ease of testing
        addTestUsers();
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","HUGE Sharks everywhere",80,15, 0));
        userHash.get("Alice").diveLog.add(new DiveEntry("Hawaii","Charlie","nothing noteworthy besides, well, everything...",200,5, 1));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,45,123, 2));
        userHash.get("Alice").diveLog.add(new DiveEntry("Hawaii","Charlie","The font-family property should hold several font names as a \"fallback\" system. If the browser does not support the first font, it tries the next font, and so on.\n" +
                "\n" +
                "Start with the font you want, and end with a generic family, to let the browser pick a similar font in the generic family, if no other fonts are available..",30,5, 3));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,45,123, 4));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,45,123, 5));
        userHash.get("Alice").diveLog.add(new DiveEntry("Hawaii","bob","TESTING PURPOSES. TESTING PURPOSES. TESTING PURPOSES. TESTING PURPOSES. TESTING PURPOSES. TESTING PURPOSES." ,45,123, 6));
        userHash.get("Alice").diveLog.add(new DiveEntry("Nebraska","bob","Lorem ipsum, Etc..." ,1,156, 7));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,5,53, 8));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","for the sake of page seperation and readability I'll make this one a little different" ,88,23, 9));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,74,93, 10));
        userHash.get("Alice").diveLog.add(new DiveEntry("Bali","bob","Lorem ipsum, Etc..." ,32,13, 11));

        staticFileLocation("templates");

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
                        int offset = 0;
                        String offStr = request.queryParams("offset");              //Add Offset parameters for pagination

                        if (offStr!=null){
                            offset = Integer.valueOf(offStr);
                        }
                        int newOffset = offset+4;

                        ListIterator<DiveEntry> iter = userHash.get(username).diveLog.listIterator(offset);  //Iterate through the list and display via offset values.
                        ArrayList<DiveEntry> diveSubList = new ArrayList<DiveEntry>();
                        while (iter.hasNext()) {
                            if (iter.nextIndex() == newOffset) {
                                break;
                            }
                            diveSubList.add(iter.next());
                        }

                        m.put("name", username);
                        m.put("diveList", diveSubList);
                        m.put("offsetup", offset+4);
                        m.put("offsetdown", offset-4);
                        m.put("shownext", offset+4 < userHash.get(username).diveLog.size());
                        m.put("showprev", offset>0);

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

        Spark.get(
                "/editEntry",
                (request, response) -> {
                    Session session =request.session();
                    String username = session.attribute("username");
                    int id= Integer.valueOf(request.queryParams("id"));

                    HashMap m = new HashMap();

                    if (username==null) {
                        response.redirect("/");
                    } else {
                        DiveEntry de = userHash.get(username).diveLog.get(id);

                        m.put("location", de.location);
                        m.put("buddy", de.buddy);
                        m.put("comments", de.comments);
                        m.put("maxdepth", de.maxDepth);
                        m.put("duration", de.duration);
                        m.put("id", de.id);
                    }
                    return new ModelAndView(m, "editEntry.html");

                }, new MustacheTemplateEngine()
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

                    User user = userHash.get(username);             //If not logged in, redirect user;
                    if (username==null) {
                        response.redirect("/");
                    }

                    String location = request.queryParams("location");          //Grab variable details from the user
                    String buddy = request.queryParams("buddy");
                    String comments = request.queryParams("comments");
                    int maxdepth = Integer.valueOf( request.queryParams("maxdepth"));
                    int duration = Integer.valueOf(request.queryParams("duration"));
                    int id = 0;

                    ArrayList<DiveEntry> emptyDiveLog = new ArrayList<>();

                    if (user.diveLog==null){
                        user.diveLog = emptyDiveLog;
                        id = emptyDiveLog.size();
                        System.out.println("temp");
                    }else {
                        id = userHash.get(username).diveLog.size();
                    }

                    userHash.get(username).diveLog.add(new DiveEntry(location,buddy,comments,maxdepth,duration,id));

                    response.redirect("/");
                    return"";
                }
        );



//TODO finish the post route below
        Spark.post(
                "/editEntry",
                (request, response) -> {
                    Session session =request.session();
                    String username = session.attribute("username");

                    User user = userHash.get(username);             //If not logged in, redirect user;
                    if (username==null) {
                        response.redirect("/");
                    }

                    String location = request.queryParams("location");          //Grab variable details from the user
                    String buddy = request.queryParams("buddy");
                    String comments = request.queryParams("comments");
                    int maxdepth = Integer.valueOf( request.queryParams("maxdepth"));
                    int duration = Integer.valueOf(request.queryParams("duration"));
                    int id = Integer.valueOf(request.queryParams("id"));

                    DiveEntry updatedEntry = new DiveEntry(location,buddy,comments,maxdepth,duration,id);

                    userHash.get(username).diveLog.set(id, updatedEntry);

                    response.redirect("/");
                    return"";


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
                    if (user.diveLog.size() >= 1) {                      //after removing an entry, if the log isn't empty reset the id numbers.
                        for (DiveEntry de : user.diveLog) {
                            de.id = index;
                            index++;
                        }
                    }
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
    }

    static void addTestUsers(){
        userHash.put("Alice", new User("Alice", "a", new ArrayList<DiveEntry>()));
        userHash.put("Bob", new User("Bob", "123"));
        userHash.put("Charlie", new User("Charlie", "qwe"));

    }

}
