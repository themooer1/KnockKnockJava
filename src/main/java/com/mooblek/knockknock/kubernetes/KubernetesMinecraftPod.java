package com.mooblek.knockknock.kubernetes;
//
//import io.kubernetes.client.custom.Quantity;
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.openapi.ApiException;
//import io.kubernetes.client.openapi.Configuration;
//import io.kubernetes.client.openapi.apis.CoreV1Api;
//import io.kubernetes.client.openapi.models.*;
//import io.kubernetes.client.util.Config;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.function.Consumer;

//import io.kubernetes.client.util.Yaml;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

//import net.md_5.bungee.BungeeServerInfo;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.jetbrains.annotations.Nullable;


public class KubernetesMinecraftPod {

    public static final String KUBERNETES_NAMESPACE;

    static {
        String NS;
        final String DEFAULT_NS = "autocraft";
        final String IN_CLUSTER_NS_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/namespace";

        System.out.println("Getting Current Namespace");

        try {
            BufferedReader b = new BufferedReader(
                    new FileReader(
                            new File(IN_CLUSTER_NS_FILE)
                    )
            );

            NS = b.readLine();

        } catch (IOException e) {
            System.out.printf("%s not found or unreadable.\nUsing default namespace: %s\n", IN_CLUSTER_NS_FILE, DEFAULT_NS);
            NS = DEFAULT_NS;
        }

        KUBERNETES_NAMESPACE = NS;
    }


//    private static CoreV1Api api;
//    private V1Pod minecraftServerPod;
    private Pod minecraftServerPod;

    private static int nextPodID = 0;

    // Get Kubernetes API instance
    static final KubernetesClient client;
    static {
        try {
            client = new DefaultKubernetesClient();
//            client = Config.defaultClient();
        } catch (KubernetesClientException e) {
            e.printStackTrace();
            throw new RuntimeException("Kubernetes API connection failed!");
        }
    }


    private static final Pod minecraftPodTemplate;
    static {
        File templateFile = new File("minecraftPodTemplate.yaml");

        System.out.println("Looking for minecraftPodTemplate.yaml in " + System.getProperty("user.dir") + '/');
        if (!templateFile.exists()) {
            System.err.println("Cannot load minecraftPodTemplate.yaml");
            System.err.println("Make sure it is placed in the root of the BungeeCord directory (not config/)");
            throw new RuntimeException("minecraftPodTemplate.yaml missing or unreadable!");
        }

        minecraftPodTemplate = client.pods().load(templateFile).get();

    }


    public KubernetesMinecraftPod(Consumer<KubernetesMinecraftPod> onPodReady, Consumer<KubernetesMinecraftPod> onPodFailed) throws MinecraftPodException{

        final String minecraftPodName = "minecraft-" + KUBERNETES_NAMESPACE + "-" + nextPodID++;

        // Watch Pod until it is Running with IP address (spawned below)
        SharedInformerFactory sharedInformerFactory = client.informers();
        SharedIndexInformer<Pod> podInformer = sharedInformerFactory.sharedIndexInformerFor(Pod.class, PodList.class, 5 * 1000L);

        KubernetesMinecraftPod self = this;
        podInformer.addEventHandler(
                new ResourceEventHandler<Pod>() {
                    @Override
                    public void onAdd(Pod pod) {
                        System.out.printf("Pod, %s, added.  IP: %s\n", pod.getMetadata().getName(), pod.getStatus().getPodIP());
                    }

                    @Override
                    public void onUpdate(Pod oldPod, Pod newPod) {

                        ObjectMeta meta = newPod.getMetadata();
                        if (meta.getName().equals(minecraftPodName) && meta.getNamespace().equals(KUBERNETES_NAMESPACE)) {
                            String name = newPod.getMetadata().getName();
                            String phase = newPod.getStatus().getPhase();
                            String IP = newPod.getStatus().getPodIP();
                            System.out.println("Waiting for pods/" + name + " in " + phase + " phase with IP=" + IP);

                            if (phase.equals("Running") && IP != null) {
                                onPodReady.accept(self);
                                sharedInformerFactory.stopAllRegisteredInformers();
                            } else if (phase.equals("Failed") || phase.equals("Unknown")) {
                                onPodFailed.accept(self);
                                sharedInformerFactory.stopAllRegisteredInformers();
                            }
                        }

                    }

                    @Override
                    public void onDelete(Pod pod, boolean deletedFinalStateUnknown) {
                        System.out.printf("Pod, %s, deleted.\n", pod.getMetadata().getName());
                    }
                }
        );

        sharedInformerFactory.startAllRegisteredInformers();

        // Start minecraft server pod
        minecraftServerPod = spawnServerPod(minecraftPodName);


    }

    public KubernetesMinecraftPod(Pod minecraftServerPod) {
        this.minecraftServerPod = minecraftServerPod;

        // Check minecraftServerPod exists
        refreshServerPod();

    }

    public String getName() {
        return minecraftServerPod.getMetadata().getName();
    }

    public PodStatus getStatus() {
        return minecraftServerPod.getStatus();
    }

    public String getIP() {
        if (minecraftServerPod.getStatus() != null && minecraftServerPod.getStatus().getPodIP() == null) {
            refreshServerPod();
        }
        return (minecraftServerPod.getStatus() != null) ? minecraftServerPod.getStatus().getPodIP() : null;
    }


    public void terminate() {
        client.pods().inNamespace(KUBERNETES_NAMESPACE).delete(minecraftServerPod);
    }

    private void refreshServerPod() {
        // TODO: Might have to handle IP is null
        minecraftServerPod = client.pods().inNamespace(KUBERNETES_NAMESPACE).withName(minecraftServerPod.getMetadata().getName()).get();
    }

    private Pod spawnServerPod(String name) {

        List<EnvVar> environment = new LinkedList<>();

        // Passthrough environment variables
        environment.addAll(MinecraftDockerEnv.localEnvThatMatch());

        environment.addAll(Arrays.asList(
//                new V1EnvVar().name("EULA").value("TRUE"),
//                new V1EnvVar().name("MEMORY").value("1G"),
                new EnvVarBuilder().withName("BROADCAST_CONSOLE_TO_OPS").withValue("TRUE").build(),
                new EnvVarBuilder().withName("BROADCAST_RCON_TO_OPS").withValue("TRUE").build(),
                new EnvVarBuilder().withName("PLAYER_IDLE_TIMEOUT").withValue("60").build(),
                new EnvVarBuilder().withName("ONLINE_MODE").withValue("FALSE").build(),
                new EnvVarBuilder().withName("ENABLE_AUTOPAUSE").withValue("TRUE").build(),
                new EnvVarBuilder().withName("AUTOPAUSE_TIMEOUT_INIT").withValue("300").build(),
                new EnvVarBuilder().withName("AUTOPAUSE_TIMEOUT_EST").withValue("300").build()
        ));



        Pod minecraftServerPod = new PodBuilder(minecraftPodTemplate)
                .editMetadata()
                    .withName(name)
                    .withNamespace(KUBERNETES_NAMESPACE)
                .endMetadata()
                .editSpec()
                    .editMatchingContainer(c -> c.getName().equals("minecraft"))
                        .addAllToEnv(environment)
                    .endContainer()
                .endSpec()
                .build();

        System.out.println("Spawning pod with spec:\n" + minecraftServerPod.toString());

        minecraftServerPod = client.pods().inNamespace(KUBERNETES_NAMESPACE).create(minecraftServerPod);

        return minecraftServerPod;
    }

}
