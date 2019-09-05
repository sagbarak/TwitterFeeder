package il.ac.colman.cs.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


// Generate the instances of all the AWS services that I'm using.
public class AWScred {
    public static AmazonSQS getSQSclient() {
        BasicSessionCredentials session = new BasicSessionCredentials(System.getProperty("aws.accessKeyId"),
                System.getProperty("aws.secretKey"), System.getProperty("aws.sessionToken"));
        AmazonSQS client = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(session))
                .withRegion(Regions.US_EAST_1).build();
        return client;
    }

    public static AmazonS3 getS3Client() {
        BasicSessionCredentials session = new BasicSessionCredentials(System.getProperty("aws.accessKeyId"),
                System.getProperty("aws.secretKey"), System.getProperty("aws.sessionToken"));
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(session)).withRegion(Regions.EU_CENTRAL_1).build();
        return amazonS3;
    }


    public static AmazonCloudWatch getCloudWatchClient() {
        BasicSessionCredentials session = new BasicSessionCredentials(System.getProperty("aws.accessKeyId"),
                System.getProperty("aws.secretKey"), System.getProperty("aws.sessionToken"));
        AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(session)).withRegion(Regions.US_EAST_1).build();
        return amazonCloudWatch;
    }
}
