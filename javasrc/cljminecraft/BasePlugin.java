package cljminecraft;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
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
	public final static Charset UTF8 = Charset.forName("UTF-8");
	
	static {
		boolean a=false;
		assert (true == (a=true));
		PrintStream boo;
		if ( a ) {
			boo = System.err;//[SEVERE] when enabled (bad for production use)
		} else {
			boo = System.out;//[INFO] when not enabled (good for production use)
		}
		
		boo.println("assertions are "+(!a?"NOT ":"")+"enabled"+(!a?" (to enable pass jvm option -ea when starting bukkit)":""));
	
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
//		showClassPath("1", previous);
		final ClassLoader classLoader = ClojurePlugin.class.getClassLoader();
//		showClassPath("2", classLoader);
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			//this happens only once when ClojurePlugin.class gets loaded
			System.out.println("!!!!!!!!!!!!!First time clojure init!!!!!!!!!!!!!!!!!!!");
			System.out.flush();
//			clojure.lang.RT.EMPTY_ARRAY.equals( null );//it's assumed that's never null, or at least not inited as null
			//nolonger needing dymmy line above which causes RT.class to run its static initializer block which does load script clojure/core
			
			clojure.lang.DynamicClassLoader newCL = (clojure.lang.DynamicClassLoader)AccessController.doPrivileged( new PrivilegedAction() {
				@Override
				public Object run() {
//					showClassPath( "inRun1", this.getClass().getClassLoader() );
//					showClassPath( "inRun2", Thread.currentThread().getContextClassLoader() );
					assert classLoader == ClojurePlugin.class.getClassLoader();
					assert this.getClass().getClassLoader() == ClojurePlugin.class.getClassLoader();//even though "this" is different
					return new clojure.lang.DynamicClassLoader( classLoader );
				}
			} );
			clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.Compiler.LOADER, newCL) );
		}finally{
			Thread.currentThread().setContextClassLoader(previous);//hmm not restoring this works :O
		}
	}
	
	@Override
	public final void onLoad() {
		URL jarURL;
		// XXX: executes once for each plugin TODO: investigate what happens if plugman unload and load is used AND server `reload`
		//main concern is if the url already exists is it re-added? if not then is it re-ordered /moved at beginning or end?
		try {
			jarURL = this.getFile().toURI().toURL();
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( "should never happen", e );
		}
		
		System.out.println( "loading jar: " + jarURL );
		assert clojure.lang.Compiler.LOADER.isBound();
		( (clojure.lang.DynamicClassLoader)clojure.lang.Compiler.LOADER.deref() ).addURL( jarURL );
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
//			clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.Compiler.LOADER, thisPluginSClassLoader ) );
			//XXX: why isn't the above equivalent to the below? hmm...
//			Thread.currentThread().setContextClassLoader( thisPluginSClassLoader );//oh wait this isn't working either, at this location
		}
		
		assert null != thisPluginSClassLoader;
		
//		assert clojure.lang.RT.baseLoader() == thisPluginSClassLoader;
				
//		assert (clojure.lang.Compiler.LOADER.isBound());
//		assert clojure.lang.RT.baseLoader() == clojure.lang.Compiler.LOADER.deref();
//		assert thisPluginSClassLoader == clojure.lang.Compiler.LOADER.deref();
//		return (ClassLoader)clojure.lang.Compiler.LOADER.deref();
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
    public abstract boolean start();//TODO: rename these or the clojure ones just so it's no confusion when reading code(because they have same name)
	
	
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
