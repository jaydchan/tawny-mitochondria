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
  ncl.mitochondria.manatomy
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology manatomy
  :iri "http://ncl.ac.uk/mitochondria/manatomy"
  :prefix "mana:")

;; OWL CLASSES
(defclass Mitochondrion_Anatomy)
;; (defclass Mitochondrion_Anatomy_related
;;   :subclass Mitochondrion_Anatomy)

;; PATTERNS
(defn manatomy-class [name]
  (println "manatomy")
  (owl-class (g/make-safe name)
             :label name
             :subclass Mitochondrion_Anatomy))

;; (defn create-manatomy-related [o name]
;;   (owl-class o
;;              (g/make-safe name)
;;              :label name
;;              :subclass Mitochondrion_Anatomy_related))

;; MAIN
(let [parts ["Outer membrane"
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
  (doseq [p parts]
    (manatomy-class p))

  ;; Auxiliary functions
  (defn get-manatomy [term]
    (g/find-first #(= (clojure.string/lower-case %) term) parts))
  (defn manatomy? [term]
    (not (empty? (get-manatomy term))))
  ;; (defn manatomy-related? [term]
  ;;   (some #(re-find (re-pattern %) term) parts))
)
