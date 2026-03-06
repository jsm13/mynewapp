(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl]
   [integrant.repl.state :as irs]
   [jsm13.system :as system]
   [jsm13.models.plan :as plan-model]
   [next.jdbc.sql :as sql]
   [hiccup2.core :as h]))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))

(comment
  (ig-repl/reset)
  (def db (:app/datasource irs/system))
  (def plan (plan-model/find-by-id db "019cb76a-5d4a-749e-8152-4cd02bd289bc")) 
  )
