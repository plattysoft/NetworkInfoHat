package com.example.networkinfohat;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void splitString() throws Exception {
        Marquee m = new Marquee();
        String res = m.splitString("string", 0);
        assertEquals("stri", res);
        res = m.splitString(" 172.1", 0);
        assertEquals(" 172.", res);
        res = m.splitString("172.10", 0);
        assertEquals("172.1", res);
        res = m.splitString("2.1.0.1.1", 0);
        assertEquals("2.1.0.1.", res);
        res = m.splitString("2..0..1", 0);
        assertEquals("2..0..", res);
    }
}