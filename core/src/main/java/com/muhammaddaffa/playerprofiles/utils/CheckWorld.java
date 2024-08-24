package com.muhammaddaffa.playerprofiles.utils;

import org.bukkit.entity.Player;

public class CheckWorld {

    public void checkWorld(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase("Spawn")) return;
    }


}
