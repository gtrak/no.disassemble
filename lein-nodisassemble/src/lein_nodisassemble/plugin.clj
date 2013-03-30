(ns lein-nodisassemble.plugin
  (:require [leiningen.core.classpath :as cp]))

(defn get-version
  [project]
  (let [[_ version] (first (filter #(re-matches #".*lein-nodisassemble.*" (str (first %)))
                                   (:plugins project)))]
    version))

(defn find-dep
  [project]
  (->> (cp/resolve-dependencies :dependencies project)
       (filter #(re-find #".*nodisassemble.*" (.getName %)))
       first))

(defn middleware [project]
  (let [version (get-version project)
        project (update-in project [:dependencies] conj ['nodisassemble version])
        path (-> (find-dep project) .toURI .getPath)]
    (update-in project [:jvm-opts] conj (str "-javaagent:" path))))
