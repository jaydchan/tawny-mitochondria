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
  ncl.mitochondria.omim
  (:use [tawny.owl])
  (:require [ncl.mitochondria
             [generic :as g]
             [protein :as p]
             [gene :as gne]
             [disease :as d]
             [refine :as r]]))

(def hasAssociatedProtein
  (object-property d/disease
                   "hasAssociatedProtein"
                   :domain d/Disease
                   :range p/Protein))

(def hasAssociatedGene
  (object-property d/disease
                   "hasAssociatedGene"
                   :domain d/Disease
                   :range gne/Gene))

;; Auxiliary functions
(defn add-association
  "'Refines' the DISEASE definition to include an exstential OPROPERTY
  property that links DISEASE with CLAZZ."
  [disease oproperty clazz]
  (owl-class d/disease
             disease
             :subclass (owl-some oproperty clazz)))

;; MAIN
(defn driver
  []
  ;; read files and create sets
  (let [disease (g/read-file (g/get-resource  "./input/disease.txt"))
        dmap (apply merge (map #(sorted-map (second %) (first %))
                               (filter #(some? (second %)) disease)))

        ids (keys dmap)
        ofiles (map #(str "./output/omim/omim" % ".txt") ids)
        oterms (map #(into #{} (g/get-lines %))
                    ofiles)
        omap (apply merge
                    (map #(hash-map %1 %2)
                         ids
                         oterms))

        pinput (map clojure.string/lower-case
                    (g/get-lines
                     (g/get-resource "./input/protein.txt")))
        proteins (into #{} (map
                            #(clojure.string/replace % "_human" "") pinput))
        genes (into #{}
                    (map clojure.string/lower-case
                         (g/get-lines
                          (g/get-resource "./input/gene.txt"))))

        pmap (apply merge
                    (map
                     #(hash-map %
                                (clojure.set/intersection proteins
                                                          (get omap %)))
                     ids))
        ppmap (apply merge
                     (map
                      #(hash-map %
                                 (r/get-pduplicates proteins
                                                (get omap %)))
                      ids))
        gmap (apply merge
                    (map
                     #(hash-map %
                                (clojure.set/intersection genes
                                                          (get omap %)))
                     ids))
        pgmap (apply merge
                     (map
                      #(hash-map %
                                 (r/get-pduplicates genes (get omap %)))
                      ids))
        ]

    ;; implement omim relations
    (doseq [id ids]
      (let [disease (owl-class d/disease (g/make-safe (get dmap id)))
            protein (map (partial owl-class p/protein) (get pmap id))
            gene (map (partial owl-class gne/gene) (get gmap id))]

        (if-not (empty? protein)
          (add-association disease
                           hasAssociatedProtein
                           protein))

        (if (not (empty? gene))
          (add-association disease
                           hasAssociatedGene
                           gene))))

    ;; save terms
    (let [coll [ppmap pgmap]
          name ["ppmap" "pgmap"]
          size (map count coll)]
      (doseq [i (range 0 (count coll))]
        (let [n (get name i)
              file (str "./output/terms/" n ".txt")
              error (str "Error: output stats to " n ".txt")
              s (get coll i)
              ]

          ;; clear old file
          (g/output file "" false error)

          ;; save term coll to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))


    ))