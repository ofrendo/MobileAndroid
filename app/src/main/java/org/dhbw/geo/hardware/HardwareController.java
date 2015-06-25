package org.dhbw.geo.hardware;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.MainActivity;

/**
 * Created by Oliver on 23.05.2015.
 * Class used to control hardware settings such as Wifi, Bluetooth and Audio.
 */
public class HardwareController {

    public static final int AUDIO_ON = 2;
    public static final int AUDIO_VIBRATE = 1;
    public static final int AUDIO_MUTE = 0;

    private static HardwareController instance;
    private Context context;

    private HardwareController() {
       context = ContextManager.getContext();
    }

    /**
     * Access point for the singleton
     * @return
     */
    public static HardwareController getInstance() {
        if (instance == null) {
            instance = new HardwareController();
        }
        return instance;
    }

    /**
     * Returns the current status of the Wifi setting.
     * @return True: Wifi is on, false: Wifi is off
     */
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * Sets the status of the Wifi setting.
     * @param newStatus True: Turn Wifi on, false: Turn Wifi off
     */
    public void setWifi(boolean newStatus) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean currentStatus = wifiManager.isWifiEnabled();
        if (currentStatus == newStatus) {
            // New status is like the old one, no need to change
            return;
        }

        wifiManager.setWifiEnabled(newStatus);
    }

    /**
     * Gets the current status of a specific audio stream
     * @param stream stream ID: AudioManager.STREAM_MUSIC, AudioManager.STREAM_RING, AudioManager.STREAM_ALARM
     * @return true for sound on, false for mute
     */
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
        //COULD SET IT TO VIBRATE LIKE THIS
        //audioManager.setStreamVolume(stream, someVolumeNumber, AudioManager.FLAG_VIBRATE);
    }

    /**
     * Sets the volume for a certain audio stream. Will also unmute the stream.
     * @param stream stream Stream ID: AudioManager.STREAM_MUSIC, AudioManager.STREAM_RING, AudioManager.STREAM_ALARM
     * @param volume
     */
    public void setAudioVolume(int stream, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        audioManager.setStreamMute(stream, false);
        audioManager.setStreamVolume(stream, volume, 0);
    }

    /**
     * Returns the current status of the Bluetooth setting.
     * @return True: Bluetooth is on, false: Bluetooth is off
     */
    public boolean getBluetoothStatus() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Sets the status of the Bluetooth setting.
     * @param status True: Turn Bluetooth on, false: Turn Bluetooth off
     */
    public void setBluetoothStatus(boolean status) {
        if (status == true && getBluetoothStatus() == false) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }
        else if (status == false && getBluetoothStatus() == true) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }



}
