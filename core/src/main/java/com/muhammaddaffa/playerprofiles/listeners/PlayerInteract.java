package com.muhammaddaffa.playerprofiles.listeners;

import com.muhammaddaffa.playerprofiles.ConfigValue;
import com.muhammaddaffa.playerprofiles.PlayerProfiles;

import com.muhammaddaffa.playerprofiles.inventory.InventoryManager;
import com.muhammaddaffa.playerprofiles.manager.DependencyManager;
import com.muhammaddaffa.playerprofiles.manager.profile.ProfileManager;
import com.muhammaddaffa.playerprofiles.utils.CheckWorld;
import com.muhammaddaffa.playerprofiles.utils.Utils;
import me.aglerr.mclibs.libs.Common;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class PlayerInteract implements Listener {

    private final PlayerProfiles plugin;
    public PlayerInteract(PlayerProfiles plugin){
        this.plugin = plugin;
    }

    // This is for the cooldown feature
    private final Map<Player, Long> mapCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerRightClickEntity(PlayerInteractAtEntityEvent event){





        // First of all we want to return the code if the right clicked entity is not player
        if(!(event.getRightClicked() instanceof Player)) return;

        // Get the player object from this event
        Player player = event.getPlayer();

        // Check if the player is in the world called world
        new CheckWorld().checkWorld(player);
        // We check if the server is 1.9+, means they have off hand
        // This event will be fired twice for both main hand and off hand
        // So we want to stop the code if the interact hand is an off hand
        if(Common.hasOffhand() && event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        // Check the shift click option, basically this option is to define whether
        // should we only allow opening profile with shift click or not
        if(ConfigValue.MUST_SHIFT_CLICK && !player.isSneaking()){
            return;
        }
        // Check for the shift click option
        // If the shift click is disabled and player is sneaking, return the code
        if(!ConfigValue.MUST_SHIFT_CLICK && player.isSneaking()){
            return;
        }
        // Now, we get the right clicked entity as Player
        Player target = (Player) event.getRightClicked();
        // Now we check for the NPC option, should we open the profile of the NPC?
        if(ConfigValue.DISABLE_NPC_PROFILE && target.hasMetadata("NPC")){
            return;
        }
        // Profile locked feature, basically every player can lock their profile
        // so no one can open their profile. First, we need to get the ProfileManager
        ProfileManager profileManager = plugin.getProfileManager();
        if(profileManager.isProfileLocked(target)){
            // Send a message to the player
            player.sendMessage(Common.color(ConfigValue.LOCKED_PROFILE
                    .replace("{prefix}", ConfigValue.PREFIX)));
            // Stop the code
            return;
        }
        // First, check for the cooldown feature, and now we check if the player is in cooldown
        if(mapCooldown.containsKey(player)){
            // Get the time left
            long timeLeft = this.getCooldownTimeLeft(player);
            // We remove the player from the map if the time left is equals to below 0
            if(timeLeft <= 0){
                mapCooldown.remove(player);
            }
            // And if the time left is greater than 0, we stop the code here
            if(timeLeft > 0){
                // Send player message
                player.sendMessage(Common.color(ConfigValue.COOLDOWN_MESSAGE
                        .replace("{prefix}", ConfigValue.PREFIX)
                        .replace("{time}", timeLeft + "")));
                // Stop the code
                return;
            }
        }
        // Now this feature is a interact cooldown message, that means player cannot
        // spam open others profile, this option is recommended
        if(ConfigValue.COOLDOWN_ENABLED){
            mapCooldown.put(player, System.currentTimeMillis());
        }
        // After all those fucking checks, now we finally open the profiles
        // for the player, first get the InventoryManager class
        InventoryManager inventoryManager = plugin.getInventoryManager();
        // Now we open the inventory for the player
        inventoryManager.openInventory(null, player, target);
        // Play sound to the player
        Utils.playSound(player, "onProfileOpen");
    }

    private Long getCooldownTimeLeft(Player player){
        return ((mapCooldown.get(player) / 1000) + ConfigValue.COOLDOWN_TIME) - (System.currentTimeMillis() / 1000);
    }

}
