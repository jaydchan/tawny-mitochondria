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
  (:use [tawny.owl]
        [clojure.java.shell :only [sh]])
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
 (defclass Quarantined))

;; define object properties
(as-inverse
 (defoproperty containedIn)
;;   :domain ppr/Paper)
 (defoproperty contains))

(defdproperty contained)

;; PATTERNS
(defn source [paper]
;;  (has-value contained (individual ppr/paper paper)))
   (owl-some term containedIn paper))

(defn create-term [o name]
  (owl-class o
             (g/make-safe name)
             :label name))

(defn existing-class [rtype term paper o]
  (owl-class o
             (g/make-safe term)
             :subclass rtype
             (source paper)))

;; Auxiliary functions
(defn create-class [rtype term paper o create]
  (do
    (create o term)
    (existing-class rtype term paper o)))

;; MAIN
(defn driver
  []

  (if (not (.exists (clojure.java.io/as-file "./output/stats/")))
    (sh "mkdir" "-p" "./output/stats/"))
  (if (not (.exists (clojure.java.io/as-file "./output/classes/")))
    (sh "mkdir" "-p" "./output/classes/"))

  ;; read files
  (let [imap (g/read-file
              "./output/terms/cmap.txt")
        cmap (apply merge (map #(hash-map (first %) (second %)) imap))
        refined (g/get-lines
                 "./output/terms/refined.txt")
        quarantined (g/get-lines
                     "./output/terms/quarantined.txt")
        ]

    ;; for each term in refined
    (doseq [t (keys cmap)]
      (let [p (str "paper" (first (get cmap t)))
            rtype (if (contains? (into #{} quarantined) t)
                    Quarantined Refined)
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
         (cclazz b/body b/create-body-related)
         (c/component-related? t)
         (cclazz c/component c/create-component-related)
         (d/disease-related? t)
         (cclazz d/disease d/create-disease-related)
         (gne/gene? t)
         (cclazz gne/gene gne/create-gene-related)
         (pro/protein? t)
         (cclazz pro/protein pro/create-protein-related)

         ;; ELSE generate term

         :else
         (cclazz term create-term)))))

  ;; check --
  (let [capture (subclasses term Term) ;; todo missing one, make-safe problem ???
        refined (subclasses term Refined)
        quarantined (subclasses term Quarantined)
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

        ;; mito (apply clojure.set/union [component disease gene protein])
        ;; rl_mito (apply clojure.set/union
        ;;                [rl_component rl_disease rl_gene rl_protein])
        ;; rf_mito (clojure.set/intersection mito refined) ;; mito vs refined
        ;; q_mito (clojure.set/intersection mito quarantined) ;; mito vs quarantined
        ;; all (clojure.set/union mito body paper)
        ;; rl_all (clojure.set/union rl_mito rl_body)
        ;; rf_done (clojure.set/intersection refined all)
        ;; q_done (clojure.set/intersection quarantined all)
        ;; rf_left (clojure.set/difference refined rl_all)
        ;; q_left (clojure.set/difference quarantined rl_all)
        ;; rl_rf_done (clojure.set/intersection refined rl_all)
        ;; rl_q_done (clojure.set/intersection quarantined rl_all)
        ;; rl_rf_left (clojure.set/difference refined rl_all)
        ;; rl_q_left (clojure.set/difference quarantined rl_all)

        rf_body (clojure.set/intersection refined body)
        rf_component (clojure.set/intersection refined component)
        rf_disease (clojure.set/intersection refined disease)
        rf_gene (clojure.set/intersection refined gene)
        rf_protein (clojure.set/intersection refined protein)
        ]

    ;; print stats
    (let [set [
               capture refined quarantined
               paper
               ;; dmutation pmutation
               rl_body rl_component rl_disease rl_gene rl_protein
               body component disease gene protein
               ;; mito rl_mito rf_mito q_mito
               ;; all rl_all
               ;; rf_done q_done rf_left q_left
               ;; rl_rf_done rl_q_done rl_rf_left rl_q_left
               rf_body rf_component rf_disease rf_gene rf_protein
               ]
          name [
                "capture" "refined" "quarantined"
                "paper"
                ;; "dmutation" "pmutation"
                "rl_body" "rl_component" "rl_disease" "rl_gene" "rl_protein"
                "body" "component" "disease" "gene" "protein"
                ;; "mito" "rl_mito" "rf_mito" "q_mito"
                ;; "all" "rl_all"
                ;; "rf_done" "q_done" "rf_left" "q_left"
                ;; "rl_rf_done" "rl_q_done" "rl_rf_left" "rl_q_left"
                "rf_body" "rf_component" "rf_disease" "rf_gene" "rf_protein"
                ]
          outfile "construction.txt"]

      ;; clear old file
      (g/output
       (str "./output/stats/" outfile)
       ""
       false
       (str "Error: output stats to " outfile))

      ;; save stats to file
      (doseq [i (range 0 (count set))]
        (g/output
         (str "./output/stats/" outfile)
         (str "subclasses of " (get name i) ": "
              (count (get set i)) "\n")
         true
         (str "Error: output stats to " outfile))))

    ;; save class sets
    (let [set [
               refined
               rl_body rl_component rl_disease rl_gene rl_protein
               ;; rl_all
               ;; rf_done q_done rf_left q_left
               ;; rl_rf_done rl_q_done rl_rf_left rl_q_left
               rf_body rf_component rf_disease rf_gene rf_protein
               ]
          name [
                "refined"
                "rl_body" "rl_component" "rl_disease" "rl_gene" "rl_protein"
                ;; "rl_all"
                ;; "rf_done" "q_done" "rf_left" "q_left"
                ;; "rl_rf_done" "rl_q_done" "rl_rf_left" "rl_q_left"
                "rf_body" "rf_component" "rf_disease" "rf_gene rf_protein"
                ]]

      (doseq [i (range 0 (count set))]
        (let [n (get name i)
              file (str "./output/classes/" n ".txt")
              error (str "Error: output stats to " n ".txt")
              s (get set i)]

          ;; clear old file
          (g/output file "" false error)

          ;; save class set to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))

    ;; body vs capture
    ;; (println (clojure.set/intersection capture body))

    ))
