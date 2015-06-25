package org.dhbw.geo.hardware;

import android.telephony.SmsManager;

/**
 * Created by Oliver on 13.06.2015.
 */
public class SMSFactory {

    /**
     * Sends an SMS to the specified number with the specified text.
     * @param number The telephone number
     * @param text The text to send
     */
    public static void createSMS(String number, String text) {
        if (number.length() == 0 || text.length() == 0)
            return;

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, text, null, null);
    }

}
