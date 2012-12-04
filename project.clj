(defproject clj-minecraft "1.0.1-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure "1.5.0-beta1"];TODO: go back to 1.4.0
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.0-RC1"]
;                 [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT" :classifier "sources"] epic fail trying to get some javadoc in eclipse
                 [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]
                 [clojure-complete "0.2.2"]
                 [cheshire "2.0.4"]
                 ]
  
  :javac-options [ "-d" "classes/" "-source" "1.6" "-target" "1.6"]
  :java-source-paths ["javasrc"]

  ;:jvm-opts ["-enableassertions"] useless here, should be when starting bukkit instead
  
  :warn-on-reflection true
  
  :repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]]
  )
