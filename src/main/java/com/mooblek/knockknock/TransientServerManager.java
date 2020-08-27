package com.mooblek.knockknock;

import com.mooblek.knockknock.router.ServerState;
import com.mooblek.knockknock.router.SleepyServer;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
//import net.md_5.bungee.api.CommandSender;
//import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
//import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
//import net.md_5.bungee.api.connection.Server;
//import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
//import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class TransientServerManager implements Listener {
    SleepyServer server;
    ProxyServer p;

    public TransientServerManager(ProxyServer p) {
        server = new SleepyServer(p); this.p = p;
    }
    @EventHandler
    public void beforeConnect(PostLoginEvent event) {
//        System.out.println("BeforeConnect: Target: " + event.getTarget().getName());

        // Redirect new connections to the transient kubernetes server
            ProxiedPlayer player = event.getPlayer();
            ServerState state = server.getServerInfo();

            // TODO: For TESTING always return serverinfo
            assert p == ProxyServer.getInstance();

//            ServerState state = new ServerState(ProxyServer.getInstance().constructServerInfo(
//                    "TestTumnus",
//                    Util.getAddr("172.18.0.3:25565"),
//                    "TEA??",
//                    false
//            ));

            System.out.println("Connecting player...");

            if (state.getServerInfo().isPresent()) {
                ServerInfo target = state.getServerInfo().get();
                System.out.printf("Connecting %s to %s.\n", player.getDisplayName(), target.getName());
                player.connect(target);
            } else {
                System.out.println("Failed to connect");
                System.out.println(state.getErrorMessage().get());
                String errorMessage = state.getErrorMessage().get();
                player.disconnect(clientErrorMessage(errorMessage));
            }

    }

    @EventHandler
    public void beforeConnect(ServerConnectEvent event) {
        System.out.println("BeforeConnect: Target: " + event.getTarget().getName() + " for reason "  + event.getReason());
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
            System.out.println("Cancelling connection to default (dummy) server.");
            event.setCancelled(true);
        }
        /*
        Event has reasons JOIN_PROXY and PLUGIN
        JOIN_PROXY is a new connection
        PLUGIN results from calling connect on a ProxiedPlayer
        LOBBY_FALLBACK
        COMMAND
        SERVER_DOWN_REDIRECT
        KICK_REDIRECT
        PLUGIN_MESSAGE
        UNKNOWN

        see ServerConnectEvent.java
         */

        // Redirect new connections to the transient kubernetes server
//        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
//            ProxiedPlayer player = event.getPlayer();
////            ServerState state = server.getServerInfo();
//
//            // TODO: For TESTING always return serverinfo
//            assert p == ProxyServer.getInstance();
////            ServerState state = new ServerState(p.constructServerInfo(
////                    "TestTumnus",
////                    Util.getAddr("172.18.0.3:25565"),
////                    "Tea?",
////                    false
////            ));
//            ServerState state = new ServerState(ProxyServer.getInstance().constructServerInfo(
//                    "TestTumnus",
//                    Util.getAddr("172.18.0.3:25565"),
//                    "TEA??",
//                    false
//            ));
//
//            System.out.println("Connecting player...");
//
//            if (state.getServerInfo().isPresent()) {
//                ServerInfo target = state.getServerInfo().get();
//                System.out.printf("Connecting %s to %s.\n", player.getDisplayName(), target.getName());
//                player.connect(target);
//            } else {
//                System.out.println("Failed to connect");
//                System.out.println(state.getErrorMessage().get());
//                String errorMessage = state.getErrorMessage().get();
//                player.disconnect(clientErrorMessage(errorMessage));
//            }
//        } else {
//            System.out.println("Reconnecting: " + event.getReason());
//            System.out.println(event.getTarget().getName());
//        }
    }

    /**
     * The message that will be returned to a player before
     * the target server has started.
     */
    private TextComponent clientErrorMessage(String msg) {

        TextComponent message = new TextComponent(msg);
        message.setColor(ChatColor.GREEN);
//        message.setUnderlined(true);
//        message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org" ));
//        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Learn more").color(ChatColor.DARK_PURPLE).create()));
        return message;
    }

}
