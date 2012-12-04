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

(defn on-enable [plugin]
  (cfg/config-defaults plugin)
  (if (cfg/get-boolean plugin "repl.enabled")
    (start-repl (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port"))
    (log/info "Repl options: %s %s %s" (cfg/get-string plugin "repl.host") (cfg/get-int plugin "repl.port") (cfg/get-boolean plugin "repl.enabled")))
  (when-let [resolved (resolve (symbol (str (.getName plugin) ".core/start")))]
    (resolved plugin))
  (log/info "Clojure started - %s" plugin))

(defn on-disable [plugin]
  (when-let [resolved (resolve (symbol (str (.getName plugin) ".core/stop")))]
    (resolved plugin))
  (log/info "Clojure stopped - %s" plugin))


