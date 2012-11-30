(ns cljminecraft.events
  "Event handlers for bukkit"
  (:require [cljminecraft.logging :as log]))

(defn block-break-event [evt]
      (let [player (.getPlayer evt)
            playerName (.getName player)]
        (.sendMessage player (.concat "Why you break my block, " playerName))))

(def event-listeners (atom {}))

(defn event [name evt]
  ;(log/info (str "Got event " name ": " (bean evt)))
  (doseq [f (get @event-listeners name [])]
    (f evt)))

(defn register-event [name fn]
  (swap! event-listeners update-in [name] conj fn))

(register-event "block-break" #'block-break-event)