(ns cljminecraft.files
  (:require [cheshire.core :as json])
  (:require [clojure.java.io :as io]))

(defn data-folder [{:keys [server]}]
  (.getDataFolder server))

(defn read-json-file [{:keys [server] :as env} filename]
  (try
    (json/decode (slurp (io/file (data-folder env) filename)))
    (catch Exception e (.printStackTrace e) {})))

(defn write-json-file [{:keys [server] :as env} filename data]
  (let [file (io/file (data-folder) filename)]
    (io/make-parents file)
    (spit file (json/encode data))))