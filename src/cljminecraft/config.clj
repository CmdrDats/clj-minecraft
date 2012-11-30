(ns cljminecraft.config
  "Provides a thin wrapper for bukkit config"
  (:require [cljminecraft.logging :as logging]))

(defn load-config 
  "Loads the bukkit config file for the given plugin and sets defaults"
  [plugin defaults]
  (let [bukkit-config (.getConfig plugin)
        config-defaults (if (nil? defaults) {} defaults)]
    (.addDefaults bukkit-config config-defaults)
    {:bukkit-config bukkit-config :defaults config-defaults}))

(defn get-keyword 
  "Gets the keyword for the specified entry key, or the default if no entry
exists or the entry is not in the set of accepted values. If no such default
exists, returns nil."
  [config entry-key accepted-values]
  (let [config-entry (keyword (.get (:bukkit-config config) entry-key))]
    (if (contains? accepted-values config-entry)
      config-entry
      (let [default (get (:defaults config) entry-key)]
        (logging/warn (format "Unrecognised repl type: %s, using default %s" config-entry (pr-str keyword)))
        default))))
