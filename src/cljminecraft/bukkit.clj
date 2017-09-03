(ns cljminecraft.bukkit
  (:require [cljminecraft.util :as util])
  (:import [org.bukkit Bukkit]
           [java.util UUID]))

(defn server []
  (Bukkit/getServer))

(defn plugin-manager []
  (.getPluginManager (server)))

(defn scheduler []
  (.getScheduler (server)))

(defn services-manager []
  (.getServicesManager (server)))

(defn worlds []
  (.getWorlds (server)))

(defn online-players []
  (seq (.getOnlinePlayers (server))))

(defn broadcast [fmt & args]
  (.broadcastMessage (server) (apply format fmt args)))

(defn broadcast-permission [permission fmt & args]
  (.broadcastMessage (server) (apply format fmt args) permission))

(defn world-by-name [name]
  (.getWorld (server) name))

(defn world-by-uuid [uuid]
  (.getWorld (server) (UUID/fromString uuid)))

(defn seconds-to-ticks [s]
  (int (* 20 s)))

(defn ui-sync
  "Execute a given function on the main UI thread"
  [plugin fn]
  (.runTask (scheduler) plugin fn))

(defn delayed-task
  "Execute a given function on the main UI thread after a delay in server ticks (1 tick = 1/20 second), will return a task id you can use to cancel the task - if you specify async?, take care not to directly call any Bukkit API and, by extension, and clj-minecraft functions that use the Bukkit API within this function"
  [plugin fn delay & [async?]]
  (if async?
    (.runTaskAsynchronously (scheduler) plugin fn (long delay))
    (.runTaskLater (scheduler) plugin fn (long delay))))

(defn repeated-task
  "Execute a given function repeatedly on the UI thread, delay and period in server ticks. If you specify async?, take care not to directly call any Bukkit API and, by extension, and clj-minecraft functions that use the Bukkit API within this function"
  [plugin fn delay period & [async?]]
  (if async?
    (.runTaskTimerAsynchronously (scheduler) fn (long delay) (long period))
    (.runTaskTimer (scheduler) fn (long delay) (long period))))

(defn cancel-task
  [task-id]
  (.cancelTask (scheduler) task-id))

(defn running-task?
  [task-id]
  (.isCurrentlyRunning (scheduler) task-id))

(defn queued-task?
  [task-id]
  (.isQueued (scheduler) task-id))