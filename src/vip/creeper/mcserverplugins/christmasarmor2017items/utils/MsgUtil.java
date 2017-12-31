package vip.creeper.mcserverplugins.christmasarmor2017items.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by July on 2017/12/30.
 */
public class MsgUtil {
    public static void sendMsg(Player player, String msg) {
        player.sendMessage("§a[2017圣诞套装]§d " + ChatColor.translateAlternateColorCodes('&', msg));
    }
}
