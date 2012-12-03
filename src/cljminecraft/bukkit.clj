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
  (.getOnlinePlayers (server)))

(defn broadcast [fmt & args]
  (.broadcaseMessage (server) (apply format fmt args)))

(defn broadcast-permission [permission fmt & args]
  (.broadcaseMessage (server) (apply format fmt args) permission))

(defn world-by-name [name]
  (.getWorld (server) name))

(defn world-by-uuid [uuid]
  (.getWorld (server) (UUID/fromString uuid)))




