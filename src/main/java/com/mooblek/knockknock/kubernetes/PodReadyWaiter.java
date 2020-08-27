//package com.mooblek.knockknock.kubernetes;
//
//import io.kubernetes.client.openapi.ApiException;
//import io.kubernetes.client.openapi.apis.CoreV1Api;
//import io.kubernetes.client.openapi.models.V1Pod;
//import io.kubernetes.client.openapi.models.V1PodStatus;
//
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.function.Consumer;
//
//public class PodReadyWaiter {
//    private CoreV1Api api;
//    private V1Pod pod;
//
//    Timer podCheckTimer = new Timer();
//    TimerTask podReadyChecker;
//
//    PodReadyWaiter(CoreV1Api api, V1Pod pod, Consumer<V1Pod> onReady, Consumer<V1Pod> onFail) {
//        /**
//         * Watch a pod until it is RUNNING with an IP address
//         * @param pod Pod metadata MUST specify a name and namespace
//         */
//
//        String name = pod.getMetadata().getName();
//        String namespace = pod.getMetadata().getNamespace();
//
//        // Require Name and default Namespace to "default"
//        if (name == null)
//            throw new RuntimeException("Pod MUST be named");
//        if (namespace == null) {
//            throw new RuntimeException("Pod MUST specify a namespace");
//        }
//
//        // Create a Task which waits for the a pod to start
//        podReadyChecker = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Checking pods/" + name + " in ns/"+ namespace);
//                try {
//                    // Get pod status from API
//                    V1Pod status = api.readNamespacedPodStatus(name, namespace, null);
//
//                    String IP = status.getStatus().getPodIP();
//                    String phase = status.getStatus().getPhase();
//
//                    System.out.println("Waiting for pods/" + name + " in " + phase + " phase with IP=" + IP);
//
//                    // If ready, fire callback
//                    if (IP != null && phase.equals("Running")) {
//                        onReady.accept(status);
//
//                        // Cancel self (stop polling pod status)
//                        podCheckTimer.cancel();
//                    }
//                    else if (phase.equals("Failed") || phase.equals("Unknown")) {
//                        onFail.accept(status);
//
//                        podCheckTimer.cancel();
//                    }
//                } catch (ApiException e) {
//                    e.printStackTrace();
//                    throw new RuntimeException("Tried getting pod status of pods/" + name);
//                }
//            }
//        };
//
//        // Check every 10 seconds
//        podCheckTimer.schedule(podReadyChecker, 10000l, 10000l);
//    }
//}
