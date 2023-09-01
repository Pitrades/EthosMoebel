package org.silvius.ethosmoebel.moebel;

import com.jeff_media.customblockdata.CustomBlockData;
import de.tr7zw.nbtapi.NBTBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.silvius.ethosmoebel.EthosMoebel;

public class Tables extends Moebel{



    public Tables(String displayName, String name, BlockData particleData){
        super(displayName, name, particleData, null);
    }

    @Override
    protected ItemStack generateItem(){
        ItemStack itemStack = new ItemStack(Material.PISTON);
        ItemMeta itemMeta = itemStack.getItemMeta();
        //itemMeta.setCustomModelData(this.customModelData);
        final PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        data.set(namespacedKey, PersistentDataType.STRING, this.name);
        itemMeta.displayName(Component.text(this.displayName).decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    @Override
    public void spawn(BlockFace blockFace, Location loc){
        Block block = loc.add(0, 1, 0).getBlock();
        block.setType(Material.PISTON_HEAD);
        final BlockData blockData = block.getBlockData();
        ((Directional) blockData).setFacing(BlockFace.UP);
        block.setBlockData(blockData);
        Block blockAbove = block.getRelative(BlockFace.UP);
        ItemFrame itemFrame = (ItemFrame)block.getWorld().spawnEntity(blockAbove.getLocation(), EntityType.ITEM_FRAME);
        itemFrame.setVisible(false);
        //itemFrame.setFixed(true);
        PersistentDataContainer customBlockData = new CustomBlockData(block, EthosMoebel.getPlugin());

        final PersistentDataContainer data = itemFrame.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosTableFrame");
        data.set(namespacedKey, PersistentDataType.INTEGER, 1);
        customBlockData.set(namespacedKey, PersistentDataType.STRING, itemFrame.getUniqueId().toString());


    }

}
