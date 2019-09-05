package il.ac.colman.cs.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.s3.AmazonS3;
import il.ac.colman.cs.ExtractedLink;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Extract content from links
 */
public class LinkExtractor {

  public LinkExtractor(){}

  public ExtractedLink extractContent(String url) {
    try {
      AmazonCloudWatch cloudWatch = AWScred.getCloudWatchClient();
      Long start_time = System.nanoTime();

      String title=null;
      String text=null;
      String description_100_chars=null;
      //Using Jsoup to extract the data from the links
      org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
      title = doc.title();
      text = doc.body().text();
      if(text.length() > 100) {
        description_100_chars = text.substring(0,99);
      }
      else
        description_100_chars = text;

      Long end_time = ((System.nanoTime() - start_time) / 1000000);
      Monitoring.CloudWatchTraffic(cloudWatch,end_time.doubleValue(),"Content URL","Processing time");
      // Take screenshot
      String screenshot_path = new ScreenshotGenerator().takeScreenshot(url);
      String bucket_name = System.getProperty("BUCKET");
      AmazonS3 s3_client = AWScred.getS3Client();
      File ss_file = new File(screenshot_path  + ".png");
      String screenshotURL = "";
      if(ss_file.exists()) {
        try {
          s3_client.putObject(bucket_name, screenshot_path, ss_file);
        }catch (AmazonServiceException e) {
          System.err.println(e.getErrorMessage());
        }
        screenshotURL = s3_client.getUrl(bucket_name, screenshot_path).toString();
      }
      ExtractedLink extractedLink = new ExtractedLink(url, text, title, description_100_chars, screenshotURL);
      if(extractedLink != null){
        ss_file.delete();
      }
      return extractedLink;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
