;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2014-2015, Newcastle University

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see http://www.gnu.org/licenses/.

(ns ncl.mitochondria.protein_test
  (:use [clojure.test])
  (:require
   [ncl.mitochondria.protein :as p]
   [tawny.owl :as o]
   [tawny.reasoner :as r]))

(defn ontology-reasoner-fixture [tests]
  (r/reasoner-factory :hermit)
  (o/ontology-to-namespace p/protein)
  (binding [r/*reasoner-progress-monitor*
            (atom
             r/reasoner-progress-monitor-silent)]
    (tests)))

(use-fixtures :once ontology-reasoner-fixture)

(deftest Basic
  (is (r/consistent?))
  (is (r/coherent?)))
