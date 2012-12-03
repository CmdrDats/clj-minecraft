package cljminecraft;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.ClassLoader;

public class ClojurePlugin extends JavaPlugin {
	
    public void loadClojureFile(String cljFile) {
        try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            System.out.println("loadscript: " + cljFile);
            clojure.lang.RT.loadResourceScript(cljFile+".clj");

            Thread.currentThread().setContextClassLoader(previous);
        } catch (Exception e) {
            System.out.println("Something broke setting up Clojure");
            e.printStackTrace();
        }
    	
    }
    
    public boolean loadClojureNameSpace(String ns) {
        try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            String cljFile = ns.replaceAll("[.]", "/")+".clj";
            System.out.println("loading clojure file: " + cljFile);
            clojure.lang.RT.loadResourceScript(cljFile);

            Thread.currentThread().setContextClassLoader(previous);
            return true;
        } catch (Exception e) {
            System.out.println("Something broke setting up Clojure");
            e.printStackTrace();
            return false;
        }
    	
    }
    
    public Object invokeClojureFunction(String ns, String funcName) {
    	return clojure.lang.RT.var(ns, funcName).invoke(this);
    }


    
    public void onEnableClojureScript(String ns, String enableFunction) {
    	if (loadClojureNameSpace(ns)) {
    		invokeClojureFunction(ns, enableFunction);
//    		clojure.lang.RT.var(ns, enableFunction).invoke(this);
    	}
//        try {
//            ClassLoader previous = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
//
//            System.out.println("loadscript: " + ns.replaceAll("[.]", "/"));
//            clojure.lang.RT.loadResourceScript(ns.replaceAll("[.]", "/")+".clj");
//            
//            clojure.lang.RT.var(ns, enableFunction).invoke(this);
//            
//
//            Thread.currentThread().setContextClassLoader(previous);
//        } catch (Exception e) {
//            System.out.println("Something broke setting up Clojure");
//            e.printStackTrace();
//        }
    }

    @Override
	public void onEnable() {
        String name = getDescription().getName();
        System.out.println("Enabling "+name+" clojure Plugin");

        if ("clj-minecraft".equals(name)) {
        	onEnableClojureScript("cljminecraft.core", "on-enable");
        } else {
        	onEnableClojureScript(name+".core", "enable-plugin");
        }
    }

    public void onDisableClojureScript(String ns, String disableFunction) {
//        clojure.lang.RT.var(ns, disableFunction).invoke(this);
    	invokeClojureFunction(ns, disableFunction);
    }

    @Override
	public void onDisable() {
        String name = getDescription().getName();
        System.out.println("Disabling "+name+" clojure Plugin");
        if ("clj-minecraft".equals(name)) {
        	onDisableClojureScript("cljminecraft.core", "on-disable");
        } else {
            onDisableClojureScript(name+".core", "disable-plugin");
        }
    }
    
/*in plugin.yml of your clojure plugin which depends on clj-minecraft, these are required:
 * main: cljminecraft.ClojurePlugin
 * depend: [clj-minecraft]
 * class-loader-of: clj-minecraft
  */  
}
