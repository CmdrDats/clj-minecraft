(defproject clj-minecraft "1.0.0-SNAPSHOT"
  :description "Clojure for Bukkit Minecraft"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]
                 [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]]

  :javac-options [ "-d" "classes/"]
  :java-source-paths ["javasrc"]

  :repositories [["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]]
  )
