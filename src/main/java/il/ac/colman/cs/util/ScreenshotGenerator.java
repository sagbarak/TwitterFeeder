package il.ac.colman.cs.util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ScreenshotGenerator {
  public static String takeScreenshot(String url){
    UUID uuid = UUID.randomUUID();
    try {
      String screenShotFile = new File(uuid.toString()).toString();
      //String[] cmd = {"xvfb-run", "--server-args=\"-screen 0 1024x768x24\"", "node screenshot.js",url, tempFile};
      String cmd = "pageres " + url + " 1024x768 --filename='" + screenShotFile + "'";
      AmazonCloudWatch cloudWatch = AWScred.getCloudWatchClient();
      Long startTime = System.nanoTime();
      Process process = Runtime.getRuntime().exec(new String[] {"bash","-c",cmd});
      process.waitFor();
      System.out.println(process.exitValue());
      Long endTime = (System.nanoTime() - startTime) / 1000000;
      Monitoring.CloudWatchTraffic(cloudWatch, endTime.doubleValue(), "ScreenShoot", "ProcessTime");
      return screenShotFile;
    } catch (IOException e) {e.printStackTrace();
    } catch (InterruptedException e){e.printStackTrace();}
    return null;
  }
}
