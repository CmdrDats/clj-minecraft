(ns cljminecraft.logging
  (:import [org.bukkit Bukkit ChatColor]))

(defn logsend [str]
  (if-let [sender (Bukkit/getConsoleSender)]
    (.sendMessage sender str)
    (println str)))

(defmacro info [fmt & args]
  `(logsend (format ~(str ChatColor/GREEN (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro warn [fmt & args]
  `(logsend (format ~(str ChatColor/YELLOW (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro debug [fmt & args]
  `(logsend (format ~(str ChatColor/RED (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))

(defmacro bug
  "as in bug in code/coding when this is reached"
  [fmt & args]
  `(logsend (Bukkit/getConsoleSender) (format ~(str "[BUG]" ChatColor/RED (.getName *ns*) ChatColor/RESET ":" ChatColor/BLUE (:line (meta &form)) ChatColor/RESET " - " fmt) ~@args)))
