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
	public synchronized void onEnable() {
    	assert isEnabled():"it should be set to enabled before this is called, by bukkit";
    	
    	boolean errored=false;
		try {
			String pluginName = getDescription().getName();
			
			
			
			if ( selfPluginName.equals( pluginName ) ) {
				info( "Enabling main " + pluginName + " clojure Plugin" );
				errored=!onEnableClojureMainOrChildPlugin( selfCoreScript, selfEnableFunction );
			} else {
				info( "Enabling child " + pluginName + " clojure Plugin" );
				errored=!onEnableClojureMainOrChildPlugin( pluginName + ".core", childPlugin_EnableFunction );
			}

			//handle both main and children plugins
			if (!errored) {
//				successfullyEnabled=true;
				setSuccessfullyEnabled();
//				pluginState.put( pluginName, Boolean.TRUE );
			}
    	}catch(Throwable t) {
    		errored=true;
    		t.printStackTrace();
		} finally {
			if ( errored ) {
				setEnabled( !errored );//avoid calling onDisable if onEnable failed! actually this fails because it internally calls onDisable or onEnable if state changed
			}
		}
    }

    public void onDisableClojureMainOrChildPlugin(String ns, String disableFunction) {
    	invokeClojureFunction(ns, disableFunction);
    }

    @Override
	public synchronized void onDisable() {//called only when onEnable didn't fail (if we did the logic right)
    	assert !isEnabled():"it should be set to disabled before this is called, by bukkit";
    	
        String pluginName = getDescription().getName();
		if ( wasSuccessfullyEnabled() ) {
			// so it was enabled(successfully prior to this) then we can call to disable it
			try {
				if ( selfPluginName.equals( pluginName ) ) {
					info( "Disabling main " + pluginName + " clojure Plugin" );
					onDisableClojureMainOrChildPlugin( selfCoreScript, selfDisableFunction );
				} else {
					info( "Disabling child " + pluginName + " clojure Plugin" );
					onDisableClojureMainOrChildPlugin( pluginName + ".core", childPlugin_DisableFunction );
				}
			} finally {
				// regardless of the failure to disable, we consider it disabled
				removeEnabledState();
			}
		} else {
			info( "did not attempt to disable " + pluginName
				+ " clojure Plugin because it wasn't successfully enabled previously" );
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
