(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl]
   [integrant.repl.state :as irs] 
   [jsm13.models.plan :as plan-model]
   [jsm13.views.plan :as plan-views]
   [jsm13.system :as system]))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))

(comment
  (def db (:app/datasource irs/system))
  (def plan
    (plan-model/find-by-id-with-sections db "019cb76a-5d4a-749e-8152-4cd02bd289bc"))
  (plan-views/plan-show-resource plan)
  (ig-repl/reset)
  )
