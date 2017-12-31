package vip.creeper.mcserverplugins.christmasarmor2017items.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by July on 2017/12/30.
 */
public class ItemUtil {
    //判断是否为有效的物品
    public static boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    //判断是否为可穿戴类物品
    public static boolean isArmor(ItemStack item) {
        if (!isValidItem(item)) {
            return false;
        }

        String itemName = item.getType().name().toLowerCase();

        return itemName.contains("leggings") || itemName.contains("boots") || itemName.contains("helmet") || itemName.contains("chestplate");

    }

    public static boolean isChestplate(ItemStack item) {
        return isValidItem(item) && item.getType().name().contains("CHESTPLATE");
    }
}
