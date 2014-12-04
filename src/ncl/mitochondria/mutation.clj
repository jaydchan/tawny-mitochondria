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
  ncl.mitochondria.mutation
  (:use [tawny.owl])
  (:require [ncl.mitochondria.generic :as g]))

(defontology mutation
  :iri "http://ncl.ac.uk/mitochondria/mutation"
  :prefix "mut:")

;; OWL CLASSES
(defclass Mutation
  :subclass ncl.mitochondria.mitochondria/Mitochondria)

(as-subclasses
 Mutation
 (defclass DNA_Mutation)
 (defclass Protein_Mutation))

;; ;; Mutations nomenclature
;; ;; http://www.hgmd.cf.ac.uk/docs/mut_nom.html

;; ;; DNA Substitution
;; (defn- dna-mutation? [term]
;;   (re-find #"[acgt]>[acgt]" term))

;; ;; Protein Substitution
;; (defn- protein-mutation? [term]
;;   (re-find
;;    #"[gpavlimcfywhkrqnedst]\d+[gpavlimcfywhkrqnedst]"
;; ;;   #"^[gpavlimcfywhkrqnedst]\d+[gpavlimcfywhkrqnedst]$"
;;    term))

;; (defn mutation? [term]
;;   (or (dna-mutation? term) (protein-mutation? term)))

(defn- dna-mutation-class [o name]
  (owl-class (g/make-safe name)
             :label name
             :subclass DNA_Mutation))

(defn- protein-mutation-class [name]
  (owl-class (g/make-safe name)
             :label name
             :subclass Protein_Mutation))

;; (defn create-mutation [o name]
;;   (if (dna-mutation? name)
;;     (create-dna-mutation o name)
;;     (create-protein-mutation o name)))

;; MAIN

;; read file
(let [dmutations (g/get-lines
                  "./output/terms/pdmutation.txt")
      pmutations (g/get-lines
                  "./output/terms/ppmutation.txt")
      mutations (clojure.set/union dmutations pmutations)]

  ;; generate dna mutation classes
  (doseq [m dmutations]
    (dna-mutation-class m))

  ;; generate protein mutation classes
  (doseq [m pmutations]
    (protein-mutation-class m))

  ;; Auxiliary functions
  (defn mutation? [term]
    (some #(= % term) mutations))

  ;; ;; also need to look out for related withour _human ;; ALREADY DONE ???
  ;; (defn protein-related? [term]
  ;;   (some #(re-find (re-pattern %) term) proteins))

)