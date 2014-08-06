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

(ns ncl.mitochondria.core
  (:use [tawny.owl :exclude [save-ontology]]
        [ncl.mitochondria.generic :only [save-ontology]]
        [clojure.java.shell :only [sh]])
  (:require [ncl.mitochondria
             refine
             paper body mitochondria component
             disease gene protein
             ;; mutation
             term
             omim
             cq
             graph
             ])
  (:gen-class))

(defn -main
  ;; "Save ontologies in .omn and .owl format"
  [& args]

  ;; check if args exists
  ;; TRUE read in ontology
  ;; FALSE start from scratch

  (save-ontology ncl.mitochondria.paper/paper "paper.omn" :omn)
  (save-ontology ncl.mitochondria.paper/paper "paper.owl" :owl)

  (save-ontology ncl.mitochondria.body/body "body.omn" :omn)
  (save-ontology ncl.mitochondria.body/body "body.owl" :owl)

  (save-ontology
   ncl.mitochondria.mitochondria/mitochondria "mitochondria.omn" :omn)
  (save-ontology
   ncl.mitochondria.mitochondria/mitochondria "mitochondria.owl" :owl)

  (save-ontology ncl.mitochondria.component/component "component.omn" :omn)
  (save-ontology ncl.mitochondria.component/component "component.owl" :owl)

  (save-ontology ncl.mitochondria.gene/gene "gene.omn" :omn)
  (save-ontology ncl.mitochondria.gene/gene "gene.owl" :owl)

  (save-ontology ncl.mitochondria.protein/protein "protein.omn" :omn)
  (save-ontology ncl.mitochondria.protein/protein "protein.owl" :owl)

  ;; (save-ontology ncl.mitochondria.mutation/mutation "mutation.omn" :omn)
  ;; (save-ontology ncl.mitochondria.mutation/mutation "mutation.owl" :owl)

  ;; refine lists
  (println "Refining lists: Start")
  (if (not (.exists (clojure.java.io/as-file "./output/omim")))
    (sh "./scripts/make-wordlist.sh"))
  (if (not (.exists (clojure.java.io/as-file "./output/cenglish.txt")))
    (sh "./scripts/check-english.sh"))
  (println "Refining lists: Loading...")
  ;; (ncl.mitochondria.refine/driver)
  (println "Refining lists: Complete")

  ;; generate term classes
  (println "Generating term classes: Start")
  (ncl.mitochondria.term/driver)
  (save-ontology ncl.mitochondria.term/term "term.omn" :omn)
  (save-ontology ncl.mitochondria.term/term "term.owl" :owl)
  (println "Generating term clases: Complete.")

  ;; incorporate omim relations
  (println "Incorporating OMIM relations: Start")
  (ncl.mitochondria.omim/driver)
  (save-ontology ncl.mitochondria.disease/disease "disease.omn" :omn)
  (save-ontology ncl.mitochondria.disease/disease "disease.owl" :owl)
  (println "Incorporating OMIM relations: Complete")

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