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
  ncl.mitochondria.component
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology component
  :iri "http://ncl.ac.uk/mitochondria/component"
  :prefix "com:")

;; OWL CLASSES
(defclass Component
  :subclass ncl.mitochondria.mitochondria/Mitochondria)
(defclass Component_related
  :subclass Component)

;; PATTERNS
(defn create-component [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Component))

(defn create-component-related [o name]
  (owl-class o
             (g/make-safe name)
             :label name
             :subclass Component_related))

;; MAIN
(let [components ["Outer membrane"
                  "Porin"
                  "Intermembrane space"
                  "Intracristal space"
                  "Peripheral space"
                  "Lamella"
                  "Inner membrane"
                  "Inner boundary membrane"
                  "Cristal membrane"
                  "Matrix"
                  "Cristae"
                  "Mitochondrial DNA"
                  "Matrix granule"
                  "Ribosome"
                  "ATP synthase"]]

  ;; generate body classes
  (doseq [c components]
    (create-component c))

  ;; Auxiliary functions
  (defn component? [term]
    (some #(= % term) components))
  (defn component-related? [term]
    (some #(re-find (re-pattern %) term) components)))