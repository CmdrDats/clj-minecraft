# clj-minecraft

clj-minecraft has two specific goals:

1) Open up support for other clojure plugins on Bukkit
2) Provide convenience functions to make writing plugins more
idiomatic to clojure.

The first objective is accomplished within the ClojurePlugin.java
class and the cljminecraft.core namespace. It will take care of
calling the [plugin].core/start function, passing in the Bukkit plugin
object for local plugin state handling.

The second objective is accomplished by the various other namespaces
in clj-minecraft. I'm keeping the structure fairly flat and concise so
that one can write idiomatic code for 80% of the plugin code.

VERY IMPORTANT: Please realize that this plugin is still in early dev,
API changes are fast and frequent so that I can settle on idiomatic
and simple code. I will try to update the changelog with every (major) push so
that you can follow what changes, but be aware that you need to
understand what changes are made and adjust your plugins accordingly.

Changelog:

03 December 2012:
 - Wrote the README, finally.

## Usage

To build this plugin, simply clone and call 'lein uberjar' in the
project root (using lein 2). This will create a *-standalone.jar in the
target folder, which you can copy to the CraftBukkit/plugins folder.

NOTE that currently there is an issue with the uberjarring that
includes the Bukkit API classes - this is not ideal and needs to be fixed.

To create another clojure mod against the clj-minecraft plugin, have a
look at http://github.com/CmdrDats/clj-memorystone for an example.

In a nutshell, create a new ordinary clojure plugin using 'lein new',
then add the following to your project.clj:

```clojure
:dev-dependencies [[org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]
                   [clj-minecraft "1.0.1-SNAPSHOT"]
                   [org.clojure/clojure "1.4.0"]
                   [org.clojure/tools.logging "0.2.3"]]
:repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]]
```

Then create a plugin.yml file and include this content:

```
name: projectname
main: cljminecraft.ClojurePlugin
version: 1.0.0
website: project-url
author: author-name
description: plugin-description
depend: [cljminecraft]
class-loader-of: cljminecraft
```

Note that the depend and class-loader-of are very important.

Once you have done this, create a file /src/[projectname]/core.clj -
your [projectname] has to match exactly what you put in your
plugin.yml. No dashes, underscores of fancynesses.

Add the following to your core.clj:

```clojure
(ns pluginname.core
  (:require [cljminecraft.events :as ev])
  (:require [cljminecraft.player :as pl])
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.logging :as log])
  (:require [cljminecraft.files :as files]))

(defn block-break [ev]
  (pl/send-msg ev "You broke something! oh dear."))
  
(defn events
  []
  [(ev/event block.block-break #'block-break)])
   
(defn start [plugin]
  (ev/register-eventlist plugin (events)))  
```

Then run 'lein jar' from the command line on your project, copy the
target/*.jar into your CraftBukkit/plugins folder, start the server,
join and break something!

At this point, nRepl would have started, by default on the 4005 port -
you can hook into this with whatever editor supports nRepl so that you
can livecode, inspect the world and push new code for your plugin
across. This is a highly recommended way of building your plugins!

## Contributions

A huge thanks to aiscott and basicsensei for their contributions to clj-minecraft!

Please feel free to fork and make pull requests if you want to contribute,
I love code contributions - it makes the whole project that much more well rounded.
On that note, we're desperately needing documentation, so if you're keen to contribute
to the wiki, let me know.

## License

Copyright (C) 2012 Deon Moolman

Distributed under the Eclipse Public License, the same as Clojure.
