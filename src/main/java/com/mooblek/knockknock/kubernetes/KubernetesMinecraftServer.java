package com.mooblek.knockknock.kubernetes;

import com.mooblek.knockknock.router.ServerState;
//import io.kubernetes.client.openapi.ApiException;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class KubernetesMinecraftServer{
    KubernetesMinecraftPod pod;
    ProxyServer proxy;

    public KubernetesMinecraftServer(ProxyServer p) {
        proxy = p;
    }

    public void start(Consumer<KubernetesMinecraftServer> onServerReady, Consumer<KubernetesMinecraftServer> onServerFailed) throws MinecraftPodException{
        // Send callback when pod is running with IP
        System.out.println("Starting POD");
        pod = new KubernetesMinecraftPod(
                pod -> {
//                  System.err.println("POD READY");
                    System.out.println("Pod " + pod.getName() + " running with IP " + pod.getIP());
                    onServerReady.accept(this);
                },
                pod -> {
                    System.out.println("Pod " + pod.getName() + " failed to start!\n" +
                            "Phase: " + pod.getStatus().getPhase() + "\n" +
                            "Reason: " + pod.getStatus().getReason()
                    );

                    onServerFailed.accept(this);
                }
        );
    }

    public ServerInfo getServerInfo() {

        String name = pod.getName();
        String IP = (pod.getIP() != null) ? pod.getIP() : "0.0.0.0";

        // Check if pod has been allocated an IP yet
        assert IP != null;

        return proxy.constructServerInfo(
                name,
                new InetSocketAddress(IP,25565),
//                Util.getAddr(IP + ":25565"),
                "An autocraft Server :)))))))))))))",
                false
        );
    }
}
