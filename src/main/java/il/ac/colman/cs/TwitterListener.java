package il.ac.colman.cs;

import il.ac.colman.cs.util.LinkExtractor;
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
      public void onStatus(Status status) {
        System.out.println(status.getUser().getName() + " : " + status.getText());
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
