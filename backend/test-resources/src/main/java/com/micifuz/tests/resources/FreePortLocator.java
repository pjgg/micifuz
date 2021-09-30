package com.micifuz.tests.resources;

import java.io.IOException;
import java.net.ServerSocket;

public class FreePortLocator {

    public static int getFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        final int port = socket.getLocalPort();
        socket.close();
        return port;
    }
}
