package org.silvius.ethosmoebel.moebel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.silvius.ethosmoebel.EthosMoebel;

public abstract class Moebel {
    final int customModelData;
    final Material inventoryMaterial;
    final Material blockMaterial;

    public final ItemStack itemStack;
    final String name;
    private final BlockData particleData;
    final String displayName;
    final EthosMoebel main;


    public Moebel(int customModelData, String displayName, Material inventoryMaterial, Material blockMaterial, String name, BlockData particleData, EthosMoebel main){
        this.customModelData = customModelData;
        this.inventoryMaterial = inventoryMaterial;
        this.blockMaterial = blockMaterial;
        this.name = name;
        this.particleData = particleData;
        this.displayName = displayName;
        this.itemStack = generateItem();
        this.main = main;
    }
    public Moebel(String displayName, String name, BlockData particleData, EthosMoebel main){
        this(0, displayName, null, null, name, particleData, main);
    }



    protected abstract ItemStack generateItem();
    public abstract void spawn(BlockFace blockFace, Location loc);


    public void giveItem(Player player) {
        player.getInventory().addItem(itemStack);
    }
    public BlockData getParticleData(){
        return this.particleData;
    }
    public String getName(){
        return this.name;
    }


}
