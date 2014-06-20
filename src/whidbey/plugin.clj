(ns whidbey.plugin
  (:require
    [leiningen.core.project :as project]))


(defn whidbey-profile
  [renderer]
  (when renderer
    `{:dependencies
      [[mvxcvi/puget "0.5.2"]
       [mvxcvi/whidbey "RELEASE"]]

      :repl-options
      {:init [(require 'clojure.tools.nrepl.middleware.render-values '~(symbol (namespace renderer)))]
       :nrepl-middleware [clojure.tools.nrepl.middleware.render-values/render-values]
       :nrepl-context {:interactive-eval {:renderer ~renderer}}}}))


(defn- inject-whidbey
  "Adds :whidbey as a merged default to the given profile. Returns an updated
  profile value."
  [profile]
  (if (vector? profile)
    (if (some #{:whidbey} profile)
      profile
      (vec (cons :whidbey profile)))
    (if profile
      [:whidbey profile]
      [:whidbey])))


(defn middleware
  [project]
  (let [renderer (or (:whidbey-renderer project)
                     'puget.printer/cprint-str)
        profile (whidbey-profile renderer)]
    (-> project
        (project/add-profiles {:whidbey profile})
        (update-in [:profiles :repl] inject-whidbey))))
