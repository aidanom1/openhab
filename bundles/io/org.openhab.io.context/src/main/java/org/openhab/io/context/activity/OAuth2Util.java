package org.openhab.io.context.activity;

/* Much of this is taken from the same filename in the google drive persistence package */
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class OAuth2Util {
       
        static final Logger logger = LoggerFactory.getLogger(OAuth2Util.class);

        // Obtained using the Google APIs Console -
        // https://code.google.com/apis/console
        private static final String CLIENT_ID = "159084007310-g40dj4li524jjm45nv9odr6p9bpmu3l6.apps.googleusercontent.com";
        private static final String CLIENT_SECRET = "rh16QI6tvk8WZ87gAk7Vv9Ce";
        private static final String SCOPE = "https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly";
        private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
       
        // Utility objects
        static ApacheHttpTransport transport = new ApacheHttpTransport();
        static HttpClient httpclient = transport.getHttpClient();
        static JacksonFactory factory = new JacksonFactory();
       
        public static UserToken getUserToken(Properties properties) {
                // Authentication to the services is accomplished through HTTP requests
                // see https://developers.google.com/accounts/docs/OAuth2ForDevices
                // OAuth 2.0 for devices doesn't seem to be available in libraries yet.
       
                UserToken token = null;
               
                 BasicNameValuePair[] params = {
                            new BasicNameValuePair("client_id", CLIENT_ID),
                            new BasicNameValuePair("scope", SCOPE)
                 };
               
                HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/device/code");
                post.setHeader("Host", "accounts.google.com");
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                UrlEncodedFormEntity urlEncodedFormEntity = null;
                try {
                        urlEncodedFormEntity = new UrlEncodedFormEntity(Arrays.asList(params));
                } catch (UnsupportedEncodingException uee) {
                        logger.error("Exception getting user tokens", uee);
                }
                post.setEntity(urlEncodedFormEntity);
               
                HttpResponse response = null;
               
                try {
                        response = httpclient.execute(post);
                } catch (ClientProtocolException cpe) {
                        logger.error("Failed to get user tokens from server", cpe);
                } catch (IOException ioe) {
                        logger.error("Failed to get user tokens from server", ioe);
                }
               
                GenericJson userCodeValues = null;
               
                if (response != null) {
                        try {
                                InputStream is = response.getEntity().getContent();
                                JsonParser parser = factory.createJsonParser(is);
                                userCodeValues = parser.parse(GenericJson.class, null);
                                is.close();
                               
                                token = new UserToken();
                                BigDecimal expires = (BigDecimal) userCodeValues.get("expires_in");
                                token.expires = expires.intValue();
                                BigDecimal interval = (BigDecimal) userCodeValues.get("interval");
                                token.interval = interval.intValue();
                                token.deviceCode = (String) userCodeValues.get("device_code");
                                token.verificationUrl = (String) userCodeValues.get("verification_url");
                                token.userCode = (String) userCodeValues.get("user_code");
                               
                                // Important! Display data in the log.  Users need this to authorize the service.
                                logger.info("\n\nAuthorize openHAB Context Aware Services to use your Google account to access calendar data\n" +
                                                "Visit " + token.verificationUrl + " and enter user code " +
                                                token.userCode + "\n\nThanks!!");
                               
                        } catch (IOException ioe) {
                                logger.error("Failed to get user code from response", ioe);
                        }
                }
                return token;
        }      
       
        /**
         * obtain access token once the user has authorized the Drive persistence addon online.
         * This method kicks off a job that periodically checks authorization state.
         * @param listener
         */
        public static void getAccessToken(Properties properties, JobListener listener) {
                logger.debug("Getting access token");
                               
                BasicNameValuePair[] params2 = {
                            new BasicNameValuePair("client_id", CLIENT_ID),
                            new BasicNameValuePair("client_secret", CLIENT_SECRET),
                            new BasicNameValuePair("code", properties.getProperty("device_code")),
                            new BasicNameValuePair("grant_type", "http://oauth.net/grant_type/device/1.0")
                 };
                       
                HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
                post.setHeader("Host", "accounts.google.com");
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                UrlEncodedFormEntity urlEncodedFormEntity = null;
                try {
                        urlEncodedFormEntity = new UrlEncodedFormEntity(Arrays.asList(params2));
                } catch (UnsupportedEncodingException uee) {
                        logger.error("Shouldn't happen, hardcoded values.", uee);
                }
                post.setEntity(urlEncodedFormEntity);
               
                try {          
                        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                        // Listener will check result (access token) upon job completion.
                        scheduler.getListenerManager().addJobListener(listener);
                       
                        // schedule job
                        JobDetail job = newJob(CheckAuthorizedJob.class)
                                .withIdentity("Check_Authorization", CheckAuthorizedJob.SCHEDULER_GROUP)
                            .build();                  

                        job.getJobDataMap().put("request", post);
                       
                        String interval = properties.getProperty("interval", "15");
                       
                        SimpleTrigger trigger = newTrigger()
                            .withIdentity("Check_Authorization", CheckAuthorizedJob.SCHEDULER_GROUP)
                            // we'll be slower than we have to.  Google can thank us later.
                            .withSchedule(repeatSecondlyForever(Integer.parseInt(interval)))
                            .build();
                       
                        scheduler.scheduleJob(job, trigger);
                        logger.debug("Scheduled CheckAuthorizedJob with interval {} sec.", interval);
                       
                } catch (SchedulerException se) {
                        logger.error("Failed to schedule job to check authorization", se);
                }
        }
       
        public static GoogleCredential createCredential(String accessToken, Long expires, String refreshToken) {
                GoogleCredential credential = new GoogleCredential.Builder().
                                setTransport(transport).
                                setJsonFactory(factory).
                                setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();
                credential.setAccessToken(accessToken).
                        setExpiresInSeconds(expires).
                        setRefreshToken(refreshToken);
                return credential;
        }
       
        public static class UserToken {
                int expires;
                int interval;
                String deviceCode;
                String verificationUrl;
                String userCode;
        }
       
        public static class AccessToken {
                public int expires;
                public String accessToken;
                public String tokenType;
                public String refreshToken;            
        }

}
