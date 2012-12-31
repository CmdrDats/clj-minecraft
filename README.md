# cljminecraft

cljminecraft has two specific goals:

**Open up support for other clojure plugins on Bukkit**
> The first objective is accomplished within the ClojurePlugin.java
> class and the cljminecraft.core namespace. It will take care of
> calling the [plugin].core/start function, passing in the Bukkit plugin
> object for local plugin state handling.

**Provide convenience functions to make writing plugins more idiomatic to clojure**
> The second objective is accomplished by the various other namespaces
> in clj-minecraft. I'm keeping the structure fairly flat and concise so
> that one can write idiomatic code for 80% of the plugin code.

## Usage

### Install the mod

Download the latest server mod build from http://dev.bukkit.org/server-mods/cljminecraft/files/ or build it yourself by cloning the repo and `lein uberjar`

Copy the jar into the Bukkit plugin folder and fire up your server. Once started, it should have opened a REPL port, by default on port 4005

If all you wanted was to enable clojure plugins on your server, you're done. If you want to write some code, read on.

### Write some code

From now on, you will need leiningen installed - see https://github.com/technomancy/leiningen

```
lein repl :connect 4005
```

Now you have an active connection to push code onto the server, let's try some stuff (the part before => is the REPL prompt...):

```clojure
user=> (in-ns 'cljminecraft.core)
#<Namespace cljminecraft.core>

cljminecraft.core=> (ev/find-event "break")
("painting.painting-break-by-entity" "hanging.hanging-break" "painting.painting-break" "entity.entity-break-door" "hanging.hanging-break-by-entity" "player.player-item-break" "block.block-break")

;; block.block-break looks good.. lets see what we can get out of it
cljminecraft.core=> (ev/describe-event "block.block-break")
#{"setExpToDrop" "isCancelled" "getEventName" "setCancelled" "getExpToDrop" "getPlayer" "getBlock"}

;; Cool, getBlock looks like I can use it..
cljminecraft.core=> (defn mybreakfn [ev] {:msg (format "You broke a %s" (.getBlock ev))})
#'cljminecraft.core/mybreakfn

cljminecraft.core=> (ev/register-event @clj-plugin "block.block-break" #'mybreakfn)
nil

;; Test breaking a block, I get a crazy message, let's make that more sane
cljminecraft.core=> (defn mybreakfn [ev] {:msg (format "You broke a %s" (.getType (.getBlock ev)))})
#'cljminecraft.core/mybreakfn
```

And that's a quick taste of interactive development.. The Rabbit hole goes rather deep :) enjoy.

### Roll your own mod

Once you've got the cljminecraft mod installed on bukkit and you've played a bit with the REPL, you might want to build your own fully capable plugin:

```
lein new cljminecraft yourplugin
```

This will create a subfolder called yourplugin with the basics needed to get started, with some sample configuration in src/config.yml and the plugin.yml already setup under src/plugin.yml, ready to roll.

```
cd yourplugin
lein jar
cp target/*.jar /path/to/bukkit/plugins/
```

Start up your Bukkit server and go, by default, you'll see a message when you place a sign and there will be a command '/yourplugin.random' which does a dice roll. Very exciting stuff!

Remember to update your details in the plugin.yml and README.md files - and very importantly, commit to github to share with the world.

Also, be sure to look at the wiki for more in-depth instructions on all the moving parts: http://github.com/CmdrDats/clj-minecraft/wiki

Time to hack away.

## Changelog:

30 December 2012:
 - Implement a simple permissions extension for checking and setting specific permissions on players
   - This does not yet persist given player permissions across sessions
 - Added clj.permission as a command to give a player a specific permission
   - Autocompletion may need work as command lists within commands are untested.
 
29 December 2012:
 - Now sends a message to the player triggering an event when the event function returns {:msg "..."}
 - Fix up line function
 - Add `actions` enum to events.clj
 - Add `is-block` convenience function to items.clj for checking if a block is a certain material
 
27 December 2012:
 - Introduce cut-to-mark, copy-to-mark, pen-from-mark and clear-mark
 - Interface of line and empty line-to-mark - have to work out the math involved
 - Thinking about how to define a brush instead of just 'material' and 'painting?' in the context.
 
21 December 2012:
 - Revamp the block action definitions by introducing a 'defaction' macro
 - Introduce cut, copy, paste and fork - still buggy though, especially for larger areas

19 December 2012:
 - get-material now always returns MaterialData and never Material for consistency
   - Note that this could lead to API breaks
 - Implement the first working version of the block drawing primitives
   - Similar idea to Logo, forward x, turn-right, forward x, extrude :up x
 - Playing with more things in the repl.clj scratchpatch
   - Noticed that you can send a block change to a player without changing the world.. I'll look into building the functionality to setup a 'virtual' paint mode in the block drawing. Either for all players, a list of players or a single player. It would need to track the virtual paint unless cleared so that it can resend it to players as required.
   
## Contributions

A huge thanks to aiscott and basicsensei for their contributions to clj-minecraft!

Please feel free to fork and make pull requests if you want to contribute,
I love code contributions - it makes the whole project that much more well rounded.
On that note, we're desperately needing documentation, so if you're keen to contribute
to the wiki, let me know.

## License

Copyright (C) 2012 Deon Moolman

Distributed under the Eclipse Public License, the same as Clojure.
