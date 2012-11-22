(ns cljminecraft.eventage
    "event handlers for bukkit")

(defn block-break-event [evt]
      (let [player (.getPlayer evt)]
        (.sendMessage player "Why you breaka my block?")))
