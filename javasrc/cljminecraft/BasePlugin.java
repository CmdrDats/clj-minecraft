package cljminecraft;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import java.util.logging.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

public abstract class BasePlugin extends JavaPlugin{
	protected final static String selfPluginName=ClojurePlugin.class.getPackage().getName();//"cljminecraft";

	public final static Charset UTF8 = Charset.forName("UTF-8");

	private final static Logger logger=Bukkit.getLogger();//it would've been the same instance across both main and child plugins
//	private final static Logger logger=Logger.getLogger( "Minecraft" );//this is equivalent to above

	//true when `reload` happened OR when this plugin got loaded to an already running server ie. via `plugman load pluginnamehere`
	private static boolean	isServerReloaded=(null != Bukkit.getConsoleSender());

	//true if onEnable was successful, false or null(not found) if onEnable failed or was never executed
	private Boolean successfullyEnabled=null;//each plugin will have one of these

	static {//static initializer block
		//XXX: on server `reload` this gets re-executed
		if ( !isServerReloaded() ) {
			boolean asserts = false;
			assert ( true == ( asserts = true ) );

			_info( "assertions are " + ( !asserts ? "NOT " : "" ) + "enabled"
				+ ( !asserts ? " (to enable pass jvm option -ea when starting bukkit)" : "" ) );
		}

		if (isServerReloaded()) {
			_info("you just `reload`-ed the server OR this plugin got loaded to an already running server ie. via `plugman load "
					+BasePlugin.class.getPackage().getName()+"`");
			//EDIT: there's one variant which may wrongly detect a `reload` if you're using this:
			//if you were running the server then then you just place your plugin in plugins folder and execute a
			//command something like `plugman load yourplugin` - it will detect it as a reload because getConsoleSender
			//is not null at this point. (tested to be true)
			//EDIT2: also note that if the plugin was already running doing `plugman unload it` then `plugman load it`
			//(or even `plugman reload it`) won't cause it to be detected as a `reload`


		}

		//this should only be executed for cljminecraft(the main not any children) plugin, and it is so if children have a depend on cljminecraft
		//bukkit will then make sure cljminecraft is loaded before them

		//one time in bukkit lifetime(right?) we set *loader* to the classloader which applies to any future clojure scripts loads
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		final ClassLoader parentClassLoader = ClojurePlugin.class.getClassLoader();
		Thread.currentThread().setContextClassLoader(parentClassLoader);
		try {
//			if (isServerReloaded()) {
////				clojure.lang.Var.popThreadBindings();great there is nothing pushed
//				assert !clojure.lang.Compiler.LOADER.isBound();
//			}
			//this happens only once when ClojurePlugin.class gets loaded which actually happens once at bukkit server startup AND
			//also happens every time there's a `reload` command executed
			if (isServerReloaded()) {
				_info("!!!!!!!!!!!!!clojure reinit!!");
			}else{
				_info("!!!!!!!!!!!!!First time clojure init!!");
			}
			System.out.flush();

			clojure.lang.DynamicClassLoader newCL = (clojure.lang.DynamicClassLoader)AccessController.doPrivileged( new PrivilegedAction() {
				@Override
				public Object run() {
					assert parentClassLoader == ClojurePlugin.class.getClassLoader();
					assert this.getClass().getClassLoader() == ClojurePlugin.class.getClassLoader();//even though "this" is different
					return new clojure.lang.DynamicClassLoader( parentClassLoader );
				}
			} );
			assert !clojure.lang.Compiler.LOADER.isBound();//wow this really isn't bound even after `reload` also nothing remains pushed
                        //Addresses NullPointerException with clojure 1.5.
                        //See http://dev.clojure.org/jira/browse/CLJ-1172
                        //Note that there should be a better way to do this...
                        clojure.lang.RT.init();
                        clojure.lang.Var.pushThreadBindings( clojure.lang.RT.map( clojure.lang.Compiler.LOADER, newCL) );//so this variant is the one
//			System.err.println(clojure.lang.RT.CLOJURE_NS);
//			clojure.lang.Var.intern(clojure.lang.RT.CLOJURE_NS,//just as I thought this variant won't work
//				Symbol.intern("clojure.lang.Compiler/LOADER")//can't intern namespace qualified symbol
////				clojure.lang.Compiler.LOADER.sym this is actually null because there's no counterpart in clojure? :O I thought it was *loader*
//				, newCL, true);
//			clojure.lang.RT.WARN_ON_REFLECTION;
			//XXX: turn on reflection warnings for all plugins (maybe add/override this in each config.yml
			//XXX: does the above make sense? not sure how
			//no: maybe specify namespace to that *var* - can't intern namespace qualified symbol(I predict)
			clojure.lang.Var.intern(clojure.lang.RT.CLOJURE_NS,
				clojure.lang.Symbol.intern("*warn-on-reflection*")
				//there's no accessible java field from which to get the symbol directly (they are non-public but there in RT nd Compiler classes)
				,
//				isServerReloaded()?clojure.lang.RT.T:clojure.lang.RT.F
				clojure.lang.RT.F
				, true);
			//the above is equivalent to clojure code: (set! *warn-on-reflection* true)

			clojure.lang.Var.intern(clojure.lang.RT.CLOJURE_NS,
				clojure.lang.Symbol.intern("*use-context-classloader*")
				,clojure.lang.RT.F
				, true);
		}finally{
			Thread.currentThread().setContextClassLoader(previous);
		}
	}

