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

(defn- has-uncommitted-changes? [dir]
  (let [{:keys [exit out err]} (sh/sh "git" "status" "--porcelain" dir)]
    (if (zero? exit)
      (not (clojure.string/blank? out))
      (throw (ex-info (str "Git status check failed: " err) {:exit exit})))))

(defn- check-no-uncommitted-changes! [dir]
  (when (has-uncommitted-changes? dir)
    (throw (ex-info (str "Cannot build. Uncommitted changes found in " dir "\n"
                         "Please commit or stash your changes first.")
                    {:uncommitted-changes true}))))

(defn uber "Build the uberjar." [opts]
  (check-no-uncommitted-changes! "src")
  (b/delete {:path "target"})
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding JAR..." (:uber-file opts))
    (b/uber opts))
  opts)
