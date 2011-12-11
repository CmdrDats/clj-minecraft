(ns cljminecraft.core
  (:require [clojure.set :as set])
  (:require [swank.swank])
  (:use [clojure.tools.logging]))


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

(defn start-clojure []
  (if (nil? swank*)
    (def swank* (swank.swank/start-repl 4005))))

(defn load-plg [plugin-name desc]
  (eval `(~(symbol (str plugin-name ".core") "enable-plugin") ~desc)))


(defn enable-plugin [test]
  (print test))

(defn onenable [plugin]
  (let [server (.getServer plugin)
        desc (.getDescription plugin)
        name (.getName desc)
        plugindesc {:plugin plugin
                    :server server
                    :manager (.getPluginManager server)
                    :desc desc}]
    (info "CLojure starting" name)
    (if (= name "clj-minecraft")
      (start-clojure)
      (load-plg name plugindesc)
      )
    (info "Clojure Started plugin:" (.getName desc) "," (.getVersion desc))
    )
  
  )

(defn ondisable [plugin]
  (if (= (.getName (.getDescription plugin)) "clj-minecraft")
    nil?
    ;(unload-plugin (.getName (.getDescription plugin)))
    )
  (info "Goodbye"))

