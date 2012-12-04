package cljminecraft;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;


public abstract class BasePlugin extends JavaPlugin{
	private final Logger logger=Bukkit.getLogger();
//	private final static Logger logger=Logger.getLogger( "Minecraft" );
	
	//true if onEnable was successful, false or null(not found) if onEnable failed or was never executed
	private Boolean successfullyEnabled=null;//each plugin will have one of these
	private ClassLoader thisPluginSClassLoader=null;//each (main/child)plugin can have (different)one
	
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
	
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		showClassPath("1", previous);
		ClassLoader classLoader = ClojurePlugin.class.getClassLoader();
		showClassPath("2", classLoader);
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			//this happens only once when ClojurePlugin.class gets loaded
			System.out.println("!!!!!!!!!!!!!First time clojure init!!!!!!!!!!!!!!!!!!!");
			System.out.flush();
			clojure.lang.RT.EMPTY_ARRAY.equals( null );//it's assumed that's never null, or at least not inited as null
		}finally{
//			Thread.currentThread().setContextClassLoader(previous);hmm not restoring this works :O
			//XXX: we're losing the bukkit classpath ? is that even needed? or is part of a parent classpath anyway?
/*
10:48:14 [INFO] ==1== For classloader sun.misc.Launcher$AppClassLoader@4aad3ba4----------
10:48:14 [INFO] { file:/S:/cb/craftbukkit-1.4.5-R0.3-20121201.071839-14.jar }
10:48:14 [INFO] ==1== ----END---sun.misc.Launcher$AppClassLoader@4aad3ba4 ----------
10:48:14 [INFO] ==2== For classloader org.bukkit.plugin.java.PluginClassLoader@31f39c59 ----------
10:48:14 [INFO] { file:/S:/cb/plugins/clj-minecraft-1.0.1-SNAPSHOT-standalone.jar }
10:48:14 [INFO] ==2== ----END---org.bukkit.plugin.java.PluginClassLoader@31f39c59 ----------
 */
		}
	}
	
	public BasePlugin() {
		//constructor
		info("CONSTRUCTOR");
		//XXX: an instance is created of this class for every child plugin (including the main one) 
		//TODO: maybe add a test to make sure this didn't change in the future
	}
	
	public static void showClassPath(String prefix, ClassLoader cl){
		System.out.println("=="+prefix+"== For classloader "+cl+" ----------");
		System.out.println(getClassPath(cl));
        System.out.println("=="+prefix+"== ----END---"+cl+" ----------");
	}
	
	
	public final static String getClassPath() {
		return getClassPath(Thread.currentThread().getContextClassLoader());
	}
	
	public final static String getClassPath(ClassLoader cl) {
		URL[] urls = ((URLClassLoader)cl).getURLs();
		String cp ="{";
		
		int max = urls.length-1;
		if (max>=0){
			cp+=" ";
		}
		for ( int i = 0; i <= max; i++ ) {
			URL url = urls[i];
        	try {
				cp+= url.toURI().toString();
				if(i != max) {
					cp+=", ";
				}else {
					cp+=" ";
				}
			} catch ( URISyntaxException use ) {
				use.printStackTrace();
				throw new RuntimeException(use);
			}
        }
        cp+="}";
        return cp;
	}
	
	public ClassLoader getOurClassLoader() {
		if ( null == thisPluginSClassLoader ) {
			// one time (for the current plugin) classloader set
			Class<?> cls = this.getClass();
			URL url;
			try {
				url=this.getFile().toURI().toURL();
				
			} catch ( MalformedURLException e ) {// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("should not happen",e);
			}
			
			assert null != url;
			URL urls[] = {
				url
			};
			
			thisPluginSClassLoader = new URLClassLoader( urls, cls.getClassLoader() );
		}
		
		assert null != thisPluginSClassLoader;
		return thisPluginSClassLoader;
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
		// there are only two ways: [INFO]+colors+suffix, or no colors + whichever suffix
		ConsoleCommandSender cons = Bukkit.getConsoleSender();
		if (null != cons) {
			cons.sendMessage( msg );// this will log with [INFO] level
		}else {
			logger.info(ChatColor.stripColor( msg));
		}
	}
    
    public void setSuccessfullyEnabled() {
		assert (null == successfullyEnabled) || (false == successfullyEnabled.booleanValue())
			:"should not have been already enabled without getting disabled first";
		
    	successfullyEnabled=Boolean.TRUE;
    }
    
    public void removeEnabledState() {
    	assert ((null == successfullyEnabled) || (true == successfullyEnabled.booleanValue()));
		successfullyEnabled=null;
    }
    
    public boolean wasSuccessfullyEnabled() {
    	return ((null != successfullyEnabled) && (true == successfullyEnabled.booleanValue()));
    }
    
    /**
     * if it doesn't return true, then stop() will not be called<br>
     * @return true if successfully enabled or false(or thrown exceptions) otherwise<br>
     */
    public abstract boolean start();
	
	
	/**
	 * called only if start() didn't fail (that is: it returned true and didn't throw exceptions)
	 * 
	 */
	public abstract void stop();

	
    //synchronized not needed because it's an instance method and each plugin has a different instance
	@Override
	public final void onEnable() {
		assert isEnabled() : "it should be set to enabled before this is called, by bukkit";
		
		if ( start() ) {
			setSuccessfullyEnabled();
		}
	}
	
	
	@Override
	public final void onDisable() {//called only when onEnable didn't fail (if we did the logic right)
    	assert !isEnabled():"it should be set to disabled before this is called, by bukkit";
    	
        String pluginName = getDescription().getName();
		if ( wasSuccessfullyEnabled() ) {
			// so it was enabled(successfully prior to this) then we can call to disable it
			try {
				stop();//return state unused
			} finally {
				// regardless of the failure to disable, we consider it disabled
				removeEnabledState();
			}
		} else {
			info( "did not attempt to disable " + pluginName
				+ " clojure Plugin because it wasn't successfully enabled previously" );
		}
    }
	
}
