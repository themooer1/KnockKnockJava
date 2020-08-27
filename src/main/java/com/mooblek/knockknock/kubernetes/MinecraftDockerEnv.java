package com.mooblek.knockknock.kubernetes;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
//import io.kubernetes.client.openapi.models.V1EnvVar;

import java.util.*;
import java.util.stream.Collectors;

public class MinecraftDockerEnv {
    public static Set<String> variables = new HashSet<String>(Arrays.asList(
        "EULA",
        "TZ",
//        "TYPE",  // Conflicts with Bungee TYPE: Bungeecord, Waterfall, etc.
        "VERSION",
        "FORGE_VERSION",
        "FORGE_INSTALLER",
        "FORGE_INSTALLER_URL",
        "BUKKIT_DOWNLOAD_URL",
        "PAPER_DOWNLOAD_URL",
        "SPIGOT_DOWNLOAD_URL",
        "FTB_LEGACYJAVAFIXER",
        "FTB_MODPACK_ID",
        "FTB_MODPACK_VERSION_ID",
        "CF_SERVER_MOD",
        "USE_MODPACK_START_SCRIPT",
        "SPONGEBRANCH",
        "FABRICVERSION",
        "FABRIC_INSTALLER",
        "FABRIC_INSTALLER_URL",
        "PLUGINS_SYNC_UPDATE",
        "CUSTOM_SERVER",
        "FORCE_REDOWNLOAD",
        "OVERRIDE_SERVER_PROPERTIES",
        "SERVER_NAME",
        "DIFFICULTY",
        "WHITELIST",
        "OPS",
        "ICON",
        "ENABLE_QUERY",
        "MAX_PLAYERS",
        "MAX_WORLD_SIZE",
        "ALLOW_NETHER",
        "ANNOUNCE_PLAYER_ACHIEVEMENTS",
        "ENABLE_COMMAND_BLOCK",
        "FORCE_GAMEMODE",
        "GENERATE_STRUCTURESHARDCORE",
        "SNOOPER_ENABLED",
        "MAX_BUILD_HEIGHT",
        "MAX_TICK_TIME",
        "SPAWN_ANIMALS",
        "SPAWN_MONSTERS",
        "SPAWN_NPCS",
        "SPAWN_PROTECTION",
        "VIEW_DISTANCE",
        "SEED",
        "MODE",
        "MOTD",
        "PVP",
        "LEVEL_TYPE",
        "GENERATOR_SETTINGS",
        "RESOURCE_PACK",
        "LEVEL",
        "WORLD",
        "FORCE_WORLD_COPY",
        "MODS",
        "REMOVE_OLD_MODS",
        "ALLOW_FLIGHT",
        "PLAYER_IDLE_TIMEOUT",
        "BROADCAST_CONSOLE_TO_OPS",
        "BROADCAST_RCON_TO_OPS",
        "ENABLE_JMX",
        "SYNC_CHUNK_WRITES",
        "ENABLE_STATUS",
        "ENTITY_BROADCAST_RANGE_PERCENTAGE",
        "FUNCTION_PERMISSION_LEVEL",
        "NETWORK_COMPRESSION_THRESHOLD",
        "OP_PERMISSION_LEVEL",
        "PREVENT_PROXY_CONNECTIONS",
        "USE_NATIVE_TRANSPORT",
        "ENFORCE_WHITELIST",
        "MEMORY",
        "INIT_MEMORY",
        "MAX_MEMORY",
        "USE_AIKAR_FLAGS",
        "USE_LARGE_PAGES"
    ));

    public static List<EnvVar> localEnvThatMatch() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> variables.contains(e.getKey()))
                .map(e -> new EnvVarBuilder().withName(e.getKey()).withValue(e.getValue()).build())
                .collect(Collectors.toList());
    }

}
