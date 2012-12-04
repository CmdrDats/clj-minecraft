package cljminecraft;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.ClassLoader;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;

/**
 * an instance of this class is create for every plugin (including the main cljminecraft one) that depends on cljminecraft, because
 * it will have to have in its plugin.yml the following:<br>
 * "main: cljminecraft.ClojurePlugin"
 *
 */
public class ClojurePlugin extends BasePlugin {
	
	private final static String selfPluginName="cljminecraft";
	private final static String selfCoreScript="cljminecraft.core";
	private final static String selfEnableFunction="on-enable";
	private final static String selfDisableFunction="on-disable";
	public final static Charset UTF8 = Charset.forName("UTF-8");
	
	private void showClassPath(String prefix, ClassLoader cl){
		System.out.println("=="+prefix+"== For classloader "+cl+" ----------");
		System.out.println(getClassPath(cl));
//        URL[] urls = ((URLClassLoader)cl).getURLs();
// 
//        for(URL url: urls){
//        	System.out.println(url.getPath());//getFile());
//        	try {
//				System.out.println(url.toURI());
//			} catch ( URISyntaxException e ) {
//				e.printStackTrace();
//			}
//        }
        System.out.println("=="+prefix+"== ----END---"+cl+" ----------");
	}
	
	
	private final static String getClassPath() {
		return getClassPath(Thread.currentThread().getContextClassLoader());
	}
	
	private final static String getClassPath(ClassLoader cl) {
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
	
	
	public void loadClojureResourceScript( String name ) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		loadClojureResourceScript(name, classLoader);
	}
	/**
	 * @param name ie. "memorystone/core.clj"
	 * @param classLoader ie. Thread.currentThread().getContextClassLoader()
	 * @throws IOException 
	 */
	public void loadClojureResourceScript( String name, ClassLoader classLoader ) throws IOException {
		assert null != name;
		assert name.length() > 0;
		assert null != classLoader;
		
		int slash = name.lastIndexOf( '/' );
		String file = slash >= 0 ? name.substring( slash + 1 ) : name;//"core.clj"
		InputStream is = classLoader.getResourceAsStream(name);
		if ( is == null ) {
			throw new FileNotFoundException( "Can't find Clojure resource `"+name+"` on classpath `" + getClassPath()
				+"` using class loader `"+classLoader+"`");
		}
		//else
		
		try {
			InputStreamReader isr = new InputStreamReader( is, UTF8 );
			try {
				ClassLoader previous = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(classLoader);
				try {
					clojure.lang.Compiler.load( isr, name, file );//"memorystone/core.clj" and "core.clj"
					//the above call also loads "clojure/core" (if first time RT class initing)
					//which means it's using current thread's classloader
				}finally{
					Thread.currentThread().setContextClassLoader(previous);
				}
			} finally {
				isr.close();//FIXME: if this throws it should not overwrite previously thrown exception
			}
		} finally {
			is.close();//FIXME: also this
		}
		info( "Loaded Clojure resource `"+name+"` on classpath `" + getClassPath()
				+"` using class loader `"+classLoader+"`");
	}
	
	//XXX: this works for cljminecraft plugin or for any child plugins having "class-loader-of: cljminecraft" in their plugin.yml
	//but if that's satisfied then config.yml (inside the child's .jar) will be shadowed by cljminecraft(inside its .jar)
	//due to them using the same classloader (as CmdrDats said)
    private boolean loadClojureFile(String cljFile) {//no synchronized needed
        try {
			Class<?> cls =this.getClass(); 
			URL urls [] = {this.getFile().toURI().toURL()};
			URLClassLoader cl = new URLClassLoader( urls, cls.getClassLoader() );
			
//			ClassLoader previous = Thread.currentThread().getContextClassLoader();
//			Thread.currentThread().setContextClassLoader(new clojure.lang.DynamicClassLoader(previous));
			
			try {
				System.out.println( "loading clojure file: " + cljFile );
//				clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.RT.USE_CONTEXT_CLASSLOADER, clojure.lang.RT.F ) );

				//the problem with this is that RT is trying to load "clojure/core"
//				clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.RT.USE_CONTEXT_CLASSLOADER, clojure.lang.RT.F ) );
				
				loadClojureResourceScript( cljFile, cl );
			}finally {
//				Thread.currentThread().setContextClassLoader(previous);
				try {
//					clojure.lang.Var.popThreadBindings();
				} finally {
//					Thread.currentThread().setContextClassLoader( previous );
				}
			}
			
			return true;
		} catch ( Exception e ) {
			System.out.println( "Something broke setting up Clojure" );
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

    @Override
	public boolean start() {
    	
		String pluginName = getDescription().getName();
		
		boolean success = false;
		if ( selfPluginName.equals( pluginName ) ) {
			info( "Enabling main " + pluginName + " clojure Plugin" );
			success = loadClojureNameSpace(selfCoreScript);
		} else {
			info( "Enabling child " + pluginName + " clojure Plugin" );
			//the child plugin must have in plugin.yml: class-loader-of: cljminecraft
			//or the following will fail:
			success = loadClojureNameSpace(pluginName+".core");
		}

		invokeClojureFunction(selfCoreScript, selfEnableFunction );
		
		return success;
    }

    
    @Override
	public void stop() {//called only when onEnable didn't fail (if we did the logic right)
		String pluginName = getDescription().getName();
		if ( selfPluginName.equals( pluginName ) ) {
			info( "Disabling main " + pluginName + " clojure Plugin" );
		} else {
			info( "Disabling child " + pluginName + " clojure Plugin" );
		}
		invokeClojureFunction( selfCoreScript, selfDisableFunction );

    }
    

/*in plugin.yml of your clojure plugin which depends on cljminecraft, these are required:
 * 
 * main: cljminecraft.ClojurePlugin
 * depend: [cljminecraft]
 * 
 * and the name of your plugin(in your plugin.yml) should be the ns name of core.clj and core.clj should be the main script 
 * which includes the two methods start and stop which take plugin instance as parameter
 * 
  */  
}
