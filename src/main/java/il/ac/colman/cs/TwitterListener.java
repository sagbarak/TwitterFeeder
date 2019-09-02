package il.ac.colman.cs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
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

      AmazonSQS client = AmazonSQSClientBuilder.defaultClient();

      public void onStatus(Status status) {
        if (status.getURLEntities().length != 0 && status.getLang().equals("en")) {
          System.out.println(status.getUser().getName() + " : " + status.getText());
          for (URLEntity url : status.getURLEntities()) {
            JSONObject tweet = new JSONObject();
            try {
              tweet.put("id", status.getId());
              tweet.put("url", url.getExpandedURL());
              tweet.put("track",System.getProperty("config.twitter.track"));

              String tweetOutput = tweet.toString();
              client.sendMessage(System.getProperty("config.sqs.url"), tweetOutput);

            } catch (JSONException e) {
              e.printStackTrace();
            }


          }
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
