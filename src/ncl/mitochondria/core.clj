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
        [clojure.java.shell :only [sh]])
  (:require [ncl.mitochondria paper])
  (:gen-class))

;; to run:
;; 1. M-x 'compile' ('lein run')
;; 2. M-x 'lein run'

(def output-file-path "./output/")
(defn- save-ontology
  "'Overlaods' save-ontology function."
  [name type]
  (tawny.owl/save-ontology (str output-file-path name) type))

(defn -main
  "Save ontologies in .omn and .owl format"
  [& args]

  (if (not (.exists (clojure.java.io/as-file output-file-path)))
    (sh "mkdir" "-p" output-file-path))

  (with-ontology ncl.mitochondria.paper/paper
    (save-ontology "paper.omn" :omn)
    (save-ontology "paper.owl" :owl))
)