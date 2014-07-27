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
  ncl.mitochondria.term
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             [generic :as g]
             [paper :as ppr]
             [body :as b]
             [mitochondria :as mit]
             [component :as c]
             [disease :as d]
             [gene :as gne]
             [protein :as pro]
             ;; [mutation :as mut]
             ]))

;; new ontology
(defontology term
  :iri "http://ncl.ac.uk/mitochondria/term"
  :prefix "term:")

;; import other ontologies
(owl-import ppr/paper)
(owl-import b/body)
(owl-import mit/mitochondria)
(owl-import c/component)
(owl-import d/disease)
(owl-import gne/gene)
(owl-import pro/protein)
;; (owl-import mut/mutation)

;; OWL CLASSES
(defclass Term)

(as-subclasses
 Term
 (defclass Refined)
 (defclass Quarentined))


;; define object properties
(as-inverse
 (defoproperty containedIn)
;;   :domain ppr/Paper)
 (defoproperty contains))

(defdproperty contained)

;; PATTERNS
(defn source [paper]
;;  (has-value contained (individual ppr/paper paper)))
   (owl-some containedIn paper))

(defn rterm [clazz paper]
  (refine clazz
          :subclass Refined
          (source paper)))

(defn qterm [clazz paper]
  (refine clazz
          :subclass Quarentined
          (source paper)))

(defn create-term [o name]
  (owl-class o
             (g/make-safe name)
             :label name))

;; Auxiliary functions
(defn create-class [rtype term paper create o]
  (do
    (create o term)
    (rtype (owl-class o (g/make-safe term)) paper)))

(defn existing-class [rtype term paper o]
  (rtype (owl-class o (g/make-safe term)) paper))

;; MAIN
(defn driver
  []

  ;; read files
  (let [imap (g/read-file
              "./output/terms/cmap.txt")
        cmap (apply merge (map #(hash-map (first %) (second %)) imap))
        refined (g/get-lines
                 "./output/terms/refined.txt")
        quarentined (g/get-lines
                     "./output/terms/quarentined.txt")
        ]

    ;; for each term in refined
    (doseq [t (keys cmap)]
      (let [p (str "paper" (first (get cmap t)))
            rtype (if (contains? (into #{} quarentined) t) qterm rterm)
            eclazz (partial existing-class rtype t p)
            cclazz (partial create-class rtype t p)]

        (cond
         ;; check if term already exists in other ontologies
         ;; TRUE refine class st :subclass Refined

         (b/body? t)
         (eclazz b/body)
         (c/component? t)
         (eclazz c/component)
         (d/disease? t)
         (eclazz d/disease)
         (gne/gene? t)
         (eclazz gne/gene)
         (pro/protein? t)
         (eclazz pro/protein)
         ;; (mut/mutation? t)
         ;; (eclazz mut/mutation)

         ;; check if term is related to any term
         ;; TRUE generate term AND :subclass Body_Part_related

         (b/body-related? t)
         (cclazz b/create-body-related b/body)
         (c/component-related? t)
         (cclazz c/create-component-related c/component)
         (d/disease-related? t)
         (cclazz d/create-disease-related d/disease)
         (gne/gene? t)
         (cclazz gne/create-gene-related gne/gene)
         (pro/protein? t)
         (cclazz pro/create-protein-related pro/protein)

         ;; ELSE generate term

         :else
         (cclazz create-term term)))))

  ;; check --
  (let [capture (subclasses term Term) ;; todo missing one, make-safe problem ???
        refined (subclasses term Refined)
        quarentined (subclasses term Quarentined)
        paper (subclasses ppr/paper ppr/Paper)
        ;; dmutation (subclasses mut/mutation mut/DNA_Mutation)
        ;; pmutation (subclasses mut/mutation mut/Protein_Mutation)
        rl_body (subclasses b/body b/Body_Part_related)
        rl_component (subclasses c/component c/Component_related)
        rl_disease (subclasses d/disease d/Disease_related)
        rl_gene (subclasses gne/gene gne/Gene_related)
        rl_protein (subclasses pro/protein pro/Protein_related)
        body (clojure.set/difference (subclasses b/body b/Body_Part) rl_body)
        component (clojure.set/difference
                   (subclasses c/component c/Component) rl_component)
        disease (clojure.set/difference
                 (subclasses d/disease d/Disease) rl_disease)
        gene (clojure.set/difference
              (subclasses gne/gene gne/Gene) rl_gene)
        protein (clojure.set/difference
                 (subclasses pro/protein pro/Protein) rl_protein)
        mito (apply clojure.set/union [component disease gene protein])
        rl_mito (apply clojure.set/union
                       [rl_component rl_disease rl_gene rl_protein])
        rf_mito (clojure.set/intersection mito refined) ;; mito vs refined
        q_mito (clojure.set/intersection mito quarentined) ;; mito vs quarentined
        all (clojure.set/union mito body paper)
        rl_all (clojure.set/union rl_mito rl_body)
        rf_done (clojure.set/intersection refined all)
        q_done (clojure.set/intersection quarentined all)
        rf_left (clojure.set/difference refined rl_all)
        q_left (clojure.set/difference quarentined rl_all)
        rl_rf_done (clojure.set/intersection refined rl_all)
        rl_q_done (clojure.set/intersection quarentined rl_all)
        rl_rf_left (clojure.set/difference refined rl_all)
        rl_q_left (clojure.set/difference quarentined rl_all)
        ]

    ;; print stats
    (let [set [capture refined quarentined
               paper
               ;; dmutation pmutation
               rl_body rl_component rl_disease rl_gene rl_protein
               body component disease gene protein
               mito rl_mito rf_mito q_mito
               all rl_all
               rf_done q_done rf_left q_left
               rl_rf_done rl_q_done rl_rf_left rl_q_left]
          name ["capture" "refined" "quarentined"
                "paper"
                ;; "dmutation" "pmutation"
                "rl_body" "rl_component" "rl_disease" "rl_gene" "rl_protein"
                "body" "component" "disease" "gene" "protein"
                "mito" "rl_mito" "rf_mito" "q_mito"
                "all" "rl_all"
                "rf_done" "q_done" "rf_left" "q_left"
                "rl_rf_done" "rl_q_done" "rl_rf_left" "rl_q_left"]]

      (doseq [i (range 0 (count set))]
        (println
         (str "subclasses of " (get name i) ": "
              (count (get set i))))))

    ;; save class sets
    (let [set [refined
               rl_body rl_component rl_disease rl_gene rl_protein
               rl_all
               rf_done q_done rf_left q_left
               rl_rf_done rl_q_done rl_rf_left rl_q_left]
          name ["refined"
                "rl_body" "rl_component" "rl_disease" "rl_gene" "rl_protein"
                "rl_all"
                "rf_done" "q_done" "rf_left" "q_left"
                "rl_rf_done" "rl_q_done" "rl_rf_left" "rl_q_left"]]

      (doseq [i (range 0 (count set))]
        (let [n (get name i)
              file (str "./output/classes/" n ".txt") ;; TODO make sure exists
              error (str "Error: output stats to " n ".txt")
              s (get set i)]

          ;; clear old file
          (g/output file "" false error)

          ;; save class set to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))

    ;; body vs capture
    (println (clojure.set/intersection capture body))

    ))
