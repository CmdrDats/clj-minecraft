package cljminecraft;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.ClassLoader;

public class ClojurePlugin extends JavaPlugin {
	
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
    	return clojure.lang.RT.var(ns, funcName).invoke(this);
    }

    
    public boolean onEnableClojureChildPlugin(String ns, String enableFunction) {
    	if (loadClojureNameSpace(ns)) {
    		invokeClojureFunction(ns, enableFunction);
    		return true;
    	}else{
    		return false;
    	}
    }

    @Override
	public void onEnable() {
    	boolean errored=false;
		try {
			String pluginName = getDescription().getName();
			System.out.println( "Enabling " + pluginName + " clojure Plugin" );
			
			if ( "clj-minecraft".equals( pluginName ) ) {
				errored=!onEnableClojureChildPlugin( selfCoreScript, selfEnableFunction );
			} else {
				errored=!onEnableClojureChildPlugin( pluginName + ".core", childPlugin_EnableFunction );
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

    public void onDisableClojureChildPlugin(String ns, String disableFunction) {
    	invokeClojureFunction(ns, disableFunction);
    }

    @Override
	public void onDisable() {//called only when onEnable didn't fail (if we did the logic right)
        String pluginName = getDescription().getName();
        System.out.println("Disabling "+pluginName+" clojure Plugin");
        if ("clj-minecraft".equals(pluginName)) {
        	onDisableClojureChildPlugin(selfCoreScript, selfDisableFunction);
        } else {
            onDisableClojureChildPlugin(pluginName+".core", childPlugin_DisableFunction);
        }
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
