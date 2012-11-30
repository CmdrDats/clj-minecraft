(ns cljminecraft.events
  "Event handlers for bukkit"
  (:require [cljminecraft.logging :as log]
            [cljminecraft.util :as util]))

(defonce priorities (util/map-enums org.bukkit.event.EventPriority))

(defn block-break-event [evt]
      (let [player (.getPlayer evt)
            playerName (.getName player)]
        (.sendMessage player (.concat "Why you break my block, " playerName))))


(defn register-event [{:keys [plugin-manager plugin] :as server} event-class f & [priority-key]]
  (.registerEvent
   plugin-manager
   event-class
   (proxy [org.bukkit.event.Listener] [])
   (get priorities (or priority-key :normal))
   (proxy [org.bukkit.plugin.EventExecutor] []
     (execute [l e] (f e)))
   plugin))

(defn register-eventlist [server events]
  (doseq [ev events]
    (register-event server (:classname ev) (:event-fn ev) (:priority ev))))

(defmacro event
  "Convenience function for registering events, event-name being prefixed with org.bukkit.event. and camelcased so that you can simply call (onevent block.block-break-event [e] (logging/info (bean e))) to register for the org.bukkit.event.block.BlockBreakEvent and run the body with the BlockBreakEvent as its only argument"
  [event-name fn & [priority]]
  (let [classname (util/package-classname "org.bukkit.event" (str event-name))]
    `{:classname ~(resolve (symbol classname))
      :event-fn ~fn
      :priority ~priority}))

(defn events []
  [(event block.block-break-event #'block-break-event)])