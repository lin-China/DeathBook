package cc.shengdao.Listener;

import cc.shengdao.DeathBookMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author 林叔
 */
public class BookEvent implements Listener {
    DeathBookMain plugin;

    public BookEvent(DeathBookMain plugin) {
        this.plugin = plugin;
    }

    /**
     * 存储附近玩家的集合
     */
    public List<Player> rangePlayerList = new ArrayList<>();

    /**
     * 存储持有死亡笔记玩家的集合
     */
    public List<Player> deathBookPlayer = new ArrayList<>();

    /**
     * 存储冷却时间
     */
    private int cooling;

    /**
     * 存储处于冷却时间当中的玩家
     */
    public List<Player> coolingPlayer = new ArrayList<>();

    Random random = new Random();

    @EventHandler
    public void bookEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //手上物品不为空时
        if (event.getItem() != null) {
            //物品是书，并且右键了空气、lore中包含"死亡笔记"时
            if (event.getItem().getType() == Material.BOOK &&
                event.getAction() == Action.RIGHT_CLICK_AIR &&
                event.getItem().getItemMeta().hasLore() &&
                Objects.equals(event.getItem().getItemMeta().getDisplayName(), "死亡笔记") &&
                event.getItem().getItemMeta().getLore().contains("§c死亡笔记")) {

                //给cooling赋值
                try {
                    cooling = plugin.getConfig().getInt("cooling");
                } catch (NullPointerException e) {
                    player.sendMessage("冷却时间不能为空！请检查配置文件！");
                }

                //遍历周围的实体
                double xyz = plugin.getConfig().getDouble("Range");
                for (Entity entity : player.getNearbyEntities(xyz, xyz, xyz)) {
                    if (entity instanceof Player) {
                        if (!rangePlayerList.contains(((Player) entity).getPlayer())) {
                            rangePlayerList.add(((Player) entity).getPlayer());
                        }
                    }
                }

                //当附近玩家集合包含要素时
                if (!rangePlayerList.isEmpty()) {
                    Player randomPlayer = rangePlayerList.get(random.nextInt(rangePlayerList.size()));
                    //防止NullPointerException
                    if (cooling != 0) {

                        //当玩家不在冷却时
                        if (!coolingPlayer.contains(player)) {

                            randomPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessageYaml().getString("message.tips")));

                            //判断是否清除死亡笔记
                            if (plugin.getConfig().getBoolean("disposable")) {
                                if (plugin.getConfig().getBoolean("useDisposebleItem")) {
                                    player.sendMessage(plugin.getMessageYaml().getString("message.useItem"));
                                }
                                player.getInventory().setItemInMainHand(null);
                            }

                            //添加死亡笔记玩家进入存储集合
                            if (!deathBookPlayer.contains(player)) {
                                deathBookPlayer.add(player);
                            }

                            //封禁自己多少秒
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                deathBookPlayer.remove(player);
                            }, plugin.getConfig().getInt("banMove")*20);

                            //处理N秒
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                //冷却后发送的消息
                                player.sendMessage(plugin.getMessageYaml().getString("message.afterCooling"));
                                //当玩家死亡后的消息
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',randomPlayer.getName() + plugin.getMessageYaml().get("message.deathMessage")));
                                randomPlayer.setHealth(0);
                            }, plugin.getConfig().getInt("time")*20);

                            coolingPlayer.add(player);
                        }

                        //冷却功能
                        if (coolingPlayer.contains(player)) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                coolingPlayer.remove(player);
                            }, plugin.getConfig().getInt("cooling") * 20);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getMessageYaml().getString("message.noPlayer")));
                }

            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        //判断玩家是否在集合内
        if (deathBookPlayer.contains(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getMessageYaml().getString("message.move")));
            event.setCancelled(true);
        }

    }


}
