package il.ac.colman.cs.util;

import com.amazonaws.services.s3.AmazonS3;
import il.ac.colman.cs.ExtractedLink;

import java.sql.*;
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

  public void createTable() {
    Statement statement = null;
    try {

      statement = this.conn.createStatement();
      String sql = "CREATE TABLE  IF NOT EXISTS TWEET_DB" +
              "(PRIMARYID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
              "LINK  TEXT," +
              "TWEETID LONG NOT NULL," +
              "TITLE   TEXT," +
              "CONTENT  TEXT," +
              "TIMESTAMP DATETIME," +
              "SCREENSHOT TEXT," +
              "TRACK TEXT)";
      statement.executeUpdate(sql);
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
    System.out.println("The table created");
  }

  public void insertTable(ExtractedLink info, Long tweetID, String track) {

    try {
      DeleteFromDB();
      conn.setAutoCommit(false);
      System.out.println("Open the database");
      String insert_sql_statement = "INSERT INTO TWEET_DB" +
              "(LINK,TWEETID,TITLE,CONTENT,TIMESTAMP,SCREENSHOT,TRACK) VALUES" +
              "(?,?,?,?,?,?,?)";
      PreparedStatement preparedStatement = conn.prepareStatement(insert_sql_statement);
      preparedStatement.setString(1,info.getUrl());
      preparedStatement.setLong(2,tweetID);
      preparedStatement.setString(3,info.getTitle());
      preparedStatement.setString(4,info.getContent());
      preparedStatement.setTimestamp(5,new Timestamp((System.currentTimeMillis())));
      preparedStatement.setString(6,info.getScreenshotURL());
      preparedStatement.setString(7,track);

      preparedStatement.executeUpdate();
      preparedStatement.close();
      conn.commit();
    } catch (Exception e) {
      System.err.println(e.getClass().getName()+ ": " +e.getMessage());
      System.exit(0);
    }
    System.out.println("Insert Successful");
  }

  private void DeleteFromDB()
  {
    int rows_number = 0;
    ResultSet set;
    try {
      Statement statement = conn.createStatement();
      String query = "SELECT COUNT(*) FROM TWEET_DB";
      set = statement.executeQuery(query);
      if(set.next())
        rows_number = set.getInt("COUNT(*)");
      if(rows_number >= 1000)
      {
        String screenshot = "";
        String id = "";
        query = "SELECT * FROM TWEET_DB ORDER BY PRIMARYID LIMIT 1 ";
        set = statement.executeQuery(query);
        if(set.next()){
          screenshot = ((set.getString("SCREENSHOT")).substring(1));
          id = set.getString("PRIMARYID");
          AmazonS3 clientS3 = AWScred.getS3Client();
          String bucket_name = System.getProperty("BUCKET");
          clientS3.deleteObject(bucket_name,screenshot);

          query = "DELETE FROM TWEET_DB WHERE PRIMARYID ="+id;
          statement.executeUpdate(query);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
