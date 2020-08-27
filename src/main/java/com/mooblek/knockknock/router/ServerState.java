package com.mooblek.knockknock.router;

import net.md_5.bungee.api.config.ServerInfo;

import javax.swing.text.html.Option;
import java.util.Optional;


public class ServerState {
    ServerInfo s;
    String err;

    public ServerState(ServerInfo server) {
        s = server;
    }

    public ServerState(String errorMessage) {
        err = errorMessage;
    }

    public Optional<ServerInfo> getServerInfo() {
        if (s == null)
            return Optional.empty();
        else
            return Optional.of(s);
    }

    public Optional<String> getErrorMessage() {
        if (err == null)
            return Optional.empty();
        else
            return Optional.of(err);
    }
}
