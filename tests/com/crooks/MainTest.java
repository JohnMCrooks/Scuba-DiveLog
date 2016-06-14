package com.crooks;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        assertTrue(de.location.equals("Jamaica"));
        assertTrue(de.maxDepth==50);
    }

}