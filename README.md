Amazon aws project, written in Java.
Project main idea:
A application that import tweets from twitter using twitter api, then filter only tweets with URL in it.
Save tweets content to AWS RDS database, and attach screen shot of the URL using AWS S3.
Users can watch all the tweets from the database in web (Json format).

Project structure:
1. TwitterListener: 

Authenticate with twitter using given keys.
Import tweets from twitter using twitter4j, a library for Java.
filter tweets with URL.
Send tweets to AWS SQS service.

2. LinkListener:
Listen to AWS SQS service for tweets that has been sent from TwitterListener.
For each tweet that received from SQS extract the content as below:
open link attached, and save the URL title, description, content, screen shot url to AWS RDS database
create a screen shot and save file to AWS S3 service.

3. SearchResultsServer: 
A server on port 8080 that show the database's content in json format.
EC2_IP:8080
 
How to use:
1. You need to have 2 files on your machine:
	a. TwitterFeeder.jar file
	b. run.sh configuration file.

2. in run.sh configuration file make sure all values are correct (config.twitter.track as your choice)

3. create 3 instances of terminal:
	a. ./run.sh TwitterListener // Start the tweets importer.
	b. ./run.sh LinkListener // Start the process to save tweets to database.
	c. ./run.sh SearchResults // Start server, enter "EC2_IP:8080" in your browser to watch database's content in json format.