(defproject cljminecraft "1.0.6-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies
  [[org.clojure/clojure         "1.10.0"]
   [org.spigotmc/spigot-api "1.14.4-R0.1-SNAPSHOT"]
   [org.clojure/tools.logging   "0.5.0"]
   [nrepl     "0.7.0-alpha3"]
   [cheshire                    "5.9.0"]   
   [org.reflections/reflections "0.9.10"]]
  
  :javac-options ["-d" "classes/" "-source" "1.8" "-target" "1.8"]
  :java-source-paths ["javasrc"]
  :uberjar-exclusions
  [#"(org|com|gnu)[/](bukkit|avaje|yaml|getspout|json|trove)[/](.*)"
   #"com[/]google[/]common[/](.*)"
   #"org[/]apache[/]commons[/](.*)"
   #"javax[/]persistence[/](.*)"
   #"net[/]sf[/]cglib[/](.*)"]
  
  :repositories
  [["bukkit.snapshots" "https://hub.spigotmc.org/nexus/content/repositories/snapshots"]
   ["bukkit.release"   "https://hub.spigotmc.org/nexus/content/repositories/releases"]])