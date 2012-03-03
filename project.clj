(defproject clj-minecraft "1.0.0-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [swank-clojure/swank-clojure "1.3.3"]
                 [org.clojure/tools.nrepl "0.0.5"]
                 ]
  :dev-dependencies [[org.bukkit/bukkit "1.1-R5-SNAPSHOT"]]

  :repl-options [:init nil :caught clj-stacktrace.repl/pst+]
  :javac-options {:destdir "classes/"}
  :java-source-path "javasrc"  
  :repositories {"spout-repo-snap" "http://repo.getspout.org/content/repositories/snapshots/"
                 "spout-repo-rel" "http://repo.getspout.org/content/repositories/releases/"})