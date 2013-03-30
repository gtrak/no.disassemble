(ns lein-nodisassemble.plugin
  (:require [leiningen.core.classpath :as cp]))

(defn middleware [project]
  (let [project (update-in project [:dependencies] conj '[nodisassemble "0.1.0-SNAPSHOT"])
        deps (cp/resolve-dependencies :dependencies project)
        depfile (first (filter #(re-find #".*nodisassemble.*" (.getName %)) deps))
        path (-> depfile .toURI .getPath)]
    (update-in project [:jvm-opts] conj (str "-javaagent:" path)
               )))
