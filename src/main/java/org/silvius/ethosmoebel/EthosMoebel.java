package org.silvius.ethosmoebel;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.silvius.ethosmoebel.listener.WorldListener;
import org.silvius.ethosmoebel.moebel.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class EthosMoebel extends JavaPlugin implements CommandExecutor, TabCompleter {
    public static final HashMap<String, Moebel> moebelList = new HashMap<>();
    private static EthosMoebel plugin;
    private final File locationFile = new File(getDataFolder(), "locations.yml");
    private final FileConfiguration locationsConfig = YamlConfiguration.loadConfiguration(locationFile);
    private ProtocolManager protocolManager;


    public static EthosMoebel getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        registerMoebel();
        getCommand("ethosmoebel").setExecutor(new MoebelCommand());
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new WorldListener(protocolManager), this);
    }

    private void registerMoebel() {
        registerSittable("magenta_stool", 1, Material.MAGENTA_BED, Material.OAK_PLANKS.createBlockData(), "Magenta Hocker");
        registerSittable("magenta_chair", 2, Material.MAGENTA_BED, Material.OAK_PLANKS.createBlockData(), "Eichenstuhl");
        registerSittable("spruce_chair", 3, Material.MAGENTA_BED, Material.SPRUCE_PLANKS.createBlockData(), "Fichtenstuhl");
        registerSittable("magenta_4", 4, Material.MAGENTA_BED,Material.BIRCH_PLANKS.createBlockData(), "Birkenstuhl");
        registerSittable("magenta_5", 5, Material.MAGENTA_BED,Material.DARK_OAK_PLANKS.createBlockData(), "Schwarzeichenstuhl");
        registerSittable("magenta_6", 6, Material.MAGENTA_BED,Material.JUNGLE_PLANKS.createBlockData(), "Tropenstuhl");
        registerSittable("magenta_7", 7, Material.MAGENTA_BED,Material.ACACIA_PLANKS.createBlockData(), "Akazienstuhl");
        registerSittable("magenta_8", 8, Material.MAGENTA_BED,Material.MANGROVE_PLANKS.createBlockData(), "Mangrovenstuhl");
        moebelList.put("table", new Tables("Tisch", "table", Material.OAK_PLANKS.createBlockData()));
        moebelList.put("closet", new Closet("§4Schrank", "closet", Material.OAK_PLANKS.createBlockData()));
        moebelList.put("silver_ore", new OreBlock(2, "Silberroherzblock", Material.IRON_INGOT, "silver_ore", Material.COBBLESTONE.createBlockData()));
    }

    private void registerSittable(String name, int customModelData, Material material, BlockData particleData, String displayName){
        moebelList.put(name, new Sittable(customModelData, displayName, material, name, particleData, this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getLocationsConfig(){
        return locationsConfig;
    }
    public File getLocationFile(){
        return locationFile;
    }
    public void saveLocationFile(){
        try{                locationsConfig.save(getLocationFile());}
        catch (IOException e){e.printStackTrace();}
    }


}