	/**
	 * @return true when `reload` happened on server<br>
	 * OR when this plugin got loaded to an already running server ie. via `plugman load pluginnamehere`
	 */
	public final static boolean isServerReloaded() {
		//TODO: find a way to know when it was actually `reload` or our plugin got inited the first time
		//or do we simply give no support for the variant of `plugman load cljminecraft` into an already running server(after just having placed the .jar first time in plugins folder)?
		return isServerReloaded;
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

		info( "loaded jar: " + jarURL );
		assert clojure.lang.Compiler.LOADER.isBound();
		if (!getName().equals(selfPluginName)) {
			//we don't have to add cljminecraft.jar to classpath here
			( (clojure.lang.DynamicClassLoader)clojure.lang.Compiler.LOADER.deref() ).addURL( jarURL );
		}
	}

	public BasePlugin() {
		super();
		//constructor
		info("CONSTRUCTOR");//for "+this.getFile().getAbsoluteFile()); these aren't yet set
		//we don't know yet for which plugin we got constructed
		//XXX: an instance is created of this class for every child plugin (including the main one)
		//TODO: maybe add a test to make sure this didn't change in the future
	}

	public static String showClassPath(String prefix, ClassLoader cl){
		_info("=="+prefix+"== For classloader "+cl+" ----------");
		_info(getClassPath(cl));
		_info("=="+prefix+"== ----END---"+cl+" ----------");
		return "";
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

	protected String showResources(ClassLoader cl, String file1) {
		try {
			Enumeration<URL> urls = cl.getResources( file1 );
			System.out.println("all `"+file1+"` Resources: { ");
			while (urls.hasMoreElements()) {
				System.out.println(urls.nextElement());
			}
			System.out.println(" }");
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return "";
	}

	protected int count(Enumeration e) {
		assert null != e:"you passed null, bug somewhere";
		int count=0;
		while(e.hasMoreElements()) {
			count++;
			e.nextElement();
		}
		return count;
	}

	@Override
    public InputStream getResource( String fileName ) {
    	//XXX: when a file within the .jar of current plugin is needed then from within clojure or java either call this method or call
    	//clojure.lang.RT.getResource(this.getClassLoader(), fileName); where this == plugin instance
    	//or else it will get the file from cljminecraft not from memorystone, ie. file name like config.yml which exists in both in same location
    	assert isNotMoreThanOneResource(fileName):"more than 1 file with the same name was detected in classpath,"
				+showClassPath("",getClassLoader())
				+showResources( getClassLoader(), fileName )
				+" PLEASE SEE ABOVE ";

    	return super.getResource( fileName );
    }

	protected boolean isNotMoreThanOneResource(String fileName) {
		try {
			return count(getClassLoader().getResources( fileName )) <= 1;
		} catch ( IOException e ) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public final void severe(String msg) {
		info(ChatColor.RED+"[SEVERE] "+ChatColor.RESET+msg);//because colored won't show [SEVERE] only [INFO] level msgs
	}

    public final void info(String msg) {
    	PluginDescriptionFile descFile = getDescription();
    	String pluginName = this.getClass().getName();
    	if (null != descFile) pluginName=descFile.getName();
    	tellConsole(ChatColor.GREEN+"["+pluginName+"]"+ChatColor.RESET+" "+msg);
    }

    public static final void _info(String msg) {
    	info(BasePlugin.class, msg);
    }

    public static final void info(Class cls, String msg) {
    	String className = cls.getName();//we won't know the difference if we're in main or child plugins (cljminecraft or memorystone) because they both use the same main class to start
    	tellConsole(ChatColor.DARK_AQUA+"["+className+"]"+ChatColor.RESET+" "+msg);//the color is likely never seen due to not inited color console sender
    }

    public final static void tellConsole( String msg ) {
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
