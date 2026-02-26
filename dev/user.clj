(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl]
   [jsm13.system :as system]))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))


(comment
  (ig-repl/reset)
  
  )
