package org.dhbw.geo.hardware;

import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

/**
 * Created by Oliver on 23.05.2015.
 */
public class HardwareController {

    private static HardwareController instance;
    private Context context;

    private HardwareController() {

    }

    public static HardwareController getInstance() {
        if (instance == null) {
            instance = new HardwareController();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public void setWifi(boolean newStatus) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean currentStatus = wifiManager.isWifiEnabled();
        if (currentStatus == newStatus) {
            // New status is like the old one, no need to change
            return;
        }

        wifiManager.setWifiEnabled(newStatus);
    }

    public boolean getStatus(int stream) {
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(stream) != 0;
    }

    public void setStatus(int stream, boolean status) {
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        audioManager.setStreamMute(stream, status); //Set mute or unmute
        audioManager.set
    }

}
