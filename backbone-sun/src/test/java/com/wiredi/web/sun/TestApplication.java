package com.wiredi.web.sun;

import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;

public class TestApplication {

    public static void main(String[] args) throws InterruptedException {
        WiredApplicationInstance application = WiredApplication.start();
        application.awaitCompletion();
    }
}
