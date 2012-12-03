(ns cljminecraft.config
  "Provides a thin wrapper for bukkit config"
  (:require [cljminecraft.logging :as logging])
  (:require [cljminecraft.util :as util]))

(defn config-defaults 
  "Loads the bukkit config file for the given plugin and sets defaults, returns a configuration object"
  [plugin]
  (.saveDefaultConfig plugin))

(defn defcn [type]
  `(defn ~(symbol (str "get-" (name type))) [~(symbol "plugin") ~(symbol "path")]
     (try
       (~(symbol (str ".get" (util/camelcase (name type)))) (.getConfig ~(symbol "plugin")) ~(symbol "path"))
       (catch Exception ~(symbol "e") nil))))

(defmacro defcns [& types]
  (let [forms (map defcn types)]
    `(do ~@forms)))

(defcns string int boolean double long list string-list integer-list boolean-list double-list float-list long-list byte-list character-list short-list map-list vector offline-player item-stack configuration-section)
