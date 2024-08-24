package com.muhammaddaffa.playerprofiles.manager;

import me.aglerr.mclibs.libs.Common;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class DependencyManager {

    public static boolean PLACEHOLDER_API;
    public static void checkDependency() {
        PluginManager pm = Bukkit.getPluginManager();
        PLACEHOLDER_API = pm.getPlugin("PlaceholderAPI") != null;
    }
}