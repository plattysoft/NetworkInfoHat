package com.example.networkinfohat;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.pio.Gpio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private Marquee mMarquee = new Marquee();
    private ButtonInputDriver mInputDriver;

    @Override
    protected void onStart() {
        super.onStart();

        // Click on the buttons can retry network
        try {
            mInputDriver = new ButtonInputDriver(RainbowHat.BUTTON_A,
                    RainbowHat.BUTTON_LOGIC_STATE,
                    KeyEvent.KEYCODE_A);
            mInputDriver.register();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // We should register for any network change broadcast message
        /*
            Apps targeting Android 7.0 (API level 24) and higher do not receive CONNECTIVITY_ACTION
            broadcasts if they declare the broadcast receiver in their manifest.
            Apps will still receive CONNECTIVITY_ACTION broadcasts if they register their BroadcastReceiver
            with Context.registerReceiver() and that context is still valid
            */
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                updateNetworkStatus();
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                updateNetworkStatus();
            }
        });
        updateNetworkStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
        mInputDriver.unregister();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_A) {
            updateNetworkStatus();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateNetworkStatus() {
        // Read the network connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If no network, show ERR-
        if (!isConnected) {
            mMarquee.stop();
            showError();
        }
        else {
            // If network, show the IP on a marquee mode on the LCD
            String currentIp = getIPAddress(true);
            mMarquee.displayText(currentIp);
        }
    }

    private void showError() {
        try {
            AlphanumericDisplay display = RainbowHat.openDisplay();
            display.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
            display.setEnabled(true);
            display.display("ERR-");
            display.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }
}
