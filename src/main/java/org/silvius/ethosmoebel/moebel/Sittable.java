package org.silvius.ethosmoebel.moebel;

import de.tr7zw.nbtapi.NBTEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.silvius.ethosmoebel.EthosMoebel;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Sittable extends Moebel{


    public Sittable(int customModelData, String displayName, Material inventoryMaterial, String name, BlockData particleData, EthosMoebel main){
        super(customModelData, displayName, inventoryMaterial, inventoryMaterial, name, particleData, main);
    }
@Override
    protected ItemStack generateItem(){
        ItemStack itemStack = new ItemStack(this.inventoryMaterial);
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

        ItemDisplay display = (ItemDisplay) loc.getWorld().spawnEntity(loc.add(0.5, 1.5, 0.5), EntityType.ITEM_DISPLAY);
        loc.getBlock().setType(Material.BARRIER);
        Interaction interaction = (Interaction) loc.getWorld().spawnEntity(loc.subtract(0, 0.5, 0), EntityType.INTERACTION);
        NBTEntity nbtInteraction = new NBTEntity(interaction);
        nbtInteraction.getPersistentDataContainer().setUUID("display", display.getUniqueId());

        interaction.setResponsive(false);
        interaction.setInteractionHeight(1.01f);
        interaction.setInteractionWidth(1.01f);
        //Entity chicken = loc.getWorld().spawnEntity(loc.add(0, 2, 0), EntityType.CHICKEN);
        //chicken.customName(Component.text("§cGans"));
        //loc.getWorld().sendMessage(Component.text(chicken.getName().substring(0, 2)));
        final PersistentDataContainer data = interaction.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        data.set(namespacedKey, PersistentDataType.STRING, this.name);

        final NamespacedKey namespacedKeyOrientation = new NamespacedKey(EthosMoebel.getPlugin(), "orientation");
        data.set(namespacedKeyOrientation, PersistentDataType.STRING, blockFace.name());


        Transformation transformation = display.getTransformation();
        transformation.getScale().mul(1);
        display.setTransformation(transformation);
        display.setItemStack(itemStack);
        switch (blockFace) {
            case NORTH -> display.setRotation(0, 0);
            case EAST -> display.setRotation(90, 0);
            case SOUTH -> display.setRotation(180, 0);
            case WEST -> display.setRotation(-90, 0);
        }

    }

}
