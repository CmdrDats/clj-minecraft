(ns cljminecraft.core
  (:require [clojure.set :as set]
            [swank.swank]
            [clojure.tools.nrepl])
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

(def plugins (ref {}))

(def repl-type (ref nil))

(def repl-server (agent nil))

(defn broadcast-msg [message]
  (.broadcastMessage clj-server* message))

(defn start-clojure
  ([] (start-clojure :swank))
  ([new-repl-type]
    (dosync
      (when (nil? @repl-type)
        (ref-set repl-type new-repl-type)
        (send-off repl-server
                  (fn [_] (case new-repl-type
                            :nrepl
                            (let [nrepl-port 4006]
                              (info (format "Starting nRepl server on port %d" nrepl-port))
                              (clojure.tools.nrepl/start-server nrepl-port)
                              )
                            ; Default to swank
                            (let [swank-port 4005]
                              ; Swank server provides its own log notification
                              (swank.swank/start-repl 4005)))))))))

(defn onenable [plugin]
  (def clj-plugin* plugin)
  (def clj-server* (.getServer plugin))
  (def clj-plugin-manager* (.getPluginManager clj-server* ))
  (def clj-plugin-desc* (.getDescription plugin))
  (let [file (java.io.File. (.getDataFolder plugin) "config.yml")]
    (if (.exists file)
      (start-clojure :nrepl)
      (start-clojure)))
  (log-info "Clojure started")
  )

(defn ondisable [plugin]
  (log-info "Clojure stopped"))

