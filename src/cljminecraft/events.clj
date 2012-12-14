(ns cljminecraft.events
  "Event handlers for bukkit"
  (:require [cljminecraft.logging :as log]
            [cljminecraft.util :as util]
            [cljminecraft.bukkit :as bk]))

(defonce priorities (util/map-enums org.bukkit.event.EventPriority))

(defn register-event [plugin eventname f & [priority-key]]
  (let [eventclass (resolve (symbol (util/package-classname "org.bukkit.event" (str eventname "-event"))))]
    (log/info "Registering event %s for plugin %s" eventname (.getName plugin))
    (.registerEvent
     (bk/plugin-manager)
     eventclass
     (proxy [org.bukkit.event.Listener] [])
     (get priorities (or priority-key :normal))
     (proxy [org.bukkit.plugin.EventExecutor] []
       (execute [l e] (f e)))
     plugin)))

(defonce registered-events
  (atom #{}))

(defn register-eventlist [plugin events]
  (doseq [ev events]
    (when-not (@registered-events ev)
      (swap! registered-events conj ev)
      (register-event plugin (:eventname ev) (:event-fn ev) (:priority ev))
      )))

(defn event
  "Convenience function for registering events, eventname being prefixed with org.bukkit.event. 
and camelcased so that you can simply call (onevent block.block-break-event [e] (logging/info (bean e))) 
to register for the org.bukkit.event.block.BlockBreakEvent and run the body with the BlockBreakEvent as its only 
argument"
  [eventname fn & [priority]]
  {:eventname eventname
    :event-fn fn
    :priority priority})

(defn find-event [name]
  (let [classes (util/find-subclasses "org.bukkit" org.bukkit.event.Event)
        names (map #(.replaceAll
                     (.replaceAll (util/class-named %) "org.bukkit.event." "")
                     "-event$" "") classes)]
    (filter #(.contains % (.toLowerCase name)) names)))

(def boring-methods #{"getHandlers" "getHandlerList" "wait" "equals" "toString" "hashCode" "getClass" "notify" "notifyAll" "isAsynchronous"})

(defn describe-event [eventname]
  (let [classname (util/package-classname "org.bukkit.event" (str eventname "-event"))
        cl (resolve (symbol classname))]
    (set
     (filter #(not (contains? boring-methods %))
             (map #(:name (bean %))  (seq (:methods (bean cl))))))))
