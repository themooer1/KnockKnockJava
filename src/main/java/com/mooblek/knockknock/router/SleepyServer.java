package com.mooblek.knockknock.router;

import com.mooblek.knockknock.kubernetes.KubernetesMinecraftServer;
import com.mooblek.knockknock.kubernetes.MinecraftPodException;
import com.mooblek.knockknock.monitoring.RemoteServerPinger;
//import io.kubernetes.client.openapi.ApiException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;

public class SleepyServer {
    public enum STATE {STARTING, RUNNING, STOPPING, STOPPED};

    public STATE state = STATE.STOPPED;
    private RemoteServerPinger monitor;
    private KubernetesMinecraftServer server;
    private final ProxyServer proxy;

    public SleepyServer(ProxyServer p) {
        proxy = p;
    }

    public ServerState getServerInfo() {
        /**
         * Gets the serverInfo to connect to this backend if it is running.
         * Otherwise it starts it.
         */
        System.out.println("Getting Server Info");
        if (state == STATE.STOPPED || state == STATE.STARTING) {

            System.out.println("Stopped or starting");
            if (state == STATE.STOPPED) {
                // Start the server if it is off
                state = STATE.STARTING;
                server = new KubernetesMinecraftServer(proxy);

                // Transition to RUNNING when server comes online
                // But first wait for the pod to start and acquire an IP
                try {
                    server.start(
                            this::startMonitoring,  // If succeeds
                            server -> {
                                System.out.println("Server, " + server.getServerInfo().getName() + " failed to start :(");
                                state = STATE.STOPPED;
                            }
                    );
                }
                catch (MinecraftPodException e) {
                    e.printStackTrace();
                    System.err.println("Failed to spawn Minecraft Server pod :(");

                    // Rollback server state
                    state = STATE.STOPPED;
                }
            }

            // Tell player that the server is starting
            return new ServerState(server.getServerInfo().getName() + " is starting.  Please wait.");
        }

        else {
            // Server is RUNNING so we can provide ServerInfo
            System.out.println("Connecting player to " + server.getServerInfo().getSocketAddress().toString());
            return new ServerState(server.getServerInfo());
        }
    }

    private void startMonitoring(KubernetesMinecraftServer srv) {
        System.out.println("Waiting for " + srv.getServerInfo().getName() + " to finish starting.");
        monitor = new RemoteServerPinger(srv.getServerInfo(), this::transitionServerState);
        monitor.start();
    }

    private void transitionServerState(ServerPing result, Throwable error) {
        // Result is null if the server cannot be reached
        String destName = server.getServerInfo().getName();
        System.out.println("Pinged " + destName);
        if (result == null || result.getPlayers() == null || error != null) {
            System.out.println(destName + " unreachable.");
            // Transition from RUNNING -> STOPPED
            if (state == STATE.RUNNING) {
                state = STATE.STOPPED;
                monitor.stop();
            }
        }
        else if (result != null && error == null){
            System.out.println("Server reached.  state=RUNNING.");
            System.out.println("Server has " + result.getPlayers().getOnline() + " players.");
            state = STATE.RUNNING;
        }
        else {
            System.err.println("?¿?¿?Strange ∆A∆ ping?¿?¿?");
        }
    }
}
