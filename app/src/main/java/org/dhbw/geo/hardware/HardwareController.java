package org.dhbw.geo.hardware;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

import org.dhbw.geo.ui.MainActivity;

/**
 * Created by Oliver on 23.05.2015.
 */
public class HardwareController {

    private static HardwareController instance;
    private Context context;

    private HardwareController() {
       context = MainActivity.getContext();
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

    public boolean getAudioStatus(int stream) {
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(stream) != 0;
    }

    /**
     * Sets the status for a certain audio stream.
     * @param stream Stream ID: AudioManager.STREAM_MUSIC, AudioManager.STREAM_RING, AudioManager.STREAM_ALARM
     * @param status true for sound on, false for mute
     */
    public void setAudioStatus(int stream, boolean status) {
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        audioManager.setStreamMute(stream, status); //Set mute or unmute
    }

    public boolean getBluetoothStatus() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public void setBluetoothStatus(boolean status) {
        if (status == true && getBluetoothStatus() == false) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }
        else if (status == false && getBluetoothStatus() == true) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }



}
