package org.silvius.ethosmoebel.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.jeff_media.customblockdata.CustomBlockData;
import de.tr7zw.nbtapi.NBTEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.silvius.ethosmoebel.BreakingBlock;
import org.silvius.ethosmoebel.EthosMoebel;
import org.silvius.ethosmoebel.Utilities;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

import static org.silvius.ethosmoebel.EthosMoebel.moebelList;
import static org.silvius.ethosmoebel.PermissionChecks.hasBreakPermission;
import static org.silvius.ethosmoebel.PermissionChecks.hasPlacePermission;

public class WorldListener implements Listener {
    private static final HashMap<Interaction, ArmorStand> sittingDir = new HashMap<>();
    private ProtocolManager protocolManager;
    private static final HashMap<UUID, Block> hitBlock = new HashMap<>();
    private static final ArrayList<Location> justPlacedBlock = new ArrayList<>();
    HashMap<Location, BreakingBlock> tasks = new HashMap<>();



    public WorldListener(ProtocolManager main) {

        protocolManager = main;
        protocolManager.addPacketListener(new PacketAdapter(EthosMoebel.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                int status = packet.getIntegers().read(0);
                if(status==0){
                    BlockPosition location = packet.getBlockPositionModifier().read(0);
                    if(tasks.containsKey(location.toLocation(player.getWorld()))){
                    tasks.get(location.toLocation(player.getWorld())).cancelTask();
                        tasks.remove(location.toLocation(player.getWorld()));
                    }
                }
            }});
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Block block = event.getClickedBlock();

