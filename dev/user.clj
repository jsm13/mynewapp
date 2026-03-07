(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl]
   [integrant.repl.state :as irs] 
   [jsm13.models.plan :as plan-model]
   [jsm13.models.section :as section-model]
   [jsm13.views.plan :as plan-views]
   [jsm13.system :as system]
   [next.jdbc.sql :as sql]))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))

(comment
  (def db (:app/datasource irs/system))
  (def developing-plan-id "019cb76a-5d4a-749e-8152-4cd02bd289bc")
  (def plan
    (plan-model/find-by-id-with-sections db developing-plan-id))
  (section-model/create-plan-section db developing-plan-id "Tidy up step")
  (section-model/delete db "019cc506-00e9-729a-b1fc-77122c1a8938")
  plan
  (ig-repl/reset)
  )
