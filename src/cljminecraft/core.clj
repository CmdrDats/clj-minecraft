(ns cljminecraft.core
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.blocks :as blocks]
            [cljminecraft.events :as ev]
            [cljminecraft.entity :as ent]
            [cljminecraft.player :as plr]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [cljminecraft.commands :as cmd]
            [cljminecraft.recipes :as r]
            [cljminecraft.items :as i]
            [cljminecraft.files]
            [clojure.tools.nrepl.server :refer (start-server stop-server)]))

(def repl-handle (atom nil))

(defn start-repl [host port]
  (log/info "Starting repl on host: %s, port %s" host port)
  (cond
   @repl-handle
   {:msg "you tried to start a(nother) repl while one was already started"}
   (util/port-in-use? port host)
   {:msg (format "REPL already started or port %s:%s is in use" host port)}
   :else
   (do
     (reset! repl-handle (start-server :host host :port port))
     {:msg (format "Started repl on host: %s, port %s" host port)})))

(defn stop-repl
  []
  (cond
   (nil? @repl-handle) {:msg "you tried to stop REPL when it was not running"}
   :else
   (try
     (do
       (stop-server @repl-handle)
       {:msg "REPL stopped"})
     (finally
      (reset! repl-handle nil)))))

(defn start-repl-if-needed [plugin]
  (let [repl-host (cfg/get-string plugin "repl.host")
        repl-port (cfg/get-int plugin "repl.port")]
    (cond
     (not (cfg/get-boolean plugin "repl.enabled"))
     (log/info "REPL Disabled")
     :else
     (let [{:keys [msg] :as response} (start-repl repl-host repl-port)]
       (log/info "Repl options: %s %s" repl-host repl-port)
       (if msg (log/info msg))))))

(defonce clj-plugin (atom nil))

(defn repl-command [sender cmd & [port]]
  (log/info "Running repl command with %s %s" cmd port)
  (case cmd
    :start (start-repl "0.0.0.0" (or port (cfg/get-int @clj-plugin "repl.port")))
    :stop (stop-repl)))

(defn tabtest-command [sender & args]
  (.sendMessage sender (apply str args)))

(defn tabcomplete-reverse-first [sender command alias args]
  [(apply str (reverse (first args)))])

(defn addevent-command [sender eventname message]
  (ev/register-event @clj-plugin eventname (fn [ev] (.sendMessage sender (str message ": " ev))))
  {:msg (format "Adding event %s with message %s" eventname message)})

(defn spawn-command [sender entity]
  (ent/spawn-entity (.getLocation sender) entity)
  (log/info "Spawning %s in front of %s" entity (.getName sender)))

;; cljminecraft basic permission system
(defn permission-command [sender player permission allow-type]
  (plr/set-permission player permission allow-type))

(defn player-permission-attach [ev]
  (plr/permission-attach-player! @clj-plugin ev))

(defn player-permission-detach [ev]
  (plr/permission-detach-player! ev))

(defn setup-permission-system
  [plugin]
  (ev/register-eventlist
   plugin
   [(ev/event "player.player-join" #'player-permission-attach)
    (ev/event "player.player-quit" #'player-permission-detach)
    (ev/event "player.player-kick" #'player-permission-detach)])
  (plr/permission-attach-all! plugin))

(defn disable-permission-system
  []
  (plr/permission-detach-all!))

;; cljminecraft specific setup
(defn start
  "onEnable cljminecraft"
  [plugin]
  (reset! clj-plugin plugin)
  (cmd/register-command @clj-plugin "clj.repl" #'repl-command [:keyword [:start :stop]] [:int [(cfg/get-int plugin "repl.port")]])
  (cmd/register-command @clj-plugin "clj.tabtest" #'tabtest-command :player :material [:keyword [:start :stop]] [:string #'tabcomplete-reverse-first])
  (cmd/register-command @clj-plugin "clj.addevent" #'addevent-command :event :string)
  (cmd/register-command @clj-plugin "clj.spawnentity" #'spawn-command :entity)
  (cmd/register-command @clj-plugin "clj.permission" #'permission-command :player :permission [:keyword [:allow :disallow :release]])
  (setup-permission-system plugin)
  (start-repl-if-needed plugin))

(defn stop
  "onDisable cljminecraft"
  [plugin]
  (stop-repl)
  (disable-permission-system))

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
