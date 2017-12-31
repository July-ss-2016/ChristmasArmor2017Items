package vip.creeper.mcserverplugins.christmasarmor2017items.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.creeper.mcserverplugins.christmasarmor2017items.ChristmasArmor2017Items;

/**
 * Created by July on 2017/12/30.
 */
public class AdminCommand implements CommandExecutor {
    public boolean onCommand(CommandSender cs, Command cmd, String lable, String[] args) {
        if (!cs.hasPermission("ChristmasArmor2017Items.admin")) {
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null || !player.isOnline()) {
                cs.sendMessage("玩家不在线!");
                return true;
            }

            if (getPlayerInventoryFreeSize(player) < 4) {
                cs.sendMessage("玩家背包空间不足!");
                return true;
            }

            for (ItemStack item : ChristmasArmor2017Items.getChristmasArmorItems2017()) {
                player.getInventory().addItem(item);
            }

            cs.sendMessage("成功!");
            return true;
        }

        return false;
    }

    private int getPlayerInventoryFreeSize(Player player) {
        int freeSize = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                freeSize++;
            }
        }

        return freeSize;
    }
}
