(ns cljminecraft.util
  (:require [clojure.set :as set])
  (:require [cljminecraft.logging :as log])
  (:import (java.net ServerSocket InetSocketAddress))
  (:import (java.io IOException))
  )

(defmacro map-enums [enumclass]
  `(apply merge (map #(hash-map (keyword (.toLowerCase (.name %))) %) (~(symbol (apply str (name enumclass) "/values"))))))

(defmacro auto-proxy
  "Automatically build a proxy, stubbing out useless entries, ala: http://www.brool.com/index.php/snippet-automatic-proxy-creation-in-clojure"
  [interfaces variables & args]
  (let [defined (set (map #(str (first %)) args))
        names (fn [i] (map #(.getName %) (.getMethods i)))
        all-names (into #{} (apply concat (map names (map resolve interfaces))))
        undefined (set/difference all-names defined) 
        auto-gen (map (fn [x] `(~(symbol x) [& ~'args])) undefined)]
    `(proxy ~interfaces ~variables ~@args ~@auto-gen)))

(defn capitalize [s]
  (if (> (count s) 0)
    (str (Character/toUpperCase (.charAt s 0))
         (.toLowerCase (subs s 1)))
    s))

(defn capitalize-all [s]
  (let [matcher (re-matcher #"(\w+)" s)
        buffer (new StringBuffer)]
    (while (.find matcher)
      (.appendReplacement matcher buffer (capitalize (.group matcher 1))))
    (.appendTail matcher buffer)
    (.toString buffer)))

(defn camelcase
  "Removes dashes, camelcases words and then removes spaces."
  [str]
  (.replaceAll (capitalize-all str) "-" ""))

(defn uncamelcase
  "Add dashes and lowercase everything"
  [str]
  (.substring (.toLowerCase (.replaceAll str "([A-Z])" "-$1")) 1))

(defn glue [sep & strs]
  (apply str (interpose sep (filter #(and (not (nil? %)) (> (.length (.trim (str %))) 0)) strs))))

(defmacro let-info
  "A let replacement that logs the bindings at each step. Useful for debugging."
  [bindings & body]
  (let [split (partition 2 bindings)
        info-bindings (map (fn [[n _]] `(~(symbol "_") (log/info ~(str (if (instance? clojure.lang.Named n) (name n) (str n)) ":" ) ~n))) split)]
    `(let [~@(apply concat (interleave split info-bindings))]
       ~@body)))

(defn package-classname [base-package nm]
  (let [split (seq (.split nm "[.]"))
        classname (camelcase (last split))
        package (apply glue "." base-package (pop (vec split)))]
    (glue "." package classname)))

(defn class-named [class]
  (let [split (seq (.split (.getName class) "[.]"))
        classname (uncamelcase (last split))
        package (apply glue "." (pop (vec split)))]
    (glue "." package classname)))


(defn port-in-use? [port bind]
  (let [bind-addr (if (InetSocketAddress. bind port) (InetSocketAddress. port))]
    (try
      (with-open [ss (ServerSocket. port 0 (.getAddress bind-addr))] false)
      (catch IOException e true))))

(defn throw-runtime [fmt & args]
  (throw (java.lang.RuntimeException. (apply format fmt args))))

(defn find-subclasses [package-name class]
  (filter #(not (nil? %))
          (seq (.getSubTypesOf (org.reflections.Reflections.
                                package-name
                                (into-array org.reflections.scanners.Scanner []))
                               class))))

