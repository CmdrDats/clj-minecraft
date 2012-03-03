(ns cljminecraft.config
  "Provides a thin wrapper for bukkit config"
  (:use cljminecraft.logging))

(defrecord Config [bukkit-config defaults])

(defn load-config [plugin defaults]
  "Loads the bukkit config file for the given plugin and sets defaults"
  (let [bukkit-config (.getConfig plugin)
        config-defaults (if (nil? defaults) {} defaults)]
    (.addDefaults bukkit-config config-defaults)
    (Config. bukkit-config config-defaults)))

(defn get-keyword [config entry-key accepted-values]
  "Gets the keyword for the specified entry key, or the default if no entry
exists or the entry is not in the set of accepted values. If no such default
exists, returns nil."
  (let [config-entry (keyword (.get (:bukkit-config config) entry-key))]
    (if (contains? accepted-values config-entry)
      config-entry
      (let [default (get (:defaults config) entry-key)]
        (warn (format "Unrecognised repl type: %s, using default %s" config-entry (pr-str keyword)))
        default))))
