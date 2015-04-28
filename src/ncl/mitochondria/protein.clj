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
;; along with this program. If not, see http://www.gnu.org/licenses/.

(ns ^{:doc "TODO"
      :author "Jennifer Warrender"}
  ncl.mitochondria.protein
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology protein
  :iri (str g/tm-iri "protein")
  :prefix "pro:")

;; OWL CLASSES
(defclass Protein)
;; (defclass Protein_related
;;   :subclass Protein)

;; PATTERNS
(defn protein-class [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Protein))

;; (defn create-protein-related [o name]
;;   (owl-class o
;;              (g/make-safe name)
;;              :label name
;;              :subclass Protein_related))

;; MAIN

;; read file
(let [input (map clojure.string/lower-case
                    (g/get-lines
                     (g/get-resource "./input/protein.txt")))
      proteins (map #(clojure.string/replace % "_human" "") input)]

  ;; generate protein classes
  (doseq [p proteins]
    (protein-class p))

  ;; Auxiliary functions
  (defn protein? [term]
    (some #(= % term) proteins))
  ;; ;; also need to look out for related without _human
  ;; (defn protein-related? [term]
  ;;   (some #(re-find (re-pattern %) term) proteins))
)

;; tests
;; (println (protein? "srac1"))
;; (println (protein? "srac1_human"))
