package com.mooblek.knockknock;

import com.mooblek.knockknock.kubernetes.KubernetesMinecraftPod;
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.openapi.ApiException;
//import io.kubernetes.client.openapi.Configuration;
//import io.kubernetes.client.openapi.apis.CoreV1Api;
//import io.kubernetes.client.openapi.models.V1DeleteOptions;
//import io.kubernetes.client.openapi.models.V1Namespace;
//import io.kubernetes.client.openapi.models.V1NamespaceBuilder;
//import io.kubernetes.client.util.ClientBuilder;
//import io.kubernetes.client.util.Config;
//import io.kubernetes.client.util.KubeConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.FileReader;
import java.io.IOException;

public class KnockKnock extends Plugin {

    @Override
    public void onEnable() {
        getLogger().info("Who's there?");

        // loading kubeconfig from file-system or in-cluster config
//        ApiClient client = null;
//        try {
////            System.getenv().forEach((k,v) -> {System.out.println(k + "=" + v);});
//            System.out.println("Kubernetes service host and port:");
//            System.out.println(System.getenv("KUBERNETES_SERVICE_HOST"));
//            System.out.println(System.getenv("KUBERNETES_SERVICE_PORT"));
//            client = Config.defaultClient();
////            client = Config.fromCluster();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to load Kube config");
//        }
//
//        // set the global default api-client to the in-cluster one from above
//        Configuration.setDefaultApiClient(client);

//        createKubernetesNamespace();

        /* Starts remote servers if not started.  Alerts players. */
        getProxy().getPluginManager().registerListener(this, new TransientServerManager(getProxy()));

//        getProxy().constructServerInfo();
    }

    @Override
    public void onDisable() {
//        destroyKubernetesNamespace();
    }

//    private void createKubernetesNamespace() {
//        ApiClient client = Configuration.getDefaultApiClient();
//        CoreV1Api api = new CoreV1Api(client);
//
//        try {
//            api.createNamespace(
//                    new V1NamespaceBuilder()
//                            .withNewMetadata()
//                                .withName(KubernetesMinecraftPod.KUBERNETES_NAMESPACE)
//                            .endMetadata()
//                        .build(),
//                    null,
//                    null,
//                    null
//            );
//        } catch (ApiException e) {
//            e.printStackTrace();
//            System.err.println("Failed to create ns/" + KubernetesMinecraftPod.KUBERNETES_NAMESPACE);
//        }
//    }
//
//    private void destroyKubernetesNamespace() {
//        ApiClient client = Configuration.getDefaultApiClient();
//        CoreV1Api api = new CoreV1Api(client);
//
//        try {
//            api.deleteNamespace(KubernetesMinecraftPod.KUBERNETES_NAMESPACE,
//                    null,
//                    null,
//                    60,
//                    null,
//                    "Background",
//                    null);
//        } catch (ApiException e) {
//            e.printStackTrace();
//            System.err.println("Failed to cleanup ns/" + KubernetesMinecraftPod.KUBERNETES_NAMESPACE);
//        }
//    }

}
