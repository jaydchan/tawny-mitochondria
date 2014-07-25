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
  ncl.mitochondria.body
  (:use [tawny.owl])
  (:require [ncl.mitochondria.generic :as g]))

(defontology body
  :iri "http://ncl.ac.uk/mitochondria/body"
  :prefix "bod:")

;; OWL CLASSES
(defclass Body_Part)
(defclass Body_Part_related
  :subclass Body_Part)

;; PATTERNS
(defn create-body [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Body_Part))

(defn create-body-related [o name]
  (owl-class o
             (g/make-safe name)
             :label name
             :subclass Body_Part_related))

;; MAIN

;; read file
(let [parts (g/get-lines
             (g/get-resource "./input/body.txt"))]

  ;; generate body classes
  (doseq [p parts]
    (create-body p))

  ;; Auxiliary functions
  (defn body? [term]
    (some #(= % term) parts))
  (defn body-related? [term]
    (some #(re-find (re-pattern %) term) parts)))