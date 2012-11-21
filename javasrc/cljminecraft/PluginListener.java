package cljminecraft;
import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Compiler;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginLoader;
import java.util.HashSet;
import java.net.URLClassLoader;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Server;
import java.lang.ClassLoader;
import java.net.URL;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.world.*;
import org.bukkit.event.painting.*;

public class PluginListener  implements Listener {
    private String ns;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-break-event");
        if (f.isBound()) f.invoke(event);
    }

    /* begin auto-generated code */
    /*@EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-bed-enter-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-bed-leave-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-bucket-empty-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "async-player-chat-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-drop-item-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-egg-throw-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-interact-entity-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-interact-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-item-held-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-level-change-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-login-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "player-respawn-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-move-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "player-toggle-sneak-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "projectile-launch-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "creature-spawn-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "food-level-change-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "entity-combust-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-damage-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-death-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-explode-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "item-spawn-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-shoot-bow-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-target-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "entity-interact-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "explosion-prime-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "projectile-hit-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-can-build-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-dispense-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-grow-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-damage-event");
        if (f.isBound()) f.invoke(event);
    }
    /*
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-place-event");
        if (f.isBound()) f.invoke(event);
    }
    */

    /*
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-place-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-piston-extend-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-redstone-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-block-collision-event");
        if (f.isBound()) f.invoke(event);
    }
    /*
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-create-event");
        if (f.isBound()) f.invoke(event);
    }
    */
  /*
    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-damage-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-destroy-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-enter-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-entity-collision-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-exit-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-move-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "vehicle-update-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "chunk-populate-event");
        if (f.isBound()) f.invoke(event);
    }
    @EventHandler
    public void onPaintingBreakByEntity(PaintingBreakByEntityEvent event) {
        clojure.lang.Var f = clojure.lang.RT.var("cljminecraft.core", "painting-break-by-entity-event");
        if (f.isBound()) f.invoke(event);
    }
    /* end auto-generated code */

}
