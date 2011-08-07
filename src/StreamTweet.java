import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Set;

import com.mongodb.*;


public class StreamTweet {

    public static void main(String[] args) throws IOException, TwitterException {
    	 try{
    		     TwitterStream twitter = new TwitterStreamFactory().getInstance();
		     File file = new File("twitter4j.properties");
		     Properties prop = new Properties();
		     InputStream is = null;
		     OutputStream os = null;
		       try {
		           if (file.exists()) {
		               is = new FileInputStream(file);
		               prop.load(is);
		           }
		           if (args.length < 2) {
		               if (null == prop.getProperty("oauth.consumerKey")&& null == prop.getProperty("oauth.consumerSecret")) {
		                    System.exit(-1);
		              }
		           } else {
		               prop.setProperty("oauth.consumerKey", args[0]);
		               prop.setProperty("oauth.consumerSecret", args[1]);
		               os = new FileOutputStream("twitter4j.properties");
		               prop.store(os, "twitter4j.properties");
		           }
		        } catch (IOException ioe) {
		            ioe.printStackTrace();
		            System.exit(-1);
		        } finally {
		           if (null != is) {
		               try {
		                   is.close();
		               } catch (IOException ignore) {
		               }
		           }
		           if (null != os) {
		               try {
		                   os.close();
		               } catch (IOException ignore) {
		               }
		           }
		       }
		       try {
		           RequestToken requestToken = twitter.getOAuthRequestToken();
		           AccessToken accessToken = null;
		
		           BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		           while (null == accessToken) {
		              System.out.println("Open the following URL and grant access to your account:");
		              System.out.println(requestToken.getAuthorizationURL());
		              System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
		              String pin = br.readLine();
		               try {
		                   if (pin.length() > 0) {
		                       accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		                   } else {
		                       accessToken = twitter.getOAuthAccessToken(requestToken);
		                   }
		               } catch (TwitterException te) {
		                   if (401 == te.getStatusCode()) {
		                       System.out.println("Unable to get the access token.");
		                   } else {
		                       te.printStackTrace();
		                   }
		               }
		           }
		          } catch (IllegalStateException ie) {
		           if (!twitter.getAuthorization().isEnabled()) {
		               System.out.println("OAuth consumer key/secret is not set.");
		               System.exit(-1);
		           }
		       }
		       StatusListener listener = new StatusListener(){
		           public void onStatus(Status status) {
		                System.out.println(status.getUser().getName() + " : " + status.getText());
		            }
		            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		       	System.out.println("Status Deleted : "  +statusDeletionNotice.getStatusId());
		            }
		            public void onTrackLimitationNotice(int numberOfLimitedStatuses)	 {}
		            public void onException(Exception ex) {
		                ex.printStackTrace();
		            }
					@Override
					public void onScrubGeo(long arg0, long arg1) {
						// TODO Auto-generated method stub
						
					}
		        };
		        FilterQuery qr=new FilterQuery();
		        qr.count(0);
		        qr.track(new String[]{"arsenal"});
		        twitter.addListener(listener);
		        twitter.sample();
		        twitter.filter(qr);
        	} catch (UnknownHostException e) {
	    		System.out.println(e.toString());
	    	} catch (MongoException e) {
	    		System.out.println(e.toString());
	    	}
    	
    }

}

