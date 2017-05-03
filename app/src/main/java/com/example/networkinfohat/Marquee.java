package com.example.networkinfohat;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rportales on 03/05/2017.
 */

class Marquee {
    private static final long MARQUEE_INTERVAL = 400;
    private static final int LCD_LENGTH = 4;
    public static final String PADDING = "    ";

    private Timer mTimer;
    private int mCurrentMarqueePos;
    private String mMarqueeText;

    public void displayText(String text) {
        mMarqueeText = PADDING+text+ PADDING;
        mCurrentMarqueePos = 0;
        stop();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMarquee();
            }
        }, 0, MARQUEE_INTERVAL);
    }

    private void updateMarquee() {
        // In this display, the character '.' does not count as such (unless consecutive)
        // The length of the string to dispay depends on the number of dots there
        if (mMarqueeText.charAt(mCurrentMarqueePos) == '.') {
            mCurrentMarqueePos++;
        }
        String displayText = splitString(mMarqueeText, mCurrentMarqueePos);
        try {
            AlphanumericDisplay display = RainbowHat.openDisplay();
            display.display(displayText);
            display.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentMarqueePos++;
        if (mCurrentMarqueePos + displayText.length() >= mMarqueeText.length()) {
            mCurrentMarqueePos = 0;
        }
    }

    public String splitString(String string, int startPos) {
        int currentPos = startPos;
        for (int numProperCharacters=0; numProperCharacters < LCD_LENGTH; numProperCharacters++) {
            // A dot after the current character is considered part of the character
            if (string.length() > currentPos+1 && string.charAt(currentPos+1) == '.') {
                currentPos++;
            }
            currentPos++;
        }
        return string.substring(startPos, currentPos);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }
}
