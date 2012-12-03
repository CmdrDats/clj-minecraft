(ns cljminecraft.events
  "Event handlers for bukkit"
  (:require [cljminecraft.logging :as log]
            [cljminecraft.util :as util]
            [cljminecraft.bukkit :as bk]))

(defonce priorities (util/map-enums org.bukkit.event.EventPriority))

(defn register-event [plugin event-class f & [priority-key]]
  (.registerEvent
   (bk/plugin-manager)
   event-class
   (proxy [org.bukkit.event.Listener] [])
   (get priorities (or priority-key :normal))
   (proxy [org.bukkit.plugin.EventExecutor] []
     (execute [l e] (f e)))
   plugin))

(defonce registered-events
  (atom #{}))

(defn register-eventlist [plugin events]
  (doseq [ev events]
    (when-not (@registered-events ev)
      (swap! registered-events conj ev)
      (register-event plugin (:classname ev) (:event-fn ev) (:priority ev))
      )))

(defmacro event
  "Convenience function for registering events, event-name being prefixed with org.bukkit.event. and camelcased so that you can simply call (onevent block.block-break-event [e] (logging/info (bean e))) to register for the org.bukkit.event.block.BlockBreakEvent and run the body with the BlockBreakEvent as its only argument"
  [event-name fn & [priority]]
  (let [classname (util/package-classname "org.bukkit.event" (str event-name "-event"))]
    `{:classname ~(resolve (symbol classname))
      :event-fn ~fn
      :priority ~priority}))