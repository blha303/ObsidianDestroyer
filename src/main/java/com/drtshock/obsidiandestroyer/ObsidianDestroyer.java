package com.drtshock.obsidiandestroyer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.drtshock.obsidiandestroyer.Metrics.Graph;

/**
 * The ObsidianDestroyer plugin.
 * 
 * Allows certain explosions to destroy Obsidian.
 * 
 * @author drtshock
 * 
 */
public final class ObsidianDestroyer extends JavaPlugin {

    private final ODCommands cmdExecutor = new ODCommands(this);
    private ODConfig config = new ODConfig(this);
    private final ODEntityListener entityListener = new ODEntityListener(this);
    private final ODJoinListener joinListener = new ODJoinListener(this);
    public static Logger LOG;

    public static boolean UPDATE = false;
    public static String NAME = "";

    
    @Override
    public void onDisable() {
        config.saveDurabilityToFile();
        saveConfig();
    }

    
    @Override
    public void onEnable() {
        LOG = getLogger();
        getCommand("obsidiandestroyer").setExecutor(cmdExecutor);
        getCommand("od").setExecutor(cmdExecutor);

        config.loadConfig();
        entityListener.setObsidianDurability(config.loadDurabilityFromFile());

        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getPluginManager().registerEvents(joinListener, this);
        
        startMetrics();

        if(config.getCheckUpdate()) {
            Updater updater = new Updater(this, "obsidiandestroyer", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            UPDATE = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE; 
            NAME = updater.getLatestVersionString();
        }
    }

    public void startMetrics() { 	
        try {	
            Metrics metrics = new Metrics(this);
            
            Graph graph = metrics.createGraph("Durability");
            
            graph.addPlotter(new Metrics.Plotter("Obsidian Durability Per Server") {

                @Override
                public String getColumnName() {
                    return String.valueOf(config.getoDurability());
                }
            	
                @Override
                public int getValue() {
                    return 1;
                }
            });

            metrics.start();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to submit the stats :-("); // Failed to submit the stats :-(
        }
    }

    /**
     * Returns the config of this plugin.
     * 
     * @return the config of this plugin
     */
    public ODConfig getODConfig() {
        return config;
    }

    /**
     * Returns the entity listener of this plugin.
     * 
     * @return the entity listener of this plugin
     */
    public ODEntityListener getListener() {
        return entityListener;
    }

}
