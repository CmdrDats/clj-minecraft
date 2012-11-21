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

public class ClojurePlugin extends JavaPlugin {
    public void onEnable(String ns, String enableFunction) {
        try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            clojure.lang.RT.loadResourceScript(ns.replaceAll("[.]", "/")+".clj");
            clojure.lang.RT.var(ns, enableFunction).invoke(this);

            Thread.currentThread().setContextClassLoader(previous);
        } catch (Exception e) {
            System.out.println("Something broke setting up Clojure");
            e.printStackTrace();
        }
    }

    public void onEnable() {
        String name = getDescription().getName();
        System.out.println("Enabling "+name+" clojure Plugin");

        if ("clj-minecraft".equals(name)) {
            onEnable("cljminecraft.core", "on-enable");
            getServer().getPluginManager().registerEvents(new PluginListener (), this);
        } else {
            onEnable(name+".core", "enable-plugin");
        }
    }

    public void onDisable(String ns, String disableFunction) {
        clojure.lang.RT.var(ns, disableFunction).invoke(this);
    }

    public void onDisable() {
        String name = getDescription().getName();
        System.out.println("Disabling "+name+" clojure Plugin");
        if ("clj-minecraft".equals(name)) {
            onEnable("cljminecraft.core", "on-disable");
        } else {
            onEnable(name+".core", "disable-plugin");
        }
    }
}
