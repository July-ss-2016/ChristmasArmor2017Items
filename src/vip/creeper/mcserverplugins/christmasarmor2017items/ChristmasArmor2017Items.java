package vip.creeper.mcserverplugins.christmasarmor2017items;

import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import vip.creeper.mcserverplugins.christmasarmor2017items.commands.AdminCommand;
import vip.creeper.mcserverplugins.christmasarmor2017items.listeners.PlayerListener;
import vip.creeper.mcserverplugins.christmasarmor2017items.utils.ItemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by July on 2017/12/30.
 */
public class ChristmasArmor2017Items extends JavaPlugin {
    private static ChristmasArmor2017Items instance;

    public void onEnable() {
        instance = this;
        getCommand("cai2017").setExecutor(new AdminCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public static ItemStack[] getChristmasArmorItems2017() {
        ItemStack[] result = new ItemStack[4];
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET); //皮革头盔
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE); //皮革胸甲
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS); //皮革护腿
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS); //皮革鞋子
        ItemMeta helmetMeta = helmet.getItemMeta();
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        ItemMeta leggingsMeta = leggings.getItemMeta();
        ItemMeta bootsMeta = boots.getItemMeta();

        result[0] = helmet;
        result[1] = chestplate;
        result[2] = leggings;
        result[3] = boots;

        helmetMeta.addEnchant(Enchantment.THORNS, 2, true);
        chestplateMeta.addEnchant(Enchantment.THORNS, 2, true);
        leggingsMeta.addEnchant(Enchantment.THORNS, 1, true);
        bootsMeta.addEnchant(Enchantment.THORNS, 1, true);

        helmetMeta.setDisplayName("§a[2017][V] §d圣诞节头盔");
        chestplateMeta.setDisplayName("§a[2017][V] §d圣诞节胸甲");
        leggingsMeta.setDisplayName("§a[2017][V] §d圣诞节护腿");
        bootsMeta.setDisplayName("§a[2017][V] §d圣诞节靴子");

        helmetMeta.setLore(getChristmasArmorItems2017Lores());
        chestplateMeta.setLore(getChristmasArmorItems2017Lores());
        leggingsMeta.setLore(getChristmasArmorItems2017Lores());
        bootsMeta.setLore(getChristmasArmorItems2017Lores());

        helmet.setItemMeta(helmetMeta);
        chestplate.setItemMeta(chestplateMeta);
        leggings.setItemMeta(leggingsMeta);
        boots.setItemMeta(bootsMeta);

        helmetMeta.spigot().setUnbreakable(true);
        chestplateMeta.spigot().setUnbreakable(true);
        leggingsMeta.spigot().setUnbreakable(true);
        bootsMeta.spigot().setUnbreakable(true);

        return result;
    }

    public static String getItemCode() {
        return "VI_0";
    }

    public static boolean isChristmasArmorItem(ItemStack item) {
        if (ItemUtil.isValidItem(item)) {
            List<String> lores = item.getItemMeta().getLore();

            if (lores != null && lores.size() >= 1) {
                String firstLore = lores.get(0);

                return firstLore != null && getItemCode().equalsIgnoreCase(firstLore.replace("§7- §f物品代码 §b> §f", ""));
            }

            return false;
        }

        return false;
    }

    private static List<String> getChristmasArmorItems2017Lores() {
        List<String> lores = new ArrayList<>();

        lores.add("§7- §f物品代码 §b> §f" + getItemCode());
        lores.add("§7- §f加成 §b> §f生命值"); //+10
        lores.add("§7- §f加成 §b> §f水下呼吸");
        lores.add("§7- §f加成 §b> §f跳跃提升");
        lores.add("§7- §f加成 §b> §f巴啦啦变色"); //每0.5秒皮革随机抽取颜色变色
        lores.add("§7- §f技能 §b> §f伤害稀释"); //吸收 75% 的伤害，防御能力基本和七夕套相同
        lores.add("§7- §f技能 §b> §f死亡诠释"); //死后 2秒后 对半径 6格 内的玩家释放中度效果持续5秒

        return lores;
    }
}
