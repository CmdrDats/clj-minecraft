(ns cljminecraft.logging
  (:import [org.bukkit Bukkit ChatColor]))

(defmacro info [fmt & args]
  `(.sendMessage (Bukkit/getConsoleSender) (format ~(str ChatColor/GREEN (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro warn [fmt & args]
  `(.sendMessage (Bukkit/getConsoleSender) (format ~(str ChatColor/YELLOW (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro debug [fmt & args]
  `(.sendMessage (Bukkit/getConsoleSender) (format ~(str ChatColor/RED (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro bug
  "as in bug in code/coding when this is reached"
  [fmt & args]
  `(.sendMessage (Bukkit/getConsoleSender) (format ~(str "[BUG]" ChatColor/RED (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))
