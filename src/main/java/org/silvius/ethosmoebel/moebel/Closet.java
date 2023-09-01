package org.silvius.ethosmoebel.moebel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.silvius.ethosmoebel.EthosMoebel;

public class Closet extends Moebel{
    public Closet(String displayName, String name, BlockData particleData){
        super(displayName, name, particleData, null);
    }

    @Override
    protected ItemStack generateItem(){
        ItemStack itemStack = new ItemStack(Material.BARREL);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(this.customModelData);
        final PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        data.set(namespacedKey, PersistentDataType.STRING, this.name);
        itemMeta.displayName(Component.text(this.displayName).decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    @Override
    public void spawn(BlockFace blockFace, Location loc){
        Block block = loc.getBlock();
        block.setType(Material.BARREL);
        org.bukkit.block.Barrel barrel = (org.bukkit.block.Barrel) loc.getBlock().getState();
        final Barrel blockData = (Barrel) block.getBlockData();
        final PersistentDataContainer data = barrel.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        data.set(namespacedKey, PersistentDataType.STRING, this.name);
        barrel.setBlockData(blockData);
        blockData.setFacing(blockFace.getOppositeFace());
        barrel.customName(Component.text(ChatColor.DARK_RED+"Schrank"));
        barrel.update();
        block.setBlockData(blockData);


    }
}
