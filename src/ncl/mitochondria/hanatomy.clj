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
  ncl.mitochondria.hanatomy
  (:use [tawny.owl])
  (:require [ncl.mitochondria.generic :as g]))

(defontology hanatomy
  :iri (str g/tm-iri "hanatomy")
  :prefix "hana:")

;; OWL CLASSES
(defclass Human_Anatomy)
;; (defclass Human_Anatomy_related
;;   :subclass Human_Anatomy)

;; PATTERNS
(defn hanatomy-class [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Human_Anatomy))

;; (defn create-hanatomy-related [o name]
;;   (owl-class o
;;              (g/make-safe name)
;;              :label name
;;              :subclass Human_Anatomy_related))

;; MAIN

;; read file
(let [parts (g/get-lines
             (g/get-resource "./input/hanatomy.txt"))]

  ;; generate hanatomy classes
  (doseq [p parts]
    (hanatomy-class p))

  ;; Auxiliary functions
  (defn hanatomy? [term]
    (some #(= % term) parts))
  ;; (defn hanatomy-related? [term]
  ;;   (some #(re-find (re-pattern %) term) parts))
)
