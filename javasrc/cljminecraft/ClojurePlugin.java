package cljminecraft;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.ClassLoader;
import java.util.*;
import java.util.logging.*;

/**
 * an instance of this class is create for every plugin (including the main cljminecraft one) that depends on cljminecraft, because
 * it will have to have in its plugin.yml the following:<br>
 * "main: cljminecraft.ClojurePlugin"
 *
 */
public class ClojurePlugin extends BasePlugin {
	
	public final static String selfPluginName="cljminecraft";
	public final static String selfCoreScript="cljminecraft.core";
	public final static String selfEnableFunction="on-enable";
	public final static String selfDisableFunction="on-disable";
	public final static String childPlugin_EnableFunction="enable-plugin";
	public final static String childPlugin_DisableFunction="disable-plugin";
	public final static String childPlugin_CoreScript="core";
	
    private boolean loadClojureFile(String cljFile) {
        try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			try {
				System.out.println( "loading clojure file: " + cljFile );
				clojure.lang.RT.loadResourceScript( cljFile );
				
			} finally {
				Thread.currentThread().setContextClassLoader( previous );
			}
            return true;
        } catch (Exception e) {
            System.out.println("Something broke setting up Clojure");
            e.printStackTrace();
            return false;
        }
    }
    
	
	public final boolean loadClojureNameSpace( String ns ) {
		String cljFile = ns.replaceAll( "[.]", "/" ) + ".clj";
		return loadClojureFile( cljFile );
	}
    
    public Object invokeClojureFunction(String ns, String funcName) {
    	return clojure.lang.RT.var(ns, funcName).invoke(this);//passing the plugin instance as param
    }

    
    public boolean onEnableClojureMainOrChildPlugin(String ns, String enableFunction) {
    	if (loadClojureNameSpace(ns)) {
    		invokeClojureFunction(ns, enableFunction);
    		return true;
    	}else{
    		return false;
    	}
    }

    @Override
	public boolean start() {
    	
		String pluginName = getDescription().getName();
		
		boolean success = false;
		if ( selfPluginName.equals( pluginName ) ) {
			info( "Enabling main " + pluginName + " clojure Plugin" );
			success = onEnableClojureMainOrChildPlugin( selfCoreScript, selfEnableFunction );
		} else {
			info( "Enabling child " + pluginName + " clojure Plugin" );
			success = onEnableClojureMainOrChildPlugin( pluginName + "."+childPlugin_CoreScript, childPlugin_EnableFunction );
		}
		
		return success;
    }

    public void onDisableClojureMainOrChildPlugin(String ns, String disableFunction) {
    	invokeClojureFunction(ns, disableFunction);
    }

    
    @Override
	public void stop() {//called only when onEnable didn't fail (if we did the logic right)
		String pluginName = getDescription().getName();
		if ( selfPluginName.equals( pluginName ) ) {
			info( "Disabling main " + pluginName + " clojure Plugin" );
			onDisableClojureMainOrChildPlugin( selfCoreScript, selfDisableFunction );
		} else {
			info( "Disabling child " + pluginName + " clojure Plugin" );
			onDisableClojureMainOrChildPlugin( pluginName + "."+childPlugin_CoreScript, childPlugin_DisableFunction );
		}
    }
    

/*in plugin.yml of your clojure plugin which depends on cljminecraft, these are required:
 * 
 * main: cljminecraft.ClojurePlugin
 * depend: [cljminecraft]
 * class-loader-of: cljminecraft
 * 
 * and the name of your plugin(in your plugin.yml) should be the ns name of core.clj and core.clj should be the main script 
 * which includes the enable/disable functions which are defined by the constants: childPlugin_EnableFunction and
 * childPlugin_DisableFunction in the above
 * 
  */  
}
