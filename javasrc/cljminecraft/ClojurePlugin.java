package cljminecraft;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import clojure.lang.*;
import clojure.lang.Compiler;

import java.io.*;
import java.lang.ClassLoader;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import java.util.logging.*;

/**
 * an instance of this class is create for every plugin (including the main cljminecraft one) that depends on cljminecraft, because
 * it will have to have in its plugin.yml the following:<br>
 * "main: cljminecraft.ClojurePlugin"
 *
 */
public class ClojurePlugin extends BasePlugin {
	
	private final static String selfPluginName="cljminecraft";//TODO: ClojurePlugin.class.getPackage()
	private final static String selfCoreScript="cljminecraft.core";
	private final static String selfEnableFunction="on-enable";
	private final static String selfDisableFunction="on-disable";
	public final static Charset UTF8 = Charset.forName("UTF-8");
	
	
	
	
//	@Override
//	public void onLoad() {
//		//XXX: executes once for each plugin
////		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!1onLoad!!!!!!!!!!!!!!!!!!!");
//	}
	
	
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
		String cljMsg = "Clojure resource `"+name
				+"` on classpath `" + getClassPath(classLoader)
				+"` using class loader `"+classLoader+"`";
		if ( is == null ) {
			throw new FileNotFoundException( "Can't find "+cljMsg);
		} else {
			info("About to load "+cljMsg);
		}
		
		try {
			InputStreamReader isr = new InputStreamReader( is, UTF8 );
			try {
				clojure.lang.Compiler.load( isr, name, file );//"memorystone/core.clj" and "core.clj"
				//the above call also loads "clojure/core" (if first time RT class initing)
				//which means it's using current thread's classloader; but only if it wasn't already inited ie. RT.class loaded
			} finally {
				isr.close();//FIXME: if this throws it should not overwrite previously thrown exception
			}
		} finally {
			is.close();//FIXME: also this
		}
		info( "Loaded "+cljMsg);
	}
	
	
	
	
//(nolonger applies): this works for cljminecraft plugin or for any child plugins having "class-loader-of: cljminecraft" in their plugin.yml
	//but if that's satisfied then config.yml (inside the child's .jar) will be shadowed by cljminecraft(inside its .jar)
	//due to them using the same classloader (as CmdrDats said)
    private boolean loadClojureFile(String cljFile) {//no synchronized needed
        try {
			
			System.out.println( "About to load clojure file: " + cljFile );
			//TODO: check for using Compiler.class.getClassLoader()
			showClassPath("0", Compiler.class.getClassLoader());
			showClassPath( "1", Thread.currentThread().getContextClassLoader() );

//			clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.Compiler.LOADER, getOurClassLoader()));
			DynamicClassLoader newCL=
//			try {
//				newCL=
				(DynamicClassLoader)AccessController.doPrivileged( new PrivilegedAction() {
					
					@Override
					public Object run() {
						return new DynamicClassLoader(
							Thread.currentThread().getContextClassLoader()
//							getOurClassLoader() 
							);
					}
				} );
//			}finally{
//				clojure.lang.Var.popThreadBindings();
//			}
			clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.Compiler.LOADER, newCL) );

//			loadClojureResourceScript( cljFile, getOurClassLoader() );
			
			showClassPath( "2", Thread.currentThread().getContextClassLoader() );
			newCL.addURL( this.getFile().toURI().toURL() );

			showClassPath( "3", (DynamicClassLoader)clojure.lang.Compiler.LOADER.deref());
			showClassPath( "4", Thread.currentThread().getContextClassLoader() );
			if (getDescription().getName().equals("moomoo")) {
				Thread.currentThread().setContextClassLoader( (DynamicClassLoader)clojure.lang.Compiler.LOADER.deref() );
			}
			showClassPath( "5", Thread.currentThread().getContextClassLoader() );
//			assert clojure.lang.RT.baseLoader() == getOurClassLoader();
//			showClassPath( "3", Thread.currentThread().getContextClassLoader() );
			
			clojure.lang.RT.loadResourceScript( cljFile );
//			Thread.currentThread().setContextClassLoader( getOurClassLoader() );
			//TODO: check if we can bind clojure.lang.Compiler.LOADER to the classloader instead of setting current thread' clsloader
			//XXX: setting this so that any future load scripts actually use this classloader :/
			
//TODO: check if config.yml options are still the same after some other child plugin loaded, but before the shutdown/stop happens which rolls them in reverse order so you can't tell if it really works
			return true;
		} catch ( Exception e ) {
			System.err.println( "Something broke setting up Clojure" );
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
