(ns cljminecraft.zigzag)

(defn test2 [pluginInstance a b c]
  (. pluginInstance info (str a b c))
  )