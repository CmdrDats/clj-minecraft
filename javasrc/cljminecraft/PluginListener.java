package cljminecraft;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.Event;
//import org.bukkit.event.player.*;
//import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
//import org.bukkit.event.vehicle.*;
//import org.bukkit.event.world.*;
//import org.bukkit.event.painting.*;

public class PluginListener  implements Listener {
    private String ns;


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.ns = "cljminecraft.core";
        clojure.lang.Var f = clojure.lang.RT.var(ns, "block-break-event");
        if (f.isBound()) f.invoke(event);
    }


}
