(ns cljminecraft.core
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.events :as events]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [cljminecraft.files]
            [clojure.tools.nrepl.server :refer (start-server stop-server)])
)

(defn start-repl [host port]
  (log/info "Starting repl on host: %s, port %s" host port)
  (start-server :host host :port port))


(defn start-repl-if-needed [plugin]
  (let [
          repl-enabled (cfg/get-boolean plugin "repl.enabled")
          repl-host (cfg/get-string plugin "repl.host")
          repl-port (cfg/get-int plugin "repl.port")
          ]
    (when repl-enabled 
      (if (util/is-port-in-use repl-port repl-host)
        (log/warn "REPL already started or port %s:%s is in use" repl-host repl-port)
        ;else
        (do 
          (log/info "Repl options: %s %s %s" repl-enabled repl-host repl-port)
          (start-repl repl-host repl-port)
          )
        )
      )
    )
  )

(defn start [plugin] 
  (start-repl-if-needed plugin)
  )


(defn on-enable 
  "to enable self or any child plugins"
  [plugin]
  (cfg/config-defaults plugin)

  (let [plugin-name (.getName plugin)
        resolved (resolve (symbol (str (.getName plugin) ".core/start")))]
    (if (not resolved)
      (log/warn "plugin didn't have a start function")
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
