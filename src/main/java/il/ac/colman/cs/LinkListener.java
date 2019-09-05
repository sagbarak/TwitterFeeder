package il.ac.colman.cs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class LinkListener {
  public static void main(String[] args) throws SQLException {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();
    dataStorage.createTable();
    // Initiate our link extractor
    LinkExtractor linkExtractor = new LinkExtractor();

    AmazonSQS client = AWScred.getSQSclient();
    String sqs_url = System.getProperty("config.sqs.url");
    ReceiveMessageRequest request = new ReceiveMessageRequest(sqs_url);
    request.setWaitTimeSeconds(5);
    request.setVisibilityTimeout(1);
    while(true)
    {
      // Listen to SQS for arriving links
      ReceiveMessageResult result = client.receiveMessage(request);
      try {
        Thread.sleep(1000);

        for (Message message : result.getMessages()) {
          ObjectMapper mapper = new ObjectMapper();
          mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
          try {
            System.out.println("Message from sqs:\n" + message.getBody());
            TweetJson twitterInfo = mapper.readValue(message.getBody(), TweetJson.class);

            String tweet_url = twitterInfo.get_url();
            linkExtractor.extractContent(tweet_url);

            ExtractedLink extractedLink = linkExtractor.extractContent(tweet_url);
            if (extractedLink != null) {
              dataStorage.insertTable(extractedLink, twitterInfo.get_tweetID(), twitterInfo.get_track());
              System.out.println(extractedLink.toString());
            }
          } catch (JsonParseException e) {
            e.printStackTrace();
          } catch (JsonMappingException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
          client.deleteMessage(sqs_url, message.getReceiptHandle());
        }
        } catch (InterruptedException e) {
         e.printStackTrace();
        }
    }
  }
}

