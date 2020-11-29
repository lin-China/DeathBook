package cc.shengdao;

import cc.shengdao.Listener.BookEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author 林叔
 */
public class DeathBookMain extends JavaPlugin {
    public DeathBookMain() {

    }
    File messageFile = new File(getDataFolder(), "message.yml");
    YamlConfiguration messageYaml = new YamlConfiguration();

    public YamlConfiguration getMessageYaml() {
        return messageYaml;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveDefaultMessage();
        loadM();

        for (int i = 0; i < 10; i++) {
            getLogger().info("插件加载成功");
        }

        Bukkit.getPluginManager().registerEvents(new BookEvent(this), this);
        Bukkit.getPluginCommand("db").setExecutor(new Command(this));
    }

    public void loadM() {
        try {
            messageYaml.load(messageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveM() {
        try {
            messageYaml.save(messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存message.yml
     */
    public void saveDefaultMessage() {
        if (!this.messageFile.exists()) {
            this.saveResource("message.yml", false);
        }
    }
}
