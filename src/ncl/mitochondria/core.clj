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

(ns ncl.mitochondria.core
  (:use [ncl.mitochondria.generic :only [save-ontology]]
        [clojure.java.shell :only [sh]])
  (:require [ncl.mitochondria
             refine
             paper hanatomy mitochondria manatomy
             disease gene protein
             mutation
             term
             omim
             cq
             graph
             ])
  (:gen-class))

(defn -main
  [& args]

  ;; check if args exists
  ;; TRUE read in ontology
  ;; FALSE start from scratch

  ;; refine lists
  (println "Refining lists: Start")
  ;; IGNORE: no longer used
  ;; (if-not (.exists (clojure.java.io/as-file "./resources/input/omim"))
  ;;   (sh "./scripts/make-wordlist.sh"))
  ;; (if-not (.exists (clojure.java.io/as-file "./output/cenglish.txt"))
  ;;   (sh "./scripts/check-english.sh"))
  (println "Refining lists: Loading...")
  (ncl.mitochondria.refine/driver)
  (println "Refining lists: Complete")

  ;; generate term classes
  (println "Generating term classes: Start")
  (ncl.mitochondria.term/driver)
  (save-ontology ncl.mitochondria.term/term "term.omn" :omn)
  (save-ontology ncl.mitochondria.term/term "term.owl" :owl)
  (println "Generating term clases: Complete.")

  ;; Save ontologies in .omn and .owl format
  (save-ontology ncl.mitochondria.paper/paper "paper.omn" :omn)
  (save-ontology ncl.mitochondria.paper/paper "paper.owl" :owl)

  (save-ontology ncl.mitochondria.hanatomy/hanatomy "hanatomy.omn" :omn)
  (save-ontology ncl.mitochondria.hanatomy/hanatomy "hanatomy.owl" :owl)

  (save-ontology
   ncl.mitochondria.mitochondria/mitochondria "mitochondria.omn" :omn)
  (save-ontology
   ncl.mitochondria.mitochondria/mitochondria "mitochondria.owl" :owl)

  (save-ontology ncl.mitochondria.manatomy/manatomy "manatomy.omn" :omn)
  (save-ontology ncl.mitochondria.manatomy/manatomy "manatomy.owl" :owl)

  (save-ontology ncl.mitochondria.gene/gene "gene.omn" :omn)
  (save-ontology ncl.mitochondria.gene/gene "gene.owl" :owl)

  (save-ontology ncl.mitochondria.protein/protein "protein.omn" :omn)
  (save-ontology ncl.mitochondria.protein/protein "protein.owl" :owl)

  (save-ontology ncl.mitochondria.mutation/mutation "mutation.omn" :omn)
  (save-ontology ncl.mitochondria.mutation/mutation "mutation.owl" :owl)

  (save-ontology ncl.mitochondria.disease/disease "disease.omn" :omn)
  (save-ontology ncl.mitochondria.disease/disease "disease.owl" :owl)

  ;; incorporate omim relations
  ;; (println "Incorporating OMIM relations: Start")
  ;; (ncl.mitochondria.omim/driver)
  ;; (save-ontology ncl.mitochondria.disease/disease "disease.omn" :omn)
  ;; (save-ontology ncl.mitochondria.disease/disease "disease.owl" :owl)
  ;; (println "Incorporating OMIM relations: Complete")

  ;; cqs
  ;; (println "Incorporating cq queries: Start")
  ;; (ncl.mitochondria.cq/driver)
  ;; (save-ontology ncl.mitochondria.cq/cq "cq.omn" :omn)
  ;; (save-ontology ncl.mitochondria.cq/cq "cq.owl" :owl)
  ;; (println "Incorporating cq queries: Complete")

  ;; generate graphs
  ;; (println "Generating graphs: Start")
  ;; (ncl.mitochondria.graph/driver)
  ;; (println "Generating graphs: Complete")
)
