(ns jsm13.system-test
  (:require [clojure.test :refer [deftest is testing]]
            [jsm13.system :as sut])) ; system under test

(deftest start-system-test
  (testing "jsm13.system/start-system starts the system"
    (let [system (sut/start-system [])]
      (is (instance? com.zaxxer.hikari.HikariDataSource (:app/datasource system)))
      (sut/stop-system system))))
