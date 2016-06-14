package com.crooks;

import com.sun.org.apache.xpath.internal.operations.Div;
import org.h2.tools.CreateCluster;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import static spark.Spark.staticFileLocation;

public class Main {

    public static void createTables(Connection conn) throws SQLException{
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS diveEntries( id IDENTITY, location VARCHAR, buddy VARCHAR, comments VARCHAR, maxdepth INT, duration INT, user_id INT)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL,?,?)");
        stmt.setString(1,name);
        stmt.setString(2,password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1,name);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            int id = results.getInt("id");
            String password = results.getString("password");
            return  new User(id,name,password);
        }
        return null;
    }

    public static void insertEntry(Connection conn, String location, String buddy, String comments, int maxDepth, int duration, int userID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO diveEntries VALUES (NULL,?,?,?,?,?,? )");
        stmt.setString(1, location);
        stmt.setString(2, buddy);
        stmt.setString(3, comments);
        stmt.setInt(4, maxDepth);
        stmt.setInt(5, duration);
        stmt.setInt(6, userID);
        stmt.execute();
    }

    public static DiveEntry selectEntry(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM diveEntries INNER JOIN users ON diveEntries.user_id = users.id WHERE users.id=?");
        stmt.setInt(1,id);
        ResultSet results = stmt.executeQuery();
        if(results.next()){
            String location = results.getString("diveEntries.location");
            String buddy = results.getString("diveEntries.buddy");
            String comments = results.getString("diveEntries.comments");
            int maxDepth = results.getInt("diveEntries.maxdepth");
            int duration = results.getInt("diveEntries.duration");
            return new DiveEntry(location,buddy,comments,maxDepth,duration,id);
        }
        return null;
    }

    public static ArrayList<DiveEntry> selectEntries(Connection conn, int userID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM diveEntries INNER JOIN users ON diveEntries.user_id = users.id WHERE users.id=?");
        stmt.setInt(1, userID);

        ResultSet results = stmt.executeQuery();

        ArrayList<DiveEntry> deList = new ArrayList<>();

        while(results.next()){
            String location = results.getString("diveEntries.location");
            String buddy = results.getString("diveEntries.buddy");
            String comments = results.getString("diveEntries.comments");
            int maxDepth = results.getInt("diveEntries.maxdepth");
            int duration = results.getInt("diveEntries.duration");
            int id = results.getInt("diveEntries.id");

            DiveEntry d1 = new DiveEntry(location,buddy,comments,maxDepth,duration,id);
            deList.add(d1);
        }
        return deList;
    }

    public static void deleteEntry(Connection conn, int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM diveEntries WHERE id = ?");
        stmt.setInt(1,id);
        stmt.execute();
    }

    public static void editEntry(Connection conn,String location, String buddy, String comments, int maxDepth, int duration, int id ) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("UPDATE diveEntries SET location = ?, buddy=?, comments=?, maxdepth=?,duration=? WHERE id=?");
        stmt.setString(1, location);
        stmt.setString(2, buddy);
        stmt.setString(3, comments);
        stmt.setInt(4,maxDepth);
        stmt.setInt(5, duration);
        stmt.setInt(6, id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        staticFileLocation("templates");

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap<>();
                    Session session = request.session();
                    String username = session.attribute("username");
                    User user =  selectUser(conn, username);
                    if(username==null){
                        return new ModelAndView(m, "index.html");
                    }else {

                        ArrayList<DiveEntry> diveSubList = selectEntries(conn, user.id);

                        m.put("name", username);
                        m.put("diveList", diveSubList);

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
                    int id = Integer.valueOf(request.queryParams("id"));
                    User user = selectUser(conn, username);

                    HashMap m = new HashMap();

                    if (username==null) {
                        response.redirect("/");
                    } else {

                        DiveEntry de = selectEntry(conn, user.id);

                        m.put("location", de.location);
                        m.put("buddy", de.buddy);
                        m.put("comments", de.comments);
                        m.put("maxdepth", de.maxDepth);
                        m.put("duration", de.duration);
                        m.put("id", id);
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

                    User user = selectUser(conn, username);
                    if(user==null){
                        insertUser(conn, username, password);

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

                    User user = selectUser(conn, username);             //If not logged in, redirect user;
                    if (username==null) {
                        response.redirect("/");
                    }

                    String location = request.queryParams("location");          //Grab variable details from the user
                    String buddy = request.queryParams("buddy");
                    String comments = request.queryParams("comments");
                    int maxdepth = Integer.valueOf( request.queryParams("maxdepth"));
                    int duration = Integer.valueOf(request.queryParams("duration"));
                    int id = 0;

                    insertEntry(conn, location, buddy, comments,maxdepth, duration, user.id);

                    response.redirect("/");
                    return"";
                }
        );

        Spark.post(
                "/editEntry",
                (request, response) -> {
                    Session session =request.session();
                    String username = session.attribute("username");

                    User user = selectUser(conn, username);             //If not logged in, redirect user;
                    if (username==null) {
                        response.redirect("/");
                    }

                    String location = request.queryParams("location");          //Grab variable details from the user
                    String buddy = request.queryParams("buddy");
                    String comments = request.queryParams("comments");
                    int maxdepth = Integer.valueOf( request.queryParams("maxdepth"));
                    int duration = Integer.valueOf(request.queryParams("duration"));
                    int id = Integer.valueOf(request.queryParams("id"));

                    editEntry(conn,location,buddy,comments,maxdepth,duration, id);

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
                    User user = selectUser(conn,username);
                    int id = Integer.valueOf(request.queryParams("id"));
                     deleteEntry(conn, id);

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
    } //End Main method


} //End Main Class
