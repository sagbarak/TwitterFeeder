package il.ac.colman.cs.util;

import il.ac.colman.cs.ExtractedLink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
  Connection conn;


  public DataStorage() {
    this.conn = getRemoteConnection() ;
  }

  private static Connection getRemoteConnection() {
    if (System.getProperty("RDS_HOSTNAME") != null) {
      try {
        try {
          System.out.println("Loading driver...");
          Class.forName("com.mysql.cj.jdbc.Driver");
          System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
          throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }
        String dbName = System.getProperty("RDS_DB_NAME");
        String userName = System.getProperty("RDS_USERNAME");
        String password = System.getProperty("RDS_PASSWORD");
        String hostname = System.getProperty("RDS_HOSTNAME");
        String port = System.getProperty("RDS_PORT");
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        System.out.println(jdbcUrl);
        Connection con = DriverManager.getConnection(jdbcUrl);
        System.out.println("The connection with the database succeeded");

        return con;
      }  catch (SQLException e) {
        e.printStackTrace();
      }
    }
    System.out.println("connection to db failed");
    return null;
  }
  /**
   * Add link to the database
   */
  public void addLink(ExtractedLink link, String track) {
    /*
    This is where we'll add our link
     */
  }

  /**
   * Search for a link
   * @param query The query to search
   */
  public List<ExtractedLink> search(String query) {
    /*
    Search for query in the database and return the results
     */

    return null;
  }
}
