(ns cljminecraft.eventage
    "event handlers for bukkit")

(defn block-break-event [evt]
      (let [player (.getPlayer evt)
            playerName (.getName player)]
        (.sendMessage player (.concat "Why you breaka my block, " playerName))))
