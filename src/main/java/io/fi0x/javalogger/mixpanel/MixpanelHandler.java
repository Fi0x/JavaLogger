package io.fi0x.javalogger.mixpanel;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import io.fi0x.javalogger.logging.LogEntry;
import io.fi0x.javalogger.logging.Logger;
import io.fi0x.javalogger.logging.LogColor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MixpanelHandler
{
    private static Thread uploader = null;
    private static long updateDelay = 5000;

    private static MessageBuilder builder;
    private static ClientDelivery delivery;

    private static String projectToken = null;
    private static String userID = null;

    private static final Map<String, String> defaultProperties = new HashMap<>();

    private MixpanelHandler()
    {
    }

    /**
     * Send all collected messages to Mixpanel immediately.
     * This will skip the delay between mixpanel messages.
     * @return True if delivery was successful, False if there is no delivery or another problem occured.
     */
    public static boolean sendMessages()
    {
        if(delivery == null)
            return false;

        if(uploader != null)
            uploader.interrupt();

        return sendDelivery() == null;
    }

    /**
     * Add a new message to the Mixpanel delivery queue.
     * Adding a message requires the uniqueID and projectToken to be set.
     * @param eventName The name of the event. This will also be visible on Mixpanel.
     *                  This must not be null.
     * @param properties The properties for this message.
     *                   These will be visible and filterable on Mixpanel.
     * @return True if the message was successfully added to the queue, False if some information was missing.
     */
    public static boolean addMessage(String eventName, Map<String, String> properties)
    {
        if(eventName == null || userID == null || projectToken == null)
        {
            LogEntry l = new LogEntry("Could not add Mixpanel-event to queue. Name, UserID or ProjectToken is null")
                    .COLOR(LogColor.RED)
                    .LEVEL("ERR")
                    .CODE(0)
                    .FILE_ENTRY(false);
            Logger.log(l);
            return false;
        }

        if(properties == null)
            properties = new HashMap<>();
        properties.putAll(defaultProperties);

        JSONObject props = new JSONObject();
        for(Map.Entry<String, String> property : properties.entrySet())
        {
            try
            {
                props.put(property.getKey(), property.getValue());
            } catch(JSONException ignored)
            {
            }
        }

        if(delivery == null)
            delivery = new ClientDelivery();

        delivery.addMessage(getBuilder().event(userID, eventName, props));
        startUploaderThread();
        return true;
    }

    /**
     * Properties that are added with this method will be appended to all future messages that are sent to Mixpanel.
     * This is useful for version information and other things that do not change on runtime.
     * @param propertyName The name of the property that will be visible on Mixpanel.
     * @param propertyValue The value of the property.
     * @return True if the property was added, False if a property with this name already exists.
     */
    public static boolean addDefaultProperty(String propertyName, String propertyValue)
    {
        if(defaultProperties.containsKey(propertyName))
            return false;

        defaultProperties.put(propertyName, propertyValue);
        return true;
    }
    /**
     * Set the minimum millisecond delay between each mixpanel delivery.
     * @param minMillisBetweenMessages The time in millis between each mixpanel delivery that should be sent (Min 500, Default is 5000).
     * @return True if the new delay was set, False if the delay was too short.
     */
    public static boolean setMilliDelay(long minMillisBetweenMessages)
    {
        if(minMillisBetweenMessages < 500)
            return false;

        updateDelay = minMillisBetweenMessages;
        return true;
    }

    /**
     * Set the project-token that can be obtained from Mixpanel.
     * This is required to successfully send messages to Mixpanel.
     * @param mixpanelProjectToken The token of you Mixpanel project.
     */
    public static void setProjectToken(String mixpanelProjectToken)
    {
        projectToken = mixpanelProjectToken;
    }
    /**
     * Set a distinct ID for all Mixpanel messages sent from this application-instance.
     * You can use a username or UUID to identify users and track them on Mixpanel.
     * @param distinctMixpanelID The distinctID that is added to each Mixpanel message.
     */
    public static void setUniqueUserID(String distinctMixpanelID)
    {
        userID = distinctMixpanelID;
    }

    private static MessageBuilder getBuilder()
    {
        if(builder == null)
            builder = new MessageBuilder(projectToken);
        return builder;
    }

    private static void startUploaderThread()
    {
        if(uploader == null)
        {
            uploader = new Thread(() ->
            {
                try
                {
                    Thread.sleep(updateDelay);
                } catch(InterruptedException e)
                {
                    uploader = null;
                    return;
                }

                IOException e = sendDelivery();
                if(e != null)
                {
                    LogEntry l = new LogEntry("Could not upload a Mixpanel delivery")
                            .COLOR(LogColor.RED)
                            .LEVEL("ERR")
                            .CODE(0)
                            .FILE_ENTRY(false);
                    Logger.log(l);
                    e.printStackTrace();
                }

                uploader = null;
            });
        }

        if(!uploader.isAlive())
            uploader.start();
    }

    private static IOException sendDelivery()
    {
        try
        {
            new MixpanelAPI().deliver(delivery);
        } catch(IOException e)
        {
            return e;
        }

        delivery = null;
        return null;
    }
}
