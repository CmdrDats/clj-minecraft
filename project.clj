(defproject cljminecraft "1.0.4-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.0-RC1"]
                 [org.bukkit/bukkit "1.4.5-R1.0"]
                 [clojure-complete "0.2.2"]
                 [cheshire "2.0.4"]
                 [org.reflections/reflections "0.9.8"]]
  :profiles {:dev {:dependencies []}}
  :javac-options [ "-d" "classes/" "-source" "1.6" "-target" "1.6"]
  :java-source-paths ["javasrc"]
  :uberjar-exclusions [#"(org|com|gnu)[/](bukkit|avaje|yaml|getspout|json|trove)[/](.*)" #"com[/]google[/]common[/](.*)" #"org[/]apache[/]commons[/](.*)" #"javax[/]persistence[/](.*)" #"net[/]sf[/]cglib[/](.*)"]
  :repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]
                 ["bukkit.release" "http://repo.bukkit.org/content/repositories/releases"]]
  )
