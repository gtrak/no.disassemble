(def version (:version (read-string (slurp "../build-meta.edn"))))

(defproject lein-nodisassemble version
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  
  )
