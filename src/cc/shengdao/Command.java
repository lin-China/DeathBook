package cc.shengdao;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林叔
 */
public class Command implements CommandExecutor {
    DeathBookMain plugin;
    List<String> lore = new ArrayList<>();
    public Command(DeathBookMain plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/db get - 获取死亡笔记");
            sender.sendMessage("/db reload - 重载配置文件+消息文件");
            return true;
        }
        //重载
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadM();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("§4重载成功");
            } else {
                plugin.getLogger().info("§4重载成功");
            }
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("get")) {
                //book
                ItemStack book = new ItemStack(Material.BOOK);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName("死亡笔记");
                //没有物品时设置为书

                if (!lore.contains("§c死亡笔记")) {
                    //修复重复领取时lore会增加的bug
                    lore.add("§c死亡笔记");
                }
                meta.setLore(lore);
                book.setItemMeta(meta);
                player.getInventory().addItem(book);

                return true;
            }
        } else {
            Bukkit.getLogger().info("抱歉，该条指令只能由玩家执行");
        }

        return false;
    }
}
