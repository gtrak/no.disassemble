(ns leiningen.nodisassemble)

(defn middleware [project]
  (update-in
   project [:jvm-opts]
   pr-str ""))

(defn nodisassemble
  "I don't do a lot."
  [project & args]
  (println "Hi!"))
