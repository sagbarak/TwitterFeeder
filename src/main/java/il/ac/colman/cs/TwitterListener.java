package il.ac.colman.cs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.AWScred;
import il.ac.colman.cs.util.LinkExtractor;
import il.ac.colman.cs.util.Monitoring;
import il.ac.colman.cs.util.TweetJson;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

public class TwitterListener {
  public static void main(String[] args) throws TwitterException, IOException {
    // TODO Auto-generated method stub
    ConfigurationBuilder cb = new ConfigurationBuilder();

    cb.setDebugEnabled(true)

            .setOAuthConsumerKey(System.getProperty("config.twitter.consumer.key"))

            .setOAuthConsumerSecret(System.getProperty("config.twitter.consumer.secret"))

            .setOAuthAccessToken(System.getProperty("config.twitter.access.token"))

            .setOAuthAccessTokenSecret(System.getProperty("config.twitter.access.secret"));


    // Create our Twitter stream

    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());

    TwitterStream twitterStream = tf.getInstance();
    System.out.println(twitterStream);

    StatusListener listener = new StatusListener(){

      AmazonSQS client = AWScred.getSQSclient();
      AmazonCloudWatch amazonCloudWatch = AWScred.getCloudWatchClient();

      public void onStatus(Status status) {
        if (status.getURLEntities() != null && status.getLang().equals("en")) {
          //if(status.getText().contains(System.getProperty("config.twitter.track"))) {
            for (final URLEntity map : status.getURLEntities()) {
              // Send message to a Queue
              try {
                ObjectMapper objectMapper = new ObjectMapper();
                long id = status.getId();
                String output = objectMapper.writeValueAsString(new
                        TweetJson(map.getExpandedURL(), id, System.getProperty("config.twitter.track")));
                client.sendMessage(System.getProperty("config.sqs.url"), output);
                System.out.println(output);
                Monitoring.CloudWatchTraffic(amazonCloudWatch, 1.00, "TwitterFeeder"
                        , System.getProperty("config.twitter.track"));
                System.out.println(status.getText());
              } catch (JsonProcessingException e) {
                e.printStackTrace();
              }
            }
          //}
        }
      }

      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

      //ignore
      public void onScrubGeo(long l, long l1) {

      }
      //ignore
      public void onStallWarning(StallWarning stallWarning) {

      }

      public void onException(Exception ex) {
        ex.printStackTrace();
      }
    };

    twitterStream.addListener(listener);
    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
    twitterStream.sample();

  }
}
