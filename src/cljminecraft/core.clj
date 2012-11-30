(ns cljminecraft.core
  (:require [clojure.set :as set]
            [cljminecraft.state :as state]
            [cljminecraft.events :as events]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [clojure.tools.nrepl.server :refer (start-server stop-server)]))



(defonce plugins (ref {}))

(defonce repl-types* #{:nrepl :swank})

(defonce repl-type (ref nil))

(defonce repl-server (agent nil))


(defn broadcast-msg [message]
  (.broadcastMessage (:server state/server) message))


(defn start-clojure [new-repl-type]
    (dosync
      (when (nil? @repl-type)
        (ref-set repl-type new-repl-type)
        (send-off repl-server
                  (fn [_] (case new-repl-type
                            :nrepl
                            (let [nrepl-port 4006]
                              (log/info (format "Starting nRepl server on port %d" nrepl-port))
                              (start-server :port nrepl-port))))))))

(defn events []
  (events/events))

(defn on-enable [plugin]
  (state/register-server (state/get-server plugin))
  (let [repl-key "repl"
        config (cfg/load-config plugin {repl-key :nrepl})]
    (start-clojure (cfg/get-keyword config repl-key repl-types*)))
  (log/info "Clojure started")
  (events/register-eventlist @state/server (events)))

(defn on-disable [plugin]
  (log/info "Clojure stopped"))