        if(block==null){return;}
        final Block placedBlock = block.getRelative(event.getBlockFace());
        Location location = placedBlock.getLocation().subtract(block.getLocation());

        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK){return;}
        if(block.getState() instanceof Barrel barrel){
            final PersistentDataContainer data = barrel.getPersistentDataContainer();
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");

            if(data.has(namespacedKey)) {
                event.setCancelled(true);
                EthosMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EthosMoebel.getPlugin(), () ->
                {
                    event.getPlayer().openInventory(barrel.getInventory());
                    barrel.close();
                    barrel.update();
                }, 0L);
            }

        }
        if(block.getRelative(event.getBlockFace()).canPlace(Material.RAW_IRON_BLOCK.createBlockData())
                &&!justPlacedBlock.contains(location)){


            final Player player = event.getPlayer();
            if(!hasPlacePermission(player, placedBlock)){event.setCancelled(true);
                return;}
            final ItemStack itemStack = player.getInventory().getItemInMainHand();
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta==null){return;}

            final PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
            if(!data.has(namespacedKey)){return;}
            event.setCancelled(true);
            String name = data.get(namespacedKey, PersistentDataType.STRING);
            if(Objects.equals(moebelList.get(name).getName(), "closet")){

                EthosMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EthosMoebel.getPlugin(), () ->
                        moebelList.get(name).spawn(player.getFacing(), placedBlock.getLocation()), 0L);

                if(player.getGameMode()== GameMode.SURVIVAL){
                    itemStack.setAmount(itemStack.getAmount()-1);}
                return;
            }
            if(moebelList.get(name).getName().contains("ore") ){
                justPlacedBlock.add(location);
                EthosMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EthosMoebel.getPlugin(), () ->
                        justPlacedBlock.remove(location), 1L);
                EthosMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EthosMoebel.getPlugin(), () ->
                        moebelList.get(name).spawn(player.getFacing(), placedBlock.getLocation()), 0L);
            player.swingMainHand();
                if(player.getGameMode()== GameMode.SURVIVAL){
                    itemStack.setAmount(itemStack.getAmount()-1);}
                return;
            }



            if(location.getY()!=1){return;}




            EthosMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(EthosMoebel.getPlugin(), () -> moebelList.get(name).spawn(player.getFacing(), block.getLocation()), 0L);


            if(player.getGameMode()== GameMode.SURVIVAL){
                itemStack.setAmount(itemStack.getAmount()-1);}
        }


    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){

    }
    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event){
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();

        if(!(entity instanceof Interaction interaction)){return;}
        final PersistentDataContainer data = entity.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "orientation");
        if(data.has(namespacedKey)){
            sitOnChair(player, entity, interaction, data, namespacedKey);}


    }

    private static boolean checkForArmorStands(Entity entity) {
        Collection<Entity> entities = entity.getNearbyEntities(0, 0, 0.0);
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosstand");
        for(Entity entityI:entities){
            final PersistentDataContainer dataI = entityI.getPersistentDataContainer();
            if(entityI instanceof ArmorStand && dataI.has(namespacedKey)){
                return true;

            }
        }
        return false;
    }

    private static void sitOnChair(Player player, Entity entity, Interaction interaction, PersistentDataContainer data, NamespacedKey namespacedKey) {
        String name = data.get(namespacedKey, PersistentDataType.STRING);

        Location loc = entity.getLocation();
        ArmorStand arrow = (ArmorStand) loc.getWorld().spawnEntity(loc.subtract(0, 0.5, 0), EntityType.ARMOR_STAND);
        if(checkForArmorStands(arrow)){
            arrow.remove();
            return;
        }
        switch (name) {
            case "NORTH" -> arrow.setRotation(180, 0);
            case "EAST" -> arrow.setRotation(-90, 0);
            case "SOUTH" -> arrow.setRotation(0, 0);
            case "WEST" -> arrow.setRotation(90, 0);
        }
        arrow.setSmall(true);
        arrow.setGravity(false);
        arrow.setVisible(false);
        arrow.addPassenger(player);
        sittingDir.put(interaction, arrow);
        final PersistentDataContainer data2 = arrow.getPersistentDataContainer();
        final NamespacedKey namespacedKey2 = new NamespacedKey(EthosMoebel.getPlugin(), "ethosstand");
        data2.set(namespacedKey2, PersistentDataType.INTEGER, 1);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event){
        if(event.getEntity() instanceof ItemFrame itemFrame){
            PersistentDataContainer data = itemFrame.getPersistentDataContainer();
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosTableFrame");
            if(data.has(namespacedKey)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAttackEntity(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if(!(damager instanceof Player player)){return;}
        if(!hasBreakPermission(player, entity.getLocation().getBlock())){
            event.setCancelled(true)
            ;return;}
        if(!(entity instanceof Interaction)){return;}
        final PersistentDataContainer data = entity.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        if(!data.has(namespacedKey)){return;}
        String name = data.get(namespacedKey, PersistentDataType.STRING);
        assert name != null;
        NBTEntity nbtInteraction = new NBTEntity(entity);
        UUID displayUUID = nbtInteraction.getPersistentDataContainer().getUUID("display");
        if(displayUUID==null){return;}
        Entity displayEntity = entity.getWorld().getEntity(displayUUID);

        Location loc = entity.getLocation();
        entity.remove();

        displayEntity.remove();

        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 200, 0.2, 0, 0.2, moebelList.get(name).getParticleData());
        loc.getWorld().playSound(loc, Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1, 1);
        if(sittingDir.containsKey((Interaction) entity)){
        sittingDir.get((Interaction) entity).remove();
        sittingDir.remove((Interaction) entity);}
        if(player.getGameMode()==GameMode.SURVIVAL){
        loc.getWorld().dropItemNaturally(loc, moebelList.get(name).itemStack);}
        loc.getBlock().setType(Material.AIR);


    }


    @EventHandler
    public void onDestroyBlock(BlockDestroyEvent event){
        Block block = event.getBlock();
        if(isTable(block)){
            block.getWorld().dropItemNaturally(block.getLocation(), moebelList.get("table").itemStack);
            Block blockAboe = block.getRelative(BlockFace.UP);
            Collection<Entity> entities = blockAboe.getLocation().add(0.5, 0.05, 0.5).getNearbyEntities(0, 0, 0.0);
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosTableFrame");
            for(Entity entityI:entities){
                final PersistentDataContainer dataI = entityI.getPersistentDataContainer();
                if(entityI instanceof ItemFrame itemFrame&& dataI.has(namespacedKey)){
                    itemFrame.getWorld().dropItemNaturally(blockAboe.getLocation().add(0.5, 0, 0.5), itemFrame.getItem());
                    entityI.remove();

                }
            }
        }
    }

    @EventHandler
    public void onBreakTable(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(!hasBreakPermission(player, block)){
            event.setCancelled(true);
            return;}

        if(isTable(block)){
            block.getWorld().dropItemNaturally(block.getLocation(), moebelList.get("table").itemStack);
            Block blockAboe = block.getRelative(BlockFace.UP);
            Collection<Entity> entities = blockAboe.getLocation().add(0.5, 0.05, 0.5).getNearbyEntities(0, 0, 0.0);
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosTableFrame");
            PersistentDataContainer customBlockData = new CustomBlockData(block, EthosMoebel.getPlugin());
            if(!customBlockData.has(namespacedKey)){return;}
            UUID itemFrameUUID = UUID.fromString(customBlockData.get(namespacedKey, PersistentDataType.STRING));
            ItemFrame itemFrame = (ItemFrame) block.getWorld().getEntity(itemFrameUUID);
                    if(event.getPlayer().getGameMode()==GameMode.SURVIVAL){
                        itemFrame.getWorld().dropItemNaturally(blockAboe.getLocation().add(0.5, 0, 0.5), itemFrame.getItem());}
            itemFrame.remove();



        }
    }
    @EventHandler
    public void onBreakCloset(BlockBreakEvent event){
        final Block block = event.getBlock();
        Player player = event.getPlayer();
        if(!hasBreakPermission(player, block)){event.setCancelled(true);
            return;}
        if(block.getState() instanceof Barrel barrel){
            final PersistentDataContainer data = barrel.getPersistentDataContainer();
            final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
            if(data.has(namespacedKey)) {

                event.setDropItems(false);
                if(event.getPlayer().getGameMode()==GameMode.SURVIVAL){
                block.getWorld().dropItemNaturally(block.getLocation(), moebelList.get(data.get(namespacedKey, PersistentDataType.STRING)).itemStack);}
            }

        }
    }

    @EventHandler
    public void onBreakOreBlock(BlockBreakEvent event){
        Block block = event.getBlock();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        if(block.getType()!=Material.BARRIER){return;}
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        PersistentDataContainer blockData = new CustomBlockData(block, EthosMoebel.getPlugin());
        if(!blockData.has(namespacedKey)){return;}
        UUID itemFrameUUID = UUID.fromString(blockData.get(namespacedKey, PersistentDataType.STRING));
        Entity entity = (Entity) block.getWorld().getEntity(itemFrameUUID);
        final PersistentDataContainer dataI = entity.getPersistentDataContainer();
        String name = dataI.get(namespacedKey, PersistentDataType.STRING);
        entity.remove();
        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 200, 0.2, 0, 0.2, moebelList.get(name).getParticleData());
        loc.getWorld().playSound(loc, Sound.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1, 1);

    }
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){


    }
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event){
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        Collection<Entity> entities = loc.getNearbyEntities(0.01, 0.01, 0.01);
        final NamespacedKey namespacedKey = new NamespacedKey(EthosMoebel.getPlugin(), "ethosmoebel");
        for(Entity entityI:entities){
            final PersistentDataContainer dataI = entityI.getPersistentDataContainer();
            if(entityI.getType()==EntityType.ITEM_DISPLAY && dataI.has(namespacedKey)){
                ItemDisplay itemDisplay = (ItemDisplay) entityI;
                ItemStack itemStack = itemDisplay.getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setCustomModelData(itemMeta.getCustomModelData()+1);

                final Runnable particleRunnable = new Runnable() {
                    float i = itemMeta.getCustomModelData()/100f-0.02f; // initialize t to 0

                    public void run() {
                        i+= 1/Utilities.getBreakTime(player, Material.RAW_GOLD_BLOCK.getHardness(), Material.RAW_GOLD_BLOCK.createBlockData(), event.getItemInHand());

                        itemMeta.setCustomModelData(Math.round(10*i)+2);
                        itemStack.setItemMeta(itemMeta);
                        itemDisplay.setItemStack(itemStack);
                        loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 1, Material.COBBLESTONE.createBlockData());
                        if(i>1){
                            tasks.get(block.getLocation()).destroyBlock(Material.RAW_GOLD_BLOCK.createBlockData().isPreferredTool(event.getItemInHand()));
                            tasks.remove(block.getLocation());
                        }
                    }
                };

// run the particleRunnable every tick
                final BukkitTask task = Bukkit.getScheduler().runTaskTimer(EthosMoebel.getPlugin(), particleRunnable, 0, 1);
                tasks.put(block.getLocation(), new BreakingBlock(task, itemDisplay, block));
            }
        }
    }

    private boolean isTable(Block block) {
        if(block.getBlockData() instanceof PistonHead){
            if(((PistonHead) block.getBlockData()).getFacing()==BlockFace.UP){
                Block blockBelow = block.getRelative(BlockFace.DOWN);
                if(!(blockBelow.getBlockData() instanceof Piston)){
                    return true;
                }
                else{
                    return !(((Piston) blockBelow.getBlockData()).getFacing() == BlockFace.UP && ((Piston) blockBelow.getBlockData()).isExtended());
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event){
        Entity entity = event.getDismounted();
        Entity dismounter = event.getEntity();
        final PersistentDataContainer data2 = entity.getPersistentDataContainer();
        final NamespacedKey namespacedKey2 = new NamespacedKey(EthosMoebel.getPlugin(), "ethosstand");
        if(data2.has(namespacedKey2)){
            entity.remove();
            dismounter.teleport(findSafePlace(dismounter.getLocation()).subtract(0, 1.52192, 0));
        }
    }
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){

    }

    private static @NotNull Location findSafePlace(Location loc){
        if(isSafe(loc.add(1, 0, 0))){return loc;}
        if(isSafe(loc.add(0, 0, 1))){return loc;}
        if(isSafe(loc.add(-1, 0, 0))){return loc;}
        if(isSafe(loc.add(-1, 0, 0))){return loc;}
        if(isSafe(loc.add(0, 0, -1))){return loc;}
        if(isSafe(loc.add(0, 0, -1))){return loc;}
        if(isSafe(loc.add(1, 0, 0))){return loc;}
        if(isSafe(loc.add(1, 0, 0))){return loc;}

        if(isSafe(loc.add(0, 1, 1))){return loc;}
        if(isSafe(loc.add(0, 0, 1))){return loc;}
        if(isSafe(loc.add(-1, 0, 0))){return loc;}
        if(isSafe(loc.add(-1, 0, 0))){return loc;}
        if(isSafe(loc.add(0, 0, -1))){return loc;}
        if(isSafe(loc.add(0, 0, -1))){return loc;}
        if(isSafe(loc.add(1, 0, 0))){return loc;}
        if(isSafe(loc.add(1, 0, 0))){return loc;}

        return loc.add(-1, 1, 1);


    }

    private static boolean isSafe(Location loc){
        return loc.getBlock().isPassable() && loc.add(0, 1, 0).getBlock().isPassable();
    }

}
