;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2014, Newcastle University

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program. If not, see http://www.gnu.org/licenses/.

(ns ^{:doc "TODO"
      :author "Jennifer Warrender"}
  ncl.mitochondria.cq
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             [generic :as g]
             [paper :as ppr]
             [hanatomy :as h]
             [mitochondria :as mit]
             [manatomy :as man]
             [disease :as d]
             [gene :as gne]
             [protein :as pro]
             ;; [mutation :as mut]
             [term :as t]
             ]))

;; new ontology
(defontology cq
  :iri "http://ncl.ac.uk/mitochondria/cq"
  :prefix "cq:")

;; import other ontologies
(owl-import ppr/paper)
(owl-import h/hanatomy)
(owl-import mit/mitochondria)
(owl-import man/manatomy)
(owl-import d/disease)
(owl-import gne/gene)
(owl-import pro/protein)
;; (owl-import mut/mutation)

;; OWL CLASSES
(defclass Competency_Question)

;; PATTERNS
(defn create-cq
  [name cquestion]
  (owl-class cq
             name
             :label cquestion
             :subclass Competency_Question))

;; MAIN
(defn driver
  []

  ;; read files
  (let [refined (into [] (g/get-lines
                          "./output/cqs/refined_cq.txt"))
        quarantined (into [] (g/get-lines
                              "./output/cqs/quarantined_cq.txt"))
        cqs (merge refined quarantined)
        ids (into [] (range 1 (+ (count cqs) 1)))
        ]

    (println (str "There are " (count cqs) " competency questions."))

    (doseq [i (range 0 (count cqs))]
      (let [name (str "cq" (get ids i))
            cquestion (get cqs i)]
        (create-cq name cquestion)
        ))

    ))
