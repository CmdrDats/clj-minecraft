package cljminecraft;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.ClassLoader;
import java.util.*;

public class ClojurePlugin extends JavaPlugin {
	
	public final static String selfCoreScript="cljminecraft.core";
	public final static String selfEnableFunction="on-enable";
	public final static String selfDisableFunction="on-disable";
	public final static String childPlugin_EnableFunction="enable-plugin";
	public final static String childPlugin_DisableFunction="disable-plugin";
	
	//true if onEnable was successful, false or null(not found) if onEnable failed or was never executed
	private HashMap<String,Boolean> pluginState=new /*Concurrent*/HashMap<String,Boolean>();
	
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
			
			System.out.println( "Enabling " + pluginName + " clojure Plugin" );
			
			if ( "clj-minecraft".equals( pluginName ) ) {
				errored=!onEnableClojureMainOrChildPlugin( selfCoreScript, selfEnableFunction );
			} else {
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
        System.out.println("Disabling "+pluginName+" clojure Plugin");
        if ("clj-minecraft".equals(pluginName)) {
        	onDisableClojureMainOrChildPlugin(selfCoreScript, selfDisableFunction);
        } else {
			if ( wasSuccessfullyEnabled() ) {
				try {
					// so it was enabled(successfuly prior to this) then we can call to disable it
					onDisableClojureMainOrChildPlugin( pluginName + ".core", childPlugin_DisableFunction );
				} finally {
					// regardless of the failure to disable, we consider it disabled
					removeState();
				}
			}
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
