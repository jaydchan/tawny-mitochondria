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
  ncl.mitochondria.disease
  (:use [tawny.owl]
	[tawny.read :only [intern-entity]])
  (:require [ncl.mitochondria
             mitochondria
             [generic :as g]]))

(defontology disease
  :iri "http://homepages.cs.ncl.ac.uk/jennifer.warrender/mitochondria/latest/disease"
  :prefix "dis:")

;; OWL CLASSES
(defclass Disease)
;; (defclass Disease_related
;;   :subclass Disease)

;; PATTERNS
(defn disease-class [name omim lname]
  (let [dname (g/make-safe name)]
    (intern-entity
     (owl-class dname
		:label name
		:subclass Disease))
     (if-not (nil? omim)
      (owl-class dname
                 :annotation (see-also (str "OMIMID:" omim))))
    (if-not (nil? lname)
      (owl-class dname
                 :annotation
                 (see-also lname)))))

;; (defn create-disease-related [o name]
;;   (owl-class o
;;              (g/make-safe name)
;;              :label name
;;              :subclass Disease_related))


;; MAIN
;; read file
(let [diseases (into [] (g/read-file
                         (g/get-resource
                          "./input/disease.txt")))
     dterms (for [d diseases] (get d 0))]

  ;; generate disease classes
  (doseq [d diseases]
    (disease-class (get d 0) (get d 1) (get d 2)))

  ;; Auxiliary functions
  (defn get-disease [term]
    (g/find-first #(= (clojure.string/lower-case %) term) dterms))
  (defn disease? [term]
    (not (empty? (get-disease term))))
  ;; (defn disease-related? [term]
  ;;   (some #(re-find (re-pattern %) term) (map first diseases)))
)

;; Additional information
(doseq [clazz [lcad lchad mad mcad scad schad vlchad]]
       (refine (owl-class clazz)
               :subclass (owl-class beta-oxidation_defects)))

(doseq [clazz [co-enzyme_q10_deficiency complex_i_deficiency
       complex_ii_deficiency complex_iii_deficiency
       complex_iv_deficiency leigh_disease lcad
       lchad mcad melas mngie narp schad vlchad]]
       (refine (owl-class clazz)
               :subclass (owl-class mitochondrial_encephalopathy)))

(doseq [clazz [barth_syndrome complex_i_deficiency
       complex_iii_deficiency complex_iv_deficiency
       cpeo lchad mitochondrial_dna_depletion]]
       (refine (owl-class clazz)
               :subclass (owl-class mitochondrial_myopathy)))

(doseq [clazz [complex_i_deficiency complex_ii_deficiency
       complex_iii_deficiency complex_iv_deficiency
       complex_v_deficiency narp]]
       (refine (owl-class clazz)
               :subclass (owl-class respiratory_chain)))

(refine (owl-class mitochondrial_cytopathy)
        :equivalent Disease)
