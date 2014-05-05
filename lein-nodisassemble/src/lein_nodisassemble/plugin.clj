(ns lein-nodisassemble.plugin)

(defn get-version
  [project]
  (let [[_ version] (first (filter #(re-matches #".*lein-nodisassemble.*" (str (first %)))
                                   (:plugins project)))]
    version))

(defn middleware [project]
  (let [version (get-version project)]
    (-> project
        (update-in [:dependencies] conj ['nodisassemble version])
        (update-in [:java-agents]  conj ['nodisassemble version]))))
