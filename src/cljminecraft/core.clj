(ns cljminecraft.core
  (:require [clojure.set :as set])
  (:require [swank.swank])
  (:use [clojure.tools.logging]))

(declare clj-server*)
(declare clj-plugin*)
(declare clj-plugin-manager*)
(declare clj-plugin-desc*)

(defmacro log-info [str]
  `(info (.getName ~(symbol "*ns*")) ":" ~str))

(defmacro log-warn [str]
  `(warn (.getName ~(symbol "*ns*")) ":" ~str))

(defmacro log-debug [str]
  `(debug (.getName ~(symbol "*ns*")) ":" ~str))

(defmacro auto-proxy
  "Automatically build a proxy, stubbing out useless entries, ala: http://www.brool.com/index.php/snippet-automatic-proxy-creation-in-clojure"
  [interfaces variables & args]
  (let [defined (set (map #(str (first %)) args))
        names (fn [i] (map #(.getName %) (.getMethods i)))
        all-names (into #{} (apply concat (map names (map resolve interfaces))))
        undefined (set/difference all-names defined) 
        auto-gen (map (fn [x] `(~(symbol x) [& ~'args])) undefined)]
    `(proxy ~interfaces ~variables ~@args ~@auto-gen)))

(defmacro map-enums [enumclass]
  `(apply merge (map #(hash-map (keyword (.name %)) %) (~(symbol (apply str (name enumclass) "/values"))))))

(def event-types (map-enums org.bukkit.event.Event$Type))
(def event-priorities (map-enums org.bukkit.event.Event$Priority))

(def plugins (ref {}))

(defonce swank* nil)

(defn broadcast-msg [message]
  (.broadcastMessage clj-server* message))

(defn start-clojure []
  (if (nil? swank*)
    (def swank* (swank.swank/start-repl 4005))))

(defn onenable [plugin]
  (def clj-plugin* plugin)
  (def clj-server* (.getServer plugin))
  (def clj-plugin-manager* (.getPluginManager clj-server* ))
  (def clj-plugin-desc* (.getDescription plugin))
  (start-clojure)
  (log-info "Clojure started")
  )

(defn ondisable [plugin]
  (log-info "Clojure stopped"))

