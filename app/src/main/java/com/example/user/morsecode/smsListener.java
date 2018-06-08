package com.example.user.morsecode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            String[] info = new String[] {"", ""};
            Object[] pdus = (Object[])intent.getExtras().get("pdus");
            StringBuilder text = new StringBuilder();

            SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            info[0] = shortMessage.getOriginatingAddress();

            for(int i = 0; i < pdus.length; i++){
                shortMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                text.append(shortMessage.getDisplayMessageBody());
            }
            info[1] = text.toString();

            verifyMorse(info[0], info[1]);
        }
    }

    public void verifyMorse(String info, String text){
        String pattern = "[\\. -]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.find())
            new Message().update(info, text);
    }
}
