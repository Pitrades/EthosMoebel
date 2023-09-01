package org.silvius.ethosmoebel.moebel;

import com.jeff_media.customblockdata.CustomBlockData;
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

import javax.xml.crypto.dsig.TransformService;

public class OreBlock extends Moebel{
    public OreBlock(int customModelData, String displayName, Material material, String name, BlockData particleData){
        super(customModelData, displayName, material, material, name, particleData, null);
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

        ItemDisplay display = (ItemDisplay) loc.getWorld().spawnEntity(loc.add(0.5, 0.5, 0.5), EntityType.ITEM_DISPLAY);
        loc.getBlock().setType(Material.BARRIER);

        PersistentDataContainer blockData = new CustomBlockData(loc.getBlock(), EthosMoebel.getPlugin());
        final PersistentDataContainer data = display.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");

        data.set(namespacedKey, PersistentDataType.STRING, this.name);
        blockData.set(namespacedKey, PersistentDataType.STRING, display.getUniqueId().toString());
        display.setItemStack(this.itemStack);


    }
}
