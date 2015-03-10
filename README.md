# TwitterVisualizer

This is a simple tool to visualize tweets happening in the vicinity of 5 km of your location in real-time. 

Build steps:
Enter the Oauth keys of Twitter (get them from https://dev.twitter.com/oauth/application-only) in the TwitterReader.java class, build (using maven), deploy (on a suitable web container) and run the url
http://localhost:port/twitterVisualizer/home
Click on 'Start Visualizer' button to see.
The visualiser's center circle represents the user and the other circles represent nearby tweets.

Only those tweets are read whose geocoding information is available and falls within the 5 km bounding box.

![Twitter Visualizer](https://github.com/zafar142007/TwitterVisualizer/blob/master/TwitterVisualizer/src/Capture.PNG?raw=true "Screenshot")
