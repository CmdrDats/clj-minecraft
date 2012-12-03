package cljminecraft;

import java.io.*;
import java.util.logging.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;


public abstract class BasePlugin extends JavaPlugin{
	private final Logger logger=Bukkit.getLogger();
//	private final static Logger logger=Logger.getLogger( "Minecraft" );
	
	//true if onEnable was successful, false or null(not found) if onEnable failed or was never executed
//	private final static HashMap<String,Boolean> pluginState=new /*Concurrent*/HashMap<String,Boolean>();
	private Boolean successfullyEnabled=null;//each plugin will have one of these
	
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
	
	public BasePlugin() {
		//constructor
		info("CONSTRUCTOR");
		//XXX: an instance is created of this class for every child plugin (including the main one) 
		//TODO: maybe add a test to make sure this didn't change in the future
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
//    	String pluginName = getDescription().getName();
    	
//    	Boolean wasAlreadyEnabled = pluginState.get( pluginName );
		assert (null == successfullyEnabled) || (false == successfullyEnabled.booleanValue())
		//((null == wasAlreadyEnabled) || (false == wasAlreadyEnabled.booleanValue()))
			:"should not have been already enabled without getting disabled first";
		
//    	pluginState.put( pluginName, Boolean.TRUE );
    	successfullyEnabled=Boolean.TRUE;
    }
    
    public void removeEnabledState() {
//    	String pluginName = getDescription().getName();
//    	Boolean state = pluginState.get( pluginName);
//		Boolean ret = pluginState.remove( pluginName );// no point keeping the false value in I guess
//		assert state == ret;
    	assert ((null == successfullyEnabled) || (true == successfullyEnabled.booleanValue()));
		successfullyEnabled=null;
    }
    
    public boolean wasSuccessfullyEnabled() {
//    	String pluginName = getDescription().getName();
//    	Boolean state = pluginState.get( pluginName);
    	return ((null != successfullyEnabled) && (true == successfullyEnabled.booleanValue()));
    }
}
