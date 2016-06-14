package com.crooks;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by johncrooks on 6/14/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        conn.close();

        assertTrue(user !=null);
    }

    @Test
    public void testEntry() throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice","");
        User user = Main.selectUser(conn, "Alice");

        Main.insertEntry(conn,"Jamaica", "eddie","the sharks were yuuuuuge",50,41,1);
        DiveEntry de = Main.selectEntry(conn,1);
        conn.close();

        assertTrue(de !=null);
        assertTrue(de.maxDepth==50);
    }

    @Test
    public void testEntries() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice","");
        Main.insertUser(conn,"bob", "");

        User alice = Main.selectUser(conn, "Alice");
        User bob = Main.selectUser(conn, "bob");

        Main.insertEntry(conn, "hawaii","jeff","shark1",50,12, alice.id);
        Main.insertEntry(conn, "Fiji","mike","shark1",50,12, bob.id);
        Main.insertEntry(conn, "Maui","mike","shark1",50,12, bob.id);

        ArrayList<DiveEntry> msgs = Main.selectEntries(conn,1);
        ArrayList<DiveEntry> msgs2 = Main.selectEntries(conn,2);

        conn.close();

        assertTrue(msgs.size() == 1);
        assertTrue(msgs.get(0).id==alice.id);
        assertTrue(msgs.get(0).buddy.equals("jeff"));

        assertTrue(msgs2.size()==2);
        assertTrue(msgs2.get(0).buddy.equals("mike"));

    }

    @Test
    public void testDeleteEntry() throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertEntry(conn,"Jamaica", "eddie","the sharks were yuuuuuge",50,41,1);
        Main.deleteEntry(conn, 1);
        DiveEntry msg = Main.selectEntry(conn, 1);
        conn.close();
        assertTrue(msg==null);
    }

    @Test
    public void testEditEntry() throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertEntry(conn,"fiji", "eddie","the sharks were yuuuuuge",50,41,1);
        Main.editEntry(conn, "mars", "me", "blah", 99, 99,1);
        DiveEntry de = Main.selectEntry(conn,1);

        conn.close();

        assertTrue(de.location.equals("mars"));



    }
}