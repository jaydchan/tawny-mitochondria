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
  ncl.mitochondria.disease
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology disease
  :iri "http://ncl.ac.uk/mitochondria/disease"
  :prefix "dis:")

;; OWL CLASSES
(defclass Disease
  :subclass ncl.mitochondria.mitochondria/Mitochondria)
(defclass Disease_related
  :subclass Disease)

;; PATTERNS
(defn create-disease [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Disease))

(defn create-disease-related [o name]
  (owl-class o
             (g/make-safe name)
             :label name
             :subclass Disease_related))

;; MAIN

;; read file
(let [diseases (g/get-lines
                (g/get-resource  "./input/disease.txt"))]

  ;; generate disease classes
  (doseq [d diseases]
    (create-disease d))

  ;; Auxiliary functions
  (defn disease? [term]
    (some #(= % term) diseases))
  (defn disease-related? [term]
    (some #(re-find (re-pattern %) term) diseases)))