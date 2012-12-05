(defproject cljminecraft "1.0.1-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.0-RC1"]
                 [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]
                 [clojure-complete "0.2.2"]
                 [cheshire "2.0.4"]]
  
  :javac-options [ "-d" "classes/" "-source" "1.6" "-target" "1.6"]
  :java-source-paths ["javasrc"]

  :repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]]
  )
