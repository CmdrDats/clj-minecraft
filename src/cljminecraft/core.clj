(ns cljminecraft.core
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.events :as events]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [cljminecraft.files]
            [clojure.tools.nrepl.server :refer (start-server stop-server)])
)

(def repl-handle (atom nil))

(defn start-repl [host port]
  (log/info "Starting repl on host: %s, port %s" host port)
  (if (compare-and-set! repl-handle nil (start-server :host host :port port))
    (log/info "Started repl on host: %s, port %s" host port)
    ;else
    ;I guess we don't allow multiple running REPLs this way, do we want more than 1 ie. on different host/port?
    (log/bug "you tried to start a(nother) repl while one was already started")
    )
  )

(defn stop-repl
  []
  (if @repl-handle
    (try
      (do 
        (stop-server @repl-handle)
        (log/info "REPL stopped")
        )
      (finally 
        (reset! repl-handle nil)
        )
      )
    ;else
    (log/bug "you tried to stop REPL when it was not running")
    )
  )


(defn start-repl-if-needed [plugin]
  (let [
          repl-enabled (cfg/get-boolean plugin "repl.enabled")
          repl-host (cfg/get-string plugin "repl.host")
          repl-port (cfg/get-int plugin "repl.port")
          ]
    (when repl-enabled 
      (if (util/port-in-use? repl-port repl-host)
        (log/warn "REPL already started or port %s:%s is in use" repl-host repl-port)
        ;else
        (do 
          (log/info "Repl options: %s %s %s" repl-enabled repl-host repl-port)
          (start-repl repl-host repl-port)
          )))))

(defonce clj-plugin (atom nil))

(defn start 
  "onEnable cljminecraft"
  [plugin]
  (reset! clj-plugin plugin)
  (start-repl-if-needed plugin))

(defn stop
  "onDisable cljminecraft"
  [plugin]
  (stop-repl))


(defn on-enable 
  "to enable self or any child plugins"
  [plugin]
  (cfg/config-defaults plugin)

  (let [plugin-name (.getName plugin)
        resolved (resolve (symbol (str (.getName plugin) ".core/start")))]
    (if (not resolved)
      (log/warn "plugin %s didn't have a start function" plugin-name)
      (do 
        ;the following line is for debugging purposes only, to be removed:
        (log/info "second Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled"))
        (log/info "calling child `start` for %s" plugin-name)
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


