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
  ncl.mitochondria.gene
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology gene
  :iri (str g/tm-iri "gene")
  :prefix "gne:")

;; OWL CLASSES
(defclass Gene)
;; (defclass Gene_related
;;   :subclass Gene)

;; PATTERNS
(defn gene-class [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Gene))

;; (defn create-gene-related [o name]
;;   (owl-class o
;;              (g/make-safe name)
;;              :label name
;;              :subclass Gene_related))

;; MAIN

;; read file
(let [genes (map clojure.string/lower-case
                 (g/get-lines
                  (g/get-resource "./input/gene.txt")))]

  ;; generate protein classes
  (doseq [gene genes]
    (gene-class gene))

  ;; Auxiliary functions
  (defn gene? [term]
    (some #(= % term) genes))
  ;; (defn gene-related? [term]
  ;;   (some #(re-find (re-pattern %) term) genes))
)
