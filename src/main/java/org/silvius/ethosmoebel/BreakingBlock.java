package org.silvius.ethosmoebel;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import static org.silvius.ethosmoebel.EthosMoebel.moebelList;

public class BreakingBlock {
    final BukkitTask task;
    final ItemDisplay display;

    final Block block;

    public BreakingBlock(BukkitTask task, ItemDisplay display, Block block){
        this.task = task;
        this.display = display;
        this.block = block;

    }

    public void cancelTask(){
        task.cancel();
        ItemStack itemStack = display.getItemStack();
        assert itemStack != null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(2);
        itemStack.setItemMeta(itemMeta);
        display.setItemStack(itemStack);

    }

    public void destroyBlock(boolean shouldDrop){
        task.cancel();
        final PersistentDataContainer data = display.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        if(!data.has(namespacedKey)){return;}
        String name = data.get(namespacedKey, PersistentDataType.STRING);
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);

        ItemStack itemStack = display.getItemStack();
        assert itemStack != null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(2);
        itemStack.setItemMeta(itemMeta);
        block.setType(Material.AIR);
        if(shouldDrop){
        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);}
        display.remove();


        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 200, 0.2, 0, 0.2, moebelList.get(name).getParticleData());
        loc.getWorld().playSound(loc, Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1, 1);
    }

}
