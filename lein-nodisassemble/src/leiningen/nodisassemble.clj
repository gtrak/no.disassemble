(ns leiningen.nodisassemble
  (:require [leiningen.util.deps :as deps]))


(defn middleware [project]
  (let [project (update-in project [:dependencies] conj '[nodisassemble "0.1.0-SNAPSHOT"])
        file (deps/dependency-hierarchy project)]
    (println file)
    (update-in project [:jvm-opts] identity)))

(defn nodisassemble
  "I don't do a lot."
  [project & args]
  (println "Hi!"))
