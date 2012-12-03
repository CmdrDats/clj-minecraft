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
 * an instance of this class is create for every plugin (including the main clj-minecraft one) that depends on clj-minecraft, because
 * it will have to have in its plugin.yml the following:<br>
 * "main: cljminecraft.ClojurePlugin"
 *
 */
public class ClojurePlugin extends JavaPlugin {
	
	public final static String selfCoreScript="cljminecraft.core";
	public final static String selfEnableFunction="on-enable";
	public final static String selfDisableFunction="on-disable";
	public final static String childPlugin_EnableFunction="enable-plugin";
	public final static String childPlugin_DisableFunction="disable-plugin";
	
	private final Logger logger=Bukkit.getLogger();
//	private final static Logger logger=Logger.getLogger( "Minecraft" );
	
	//true if onEnable was successful, false or null(not found) if onEnable failed or was never executed
	private final static HashMap<String,Boolean> pluginState=new /*Concurrent*/HashMap<String,Boolean>();
	
	static {
		boolean a=false;
		assert (true == (a=true));
		PrintStream boo;
		if ( a ) {
			boo = System.err;//[SEVERE] when enabled (bad for production use)
		} else {
			boo = System.out;//[INFO] when not enabled (good for production use)
		}
		
		boo.println("assertions are "+(!a?"NOT ":"")+"enabled"+(!a?" (to enable pass jvm option -ea when starting bukkit":""));
	}
	
	public ClojurePlugin() {
		//constructor
		info("CONSTRUCTOR");
		//XXX: hmm  an instance is create of this class for every child plugin (including the main one) 
	}
	
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
    	return clojure.lang.RT.var(ns, funcName).invoke(this);
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
			
			
			
			if ( "clj-minecraft".equals( pluginName ) ) {
				info( "Enabling main " + pluginName + " clojure Plugin" );
				errored=!onEnableClojureMainOrChildPlugin( selfCoreScript, selfEnableFunction );
			} else {
				info( "Enabling child " + pluginName + " clojure Plugin" );
				errored=!onEnableClojureMainOrChildPlugin( pluginName + ".core", childPlugin_EnableFunction );
				if (!errored) {
					pluginState.put( pluginName, Boolean.TRUE );
				}
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
        if ("clj-minecraft".equals(pluginName)) {
        	info("Disabling main "+pluginName+" clojure Plugin");
        	onDisableClojureMainOrChildPlugin(selfCoreScript, selfDisableFunction);
        } else {
			if ( wasSuccessfullyEnabled() ) {
				try {
					info("Disabling child "+pluginName+" clojure Plugin");
					// so it was enabled(successfuly prior to this) then we can call to disable it
					onDisableClojureMainOrChildPlugin( pluginName + ".core", childPlugin_DisableFunction );
				} finally {
					// regardless of the failure to disable, we consider it disabled
					removeState();
				}
			}else {
				info("did not attempt to disable "+pluginName+" clojure Plugin because it wasn't successfully enabled previously");
			}
        }
    }
    
    public final void info(String msg) {
    	PluginDescriptionFile descFile = getDescription();
    	String pluginName = this.getClass().getName();
    	if (null != descFile) pluginName=descFile.getName();
    	tellConsole(ChatColor.GREEN+"["+pluginName+"]"+ChatColor.RESET+" "+msg);
    }
    
    public final void tellConsole( String msg ) {
		// nvm; find another way to display colored msgs in console without having [INFO] prefix
		// there's no other way it's done via ColouredConsoleSender of craftbukkit
		// there are only two ways: colors+[INFO] prefix, or no colors + whichever prefix
		ConsoleCommandSender cons = Bukkit.getConsoleSender();
		if (null != cons) {
			cons.sendMessage( msg );// this will log with [INFO] level
		}else {
			logger.info(ChatColor.stripColor( msg));
		}
	}
    
    public void setSuccessfullyEnabled() {
    	String pluginName = getDescription().getName();
    	
    	Boolean wasAlreadyEnabled = pluginState.get( pluginName );
		assert ((null == wasAlreadyEnabled) || (false == wasAlreadyEnabled.booleanValue()))
			:"should not have been already enabled without getting disabled first";
		
    	pluginState.put( pluginName, Boolean.TRUE );
    }
    
    public void removeState() {
    	String pluginName = getDescription().getName();
    	Boolean state = pluginState.get( pluginName);
		Boolean ret = pluginState.remove( pluginName );// no point keeping the false value in I guess
		assert state == ret;
    }
    
    public boolean wasSuccessfullyEnabled() {
    	String pluginName = getDescription().getName();
    	Boolean state = pluginState.get( pluginName);
    	return ((null != state) && (true == state.booleanValue()));
    }
/*in plugin.yml of your clojure plugin which depends on clj-minecraft, these are required:
 * 
 * main: cljminecraft.ClojurePlugin
 * depend: [clj-minecraft]
 * class-loader-of: clj-minecraft
 * 
 * and the name of your plugin(in your plugin.yml) should be the ns name of core.clj and core.clj should be the main script 
 * which includes the enable/disable functions which are defined by the constants: childPlugin_EnableFunction and
 * childPlugin_DisableFunction in the above
 * 
  */  
}
