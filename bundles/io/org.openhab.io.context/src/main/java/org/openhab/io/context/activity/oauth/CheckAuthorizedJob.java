package org.openhab.io.context.activity.oauth;

import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.openhab.io.context.activity.oauth.OAuth2Util.AccessToken;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonParser;

/**
 * A quartz scheduler job to check whether the user/admin has authorized the
 * openHAB Drive persistence addon.
 *
 * @author Tom Deckers
 * @since 1.2.0
 */
@DisallowConcurrentExecution
public class CheckAuthorizedJob implements Job {
	    protected static final String SCHEDULER_GROUP = "CALENDAR_SchedulerGroup";

        static final Logger logger = LoggerFactory.getLogger(CheckAuthorizedJob.class);
       
        @Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
                JobDataMap jobData = context.getMergedJobDataMap();
               
                HttpResponse response = null;
                HttpPost post = (HttpPost) jobData.get("request");
                try {
                        response = OAuth2Util.httpclient.execute(post);
                } catch (ClientProtocolException cpe) {
                        logger.warn("Failed to check authorization status", cpe);
                } catch (IOException ioe) {
                        logger.warn("Failed to check authorization status", ioe);
                }
               
                GenericJson tokenValues = null;
                JsonParser parser;
                try {
                        InputStream is = response.getEntity().getContent();
                        parser = OAuth2Util.factory.createJsonParser(is);
                        tokenValues = parser.parse(GenericJson.class, null);
                        is.close();
                } catch (IllegalStateException ise) {
                        logger.warn("Failed to parse response from authorization check", ise);
                } catch (IOException ioe) {
                        logger.warn("Failed to parse response from authorization check", ioe);
                }
               
                if (tokenValues.containsKey("error")) {
                        String error = (String) tokenValues.get("error");
                        if ("authorization_pending".equalsIgnoreCase(error)) {
                                logger.debug("Waiting for authorization");
                        } else if("slow_down".equalsIgnoreCase(error))  {
                                logger.debug("Need to slow down (Not implemented!)");
                                TriggerKey key = context.getTrigger().getKey();
                                Scheduler scheduler = context.getScheduler();
                                SimpleTrigger newTrigger = newTrigger()
                                            .withIdentity("Check_Authorization", SCHEDULER_GROUP)
                                            .withSchedule(repeatSecondlyForever(20)) // update this value.
                                            .build();
                               
                                //scheduler.rescheduleJob(key, newTrigger)
                        } else {
                                logger.warn("Unknown error: {}", tokenValues.get("error"));
                        }
                } else { // Success!    
                        logger.info("We're authorized.");
                        try {                                  
                                // Stop job.
                                context.getScheduler().deleteJob(context.getJobDetail().getKey());
                        } catch (SchedulerException se) {
                                logger.warn("Failed to stop the check authorize job", se);
                        }
                       
                        AccessToken token = new AccessToken();
                       
                        BigDecimal expires = (BigDecimal) tokenValues.get("expires_in");
                        token.expires = expires.intValue();
                        token.accessToken = (String) tokenValues.get("access_token");
                        token.tokenType = (String) tokenValues.get("token_type");
                        token.refreshToken = (String) tokenValues.get("refresh_token");
                       
                        // We'll check for this result in the listener (see DriveConfigurationImpl)
                        context.setResult(token);
                }
        }
       
}

