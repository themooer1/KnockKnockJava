package com.mooblek.knockknock.monitoring;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class RemoteServerPinger {
    private final Timer serverPinger = new Timer();
    private final TimerTask pingRemoteServer;

    private final ServerInfo remoteServer;

    public RemoteServerPinger(ServerInfo s, BiConsumer<ServerPing, Throwable> onPing) {
        remoteServer = s;

        // Setup the task which fires a ping job
        pingRemoteServer = new TimerTask() {
            @Override
            public void run() {
                remoteServer.ping(onPing::accept);
            }
        };
    }

    public void start() {
        /**
         * Starts pinging the remote server.
         */

        // Schedule a new ping every 10 seconds
        serverPinger.schedule(pingRemoteServer, 10000l, 10000l);

        System.out.println("Started monitoring " + remoteServer.getName());
    }

    public void stop() {
        /**
         * Stops scheduling new pings.
         */

        serverPinger.cancel();

        System.out.println("Stopped monitoring " + remoteServer.getName());

    }



}
