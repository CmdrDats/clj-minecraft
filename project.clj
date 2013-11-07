(defproject cljminecraft "1.0.6-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure         "1.5.1"]
                 [org.clojure/tools.logging   "0.2.6"]
                 [org.clojure/tools.nrepl     "0.2.3"]
                 [org.bukkit/bukkit           "1.6.4-R2.0"]
                 [clojure-complete            "0.2.3"]
                 [cheshire                    "5.2.0"]
                 [org.reflections/reflections "0.9.8"]]
  :profiles {:dev {:dependencies []}}
  :javac-options [ "-d" "classes/" "-source" "1.6" "-target" "1.6"]
  :java-source-paths ["javasrc"]
  :uberjar-exclusions [#"(org|com|gnu)[/](bukkit|avaje|yaml|getspout|json|trove)[/](.*)"
                       #"com[/]google[/]common[/](.*)"
                       #"org[/]apache[/]commons[/](.*)"
                       #"javax[/]persistence[/](.*)"
                       #"net[/]sf[/]cglib[/](.*)"]
  :repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]
                 ["bukkit.release"   "http://repo.bukkit.org/content/repositories/releases"]]
  )
