package cljminecraft;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import java.util.concurrent.ConcurrentHashMap;
//import org.bukkit.event.Event;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.world.*;
import clojure.lang.Var;
import clojure.lang.RT;

public class PluginListener  implements Listener {
    String ns;
    Var eventVar;
    
    public PluginListener () {
        this("cljminecraft.events");
    } 
    
    public PluginListener (String cljns) {
        this.ns = cljns;
        eventVar = RT.var(ns, "event");
    }
    
    private void invokeEvent(String eventName, Object event) {        
        if (eventVar != null && eventVar.isBound()) {
            eventVar.invoke(eventName, event);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        invokeEvent("block-break", event);
    }
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        invokeEvent("player-bed-enter", event); 
    }
    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        invokeEvent("player-bed-leave", event); 
    }
    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        invokeEvent("player-bucket-empty", event); 
    }
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        invokeEvent("async-player-chat", event); 
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        invokeEvent("player-drop-item", event); 
    }
    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        invokeEvent("player-egg-throw", event); 
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        invokeEvent("player-interact-entity", event); 
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        invokeEvent("player-interact", event); 
    }
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        invokeEvent("player-item-held", event); 
    }
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        invokeEvent("player-level-change", event); 
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        invokeEvent("player-login", event); 
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        invokeEvent("player-respawn", event); 
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        invokeEvent("player-move", event); 
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        invokeEvent("player-toggle-sneak", event); 
    }
    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        invokeEvent("projectile-launch", event); 
    }
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        invokeEvent("creature-spawn", event);
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        invokeEvent("food-level-change", event);
    }
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        invokeEvent("entity-combust", event);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        invokeEvent("entity-damage", event); 
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        invokeEvent("entity-death", event); 
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        invokeEvent("entity-explode", event); 
    }
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        invokeEvent("item-spawn", event); 
    }
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        invokeEvent("entity-shoot-bow", event); 
    }
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        invokeEvent("entity-target", event);
    }
    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        invokeEvent("entity-interact", event);
    }
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        invokeEvent("explosion-prime", event);
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        invokeEvent("projectile-hit", event);
    }
    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        invokeEvent("block-can-build", event);
    }
    
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        invokeEvent("block-dispense", event);
    }
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        invokeEvent("block-grow", event);
    }
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        invokeEvent("block-damage", event);
    }
    /*
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        invokeEvent("block-physics", event);
    }
    */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        invokeEvent("block-place", event);
    }
    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        invokeEvent("block-piston", event);
    }
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        invokeEvent("block-redstone", event);
    }
    @EventHandler
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        invokeEvent("vehicle-block-collision", event);
    }
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        invokeEvent("vehicle-create", event);
    }
    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        invokeEvent("vehicle-damage", event);
    }
    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        invokeEvent("vehicle-destroy", event);
    }
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        invokeEvent("vehicle-enter", event);
    }
    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        invokeEvent("vehicle-entity-collision", event);
    }
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        invokeEvent("vehicle-exit", event);
    }
    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        invokeEvent("vehicle-move", event);
    }
    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        invokeEvent("vehicle-update", event);
    }
    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        invokeEvent("chunk-populate", event);
    }
}
