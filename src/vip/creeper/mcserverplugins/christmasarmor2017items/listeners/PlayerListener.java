package vip.creeper.mcserverplugins.christmasarmor2017items.listeners;

import com.codingforcookies.lorearmorequip.LoreArmorEquipEvent;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.BleedEffect;
import de.slikey.effectlib.effect.SmokeEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import vip.creeper.mcserverplugins.christmasarmor2017items.ChristmasArmor2017Items;
import vip.creeper.mcserverplugins.christmasarmor2017items.utils.ItemUtil;
import vip.creeper.mcserverplugins.christmasarmor2017items.utils.MathUtil;
import vip.creeper.mcserverplugins.christmasarmor2017items.utils.MsgUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by July on 2017/12/30.
 */
public class PlayerListener implements Listener {
    private ChristmasArmor2017Items plugin;
    private EffectManager effectManager;
    private HashMap<String, BukkitTask> leatherColorUpdateTasks;
    private HashMap<String, BukkitTask> particleDisplayTasks;
    private List<String> worePlayerNames;

    public PlayerListener(ChristmasArmor2017Items plugin) {
        this.plugin = plugin;
        this.effectManager = new EffectManager(plugin);
        this.leatherColorUpdateTasks = new HashMap<>();
        this.particleDisplayTasks = new HashMap<>();
        this.worePlayerNames = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInventory playerInventory = player.getInventory();

        if (ChristmasArmor2017Items.isChristmasArmorItem(playerInventory.getChestplate())) {
            setPlayer(player);
            MsgUtil.sendMsg(player, "加成效果已生效,尽情享受吧!");
        }
    }

    @EventHandler
    public void onArmorEquipEvent(LoreArmorEquipEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        ItemStack newItem = event.getNewArmorPiece();
        ItemStack oldItem = event.getOldArmorPiece();


        if (event.getMethod() == LoreArmorEquipEvent.EquipMethod.DEATH) {
            return;
        }

        //可能衣服被拿下了
        if (ChristmasArmor2017Items.isChristmasArmorItem(oldItem) && ItemUtil.isChestplate(oldItem) && worePlayerNames.contains(playerName)) {
            resetPlayer(player);
            MsgUtil.sendMsg(player, "&c加成效果已失效!");
        }

        //可能衣服被穿上了
        if (ChristmasArmor2017Items.isChristmasArmorItem(newItem) && ItemUtil.isChestplate(newItem) && !worePlayerNames.contains(playerName)) {
            setPlayer(player);
            MsgUtil.sendMsg(player, "加成效果已生效,尽情享受吧!");
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        //玩家离线，停止任务
        if (worePlayerNames.contains(playerName)) {
            resetPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (ChristmasArmor2017Items.isChristmasArmorItem(player.getInventory().getChestplate())) {
            resetPlayer(player);
            setPlayer(player);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();

            if (worePlayerNames.contains(player.getName())) {
                int woreArmorAmount = 0;

                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (ChristmasArmor2017Items.isChristmasArmorItem(item)) {
                        woreArmorAmount++;
                    }
                }

                //必须穿上三件才能减免伤害
                if (woreArmorAmount >= 3) {
                    event.setDamage(event.getDamage() * 0.15D); //接受 30% 的伤害
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        if (worePlayerNames.contains(playerName)) {
            Location loc = player.getLocation();
            BleedEffect bleedEffect = new BleedEffect(effectManager);

            loc.setY(loc.getY() + 1);
            bleedEffect.setLocation(loc);

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, () -> {
                List<Entity> entities = player.getNearbyEntities(5D, 5D, 5D);

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity && !(entity instanceof Animals) && !entity.equals(player)) {
                        LivingEntity livingEntity = (LivingEntity) entity;

                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));

                        if (livingEntity instanceof Player) {
                            Player targetPlayer = (Player) livingEntity;

                            MsgUtil.sendMsg(targetPlayer, "&c你受到了来自 &e" + playerName + " &c的 &e死亡诠释 &c攻击!");
                            MsgUtil.sendMsg(player, "&c你的被动技能 &e死亡诠释 &c对 &e" + targetPlayer.getName() + " &c造成了伤害!");
                        }
                    }
                }

                bleedEffect.start();
            }), 40L);
        }

        //不能对血量和buff操作
        cancelLeatherColorUpdateTask(player);
        cancelParticleDisplayTask(player);
    }

    private void setPlayer(Player player) {
        runLeatherColorUpdateTask(player);
        runParticleDisplayTask(player);
        giveEffects(player);
        worePlayerNames.add(player.getName());
    }

    private void resetPlayer(Player player) {
        cancelLeatherColorUpdateTask(player);
        cancelParticleDisplayTask(player);
        removeEffects(player);
        worePlayerNames.remove(player.getName());
    }

    private void removeEffects(Player player) {
        //必须挂在同步线程里执行
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.setHealth(20D);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        });
    }

    private void giveEffects(Player player) {
        //必须挂在同步线程里执行
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.setMaxHealth(30D);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 2));
        });
    }

    private void runLeatherColorUpdateTask(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, () -> {
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (ItemUtil.isValidItem(item) && item.getType().name().contains("LEATHER")) {
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();

                    leatherMeta.setColor(org.bukkit.Color.fromRGB(MathUtil.getRandomIntegerNum(0, 255), MathUtil.getRandomIntegerNum(0, 255), MathUtil.getRandomIntegerNum(0, 255)));
                    item.setItemMeta(leatherMeta);
                }
            }
        }),0L, 2L);

        leatherColorUpdateTasks.put(player.getName(), task);
    }

    private void cancelLeatherColorUpdateTask(Player player) {
        String playerName = player.getName();

        if (leatherColorUpdateTasks.containsKey(playerName)) {
            leatherColorUpdateTasks.get(playerName).cancel();
            leatherColorUpdateTasks.remove(playerName);
        }
    }

    private void runParticleDisplayTask(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, () -> {
            SmokeEffect effect = new SmokeEffect(effectManager);
            Location loc = player.getLocation();

            effect.particleCount = 6;
            loc.setY(loc.getY() + 2);

            effect.setLocation(loc);
            effect.start();
        }), 0L, 20L);

        particleDisplayTasks.put(player.getName(), task);
    }

    private void cancelParticleDisplayTask(Player player) {
        String playerName = player.getName();

        if (particleDisplayTasks.containsKey(playerName)) {
            particleDisplayTasks.get(playerName).cancel();
            particleDisplayTasks.remove(playerName);
        }
    }
}
