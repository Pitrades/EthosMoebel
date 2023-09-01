package org.silvius.ethosmoebel;

import it.unimi.dsi.fastutil.Hash;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Utilities {


    private static int getToolMultiplier(String name){
        if(name.contains("WOODEN")){
            return 2;
        }
        if(name.contains("STONE")){
            return 4;
        }        if(name.contains("IRON")){
            return 6;
        }        if(name.contains("DIAMOND")){
            return 8;
        }        if(name.contains("NETHERITE")){
            return 9;
        }
        if(name.contains("GOLD")){
            return 12;
        }
        return 1;
    }
    public static float getBreakTime(Player player, float hardness, BlockData blockData, ItemStack itemStack){
        float speedMultiplier = 1;
        ItemStack bestTool = new ItemStack(Material.BARRIER);


        switch(itemStack.getType().name().split("_")[1]){
            case "PICKAXE":
                bestTool.setType(Material.NETHERITE_PICKAXE);
                break;
            case "AXE":
                bestTool.setType(Material.NETHERITE_AXE);
                break;
            case "SHOVEL":
                bestTool.setType(Material.NETHERITE_SHOVEL);
                break;

            case "HOE":
                bestTool.setType(Material.NETHERITE_HOE);
                break;

        }
        Bukkit.broadcast(Component.text(bestTool.getType().name()));
        if(blockData.isPreferredTool(bestTool)){
            speedMultiplier = getToolMultiplier(itemStack.getType().name());
        }
        if(itemStack.getEnchantments().containsKey(Enchantment.DIG_SPEED)){
            speedMultiplier+= Math.pow(itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED),2)+1;
        }
        if(player.hasPotionEffect(PotionEffectType.FAST_DIGGING)){
            speedMultiplier *= 0.2*player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier()+1;
        }
        if(player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)){
            speedMultiplier *= Math.pow(0.3,Math.min(player.getPotionEffect(PotionEffectType.SLOW_DIGGING).getAmplifier(), 4));
        }
        if(player.isUnderWater() && !player.getInventory().getHelmet().getEnchantments().containsKey(Enchantment.WATER_WORKER)){
            speedMultiplier/=5;
        }
        if(!player.isOnGround()){
            speedMultiplier/=5;
        }
        float damage = speedMultiplier / hardness;
        if(blockData.isPreferredTool(itemStack)){
            damage /= 30;
        }
        else{
            damage /= 100;
        }
        return (int) Math.ceil(1/damage);
    }
}
