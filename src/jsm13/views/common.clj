(ns jsm13.views.common 
  (:require 
   [hiccup.page :as page]
   [hiccup2.core :as h]))

(defn layout
  [body options]
  (h/html 
   (page/doctype :html5) 
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:title (or (:title options) "NPP")]
     [:link {:href "/assets/style.css" :rel "stylesheet"}]
     [:script {:type "module" :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.7/bundles/datastar.js"}]]
    [:body body]]))
