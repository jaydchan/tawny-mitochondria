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
(defn disease-class [name omim lname]
  (let [dname (g/make-safe name)]
    (owl-class dname
               :label name
               :subclass Disease)
    (if-not (nil? omim)
      (owl-class dname
                 :annotation (see-also (str "OMIMID:" omim))))
    (if-not (nil? lname)
      (owl-class dname
                 :label (str "The long name for this disease is " lname)))))

(defn create-disease-related [o name]
  (owl-class o
             (g/make-safe name)
             :label name
             :subclass Disease_related))

;; MAIN

;; read file
(let [diseases (into [] (g/read-file
                         (g/get-resource
                          "./input/disease.txt")))]

  ;; generate disease classes
  (doseq [d diseases]
    (disease-class (get d 0) (get d 1) (get d 2)))

  ;; Auxiliary functions
  (defn disease? [term]
    (some #(= % term) diseases))
  (defn disease-related? [term]
    (some #(re-find (re-pattern %) term) (map first diseases))))

  ;; Additional information
  (doseq [clazz ["LCAD" "LCHAD" "MAD" "MCAD" "SCAD" "SCHAD" "VLCAD"]]
    (refine (owl-class (g/make-safe clazz))
            :subclass (owl-class (g/make-safe "Beta-oxidation Defects"))))

(doseq [clazz ["Co-Enzyme Q10 Deficiency" "Complex I Deficiency"
               "Complex II Deficiency" "Complex III Deficiency"
               "Complex IV Deficiency" "Leigh Disease" "LCAD"
               "LCHAD" "MCAD" "MELAS" "MNGIE" "NARP" "SCHAD" "VLCHAD"]]
  (refine (owl-class (g/make-safe clazz))
          :subclass (owl-class (g/make-safe "Mitochondrial Encephalopathy"))))

(doseq [clazz ["Barth Syndrome" "Complex I Deficiency"
               "Complex III Deficiency" "Complex IV Deficiency"
               "CPEO" "LCHAD" "Mitochondrial DNA Depletion"]]
  (refine (owl-class (g/make-safe clazz))
          :subclass (owl-class (g/make-safe "Mitochondrial Myopathy"))))

(doseq [clazz ["Complex I Deficiency" "Complex II Deficiency"
               "Complex III Deficiency" "Complex IV Deficiency"
               "Complex V Deficiency" "NARP"]]
  (refine (owl-class (g/make-safe clazz))
          :subclass (owl-class (g/make-safe "Respiratory Chain"))))

(refine (owl-class (g/make-safe "Mitochondrial Cytopathy"))
        :equivalent Disease)