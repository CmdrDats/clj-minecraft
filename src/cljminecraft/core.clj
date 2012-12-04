(ns cljminecraft.core
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.events :as events]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [cljminecraft.files]
            [clojure.tools.nrepl.server :refer (start-server stop-server)]))

(defn start-repl [host port]
  (log/info "Starting repl on host: %s, port %s" host port)
  (start-server :host host :port port))

(defn on-enable
  "to enable self or any child plugins"
  [plugin]
  (cfg/config-defaults plugin)
  (if (cfg/get-boolean plugin "repl.enabled")
    (start-repl (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port"))
    (log/info "Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled")))

  (let [plugin-name (.getName plugin)]
    (when (not (.equals plugin-name "cljminecraft"))
      (binding [*use-context-classloader* true]
        (let [cl (.getContextClassLoader (Thread/currentThread))]
          (.setContextClassLoader (Thread/currentThread) (clojure.lang.DynamicClassLoader. cl))
          )
        (load (str plugin-name "/core"))
        (. plugin info "after load")
        (let [resolved (resolve (symbol (str (.getName plugin) ".core/start")))]
          (if (not resolved)
            (. plugin info "plugin didn't have a start method")
            (do 
              (. plugin info "calling child start")
              (resolved plugin))
            )
          )
        )
      )
    )
  (log/info "Clojure started - %s" plugin))

(defn on-disable
  "to disable self or any child plugins"
  [plugin]
  (when-let [resolved (resolve (symbol (str (.getName plugin) ".core/stop")))]
    (resolved plugin))
  (log/info "Clojure stopped - %s" plugin))


; could add a start and stop methods if wanted, which will be run for cljminecraft only
