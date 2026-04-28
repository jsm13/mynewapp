(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]
            [clojure.java.shell :as sh]))

(def lib 'net.clojars.jsm13/mynewapp)
(def base-version "0.0.1")
(def main 'jsm13.mynewapp)
(def class-dir "target/classes")

(defn- git-commit-hash []
  (-> (sh/sh "git" "rev-parse" "--short" "HEAD")
      :out
      (clojure.string/trim)))

(defn- uber-opts [opts]
  (let [git-hash (git-commit-hash)
        version (str base-version "-" git-hash)]
  (assoc opts
         :lib lib :main main
         :uber-file (format "target/%s-%s.jar" lib version)
         :basis (b/create-basis {})
         :class-dir class-dir
         :src-dirs ["src"]
         :ns-compile [main])))

(defn uber "Build the uberjar." [opts]
  (b/delete {:path "target"})
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding JAR..." (:uber-file opts))
    (b/uber opts))
  opts)
