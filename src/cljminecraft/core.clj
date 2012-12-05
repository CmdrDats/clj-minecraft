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

(defn start [plugin] 
  (when (cfg/get-boolean plugin "repl.enabled")
    (log/info "Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled"))
    (start-repl (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port"))));TODO: this gets rerun on server `reload` at least and fail to rebind

(defn on-enable 
  "to enable self or any child plugins"
  [plugin]
  (cfg/config-defaults plugin)

  (let [plugin-name (.getName plugin)
        resolved (resolve (symbol (str (.getName plugin) ".core/start")))]
    (if (not resolved)
      (log/info "plugin didn't have a start method");TODO: this should throw right?
      (do 
        ;the following line is for debugging purposes only, to be removed:
        (log/info "second Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled"))
        (log/info "calling child start")
        (resolved plugin))
      )
    )
  (log/info "Clojure started - %s" plugin)
  )

(defn on-disable
  "to disable self or any child plugins"
  [plugin]
  (when-let [resolved (resolve (symbol (str (.getName plugin) ".core/stop")))]
    (resolved plugin))
  (log/info "Clojure stopped - %s" plugin)
  ;the following line is for debugging purposes only, to be removed:
  (log/info "third Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled"))
  )


; could add a stop method if wanted, which will be run for cljminecraft only
