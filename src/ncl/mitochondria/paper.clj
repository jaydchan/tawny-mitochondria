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

(ns ^{:doc "Translating paper information."
      :author "Jennifer Warrender"}
  ncl.mitochondria.paper
  (:use [tawny.owl]
        (incanter core io excel))
  (:require [clojure.java.io :as io]
            [clojure.string :only split]))

(defontology paper
  :iri "http://ncl.ac.uk/mitochondria/paper"
  :prefix "ppr:")

;; OWL CLASSES
 (defclass Paper)

;; define data properties
(defdproperty hasTitle
  :domain Paper)
(defdproperty hasAuthor
   :domain Paper)

;; Auxiliary functions
(defn get-lines
  "Reads in file-name line by line. Returns a java.lang.Cons"
  [file-name]
  (with-open [r (io/reader file-name)]
    (doall (line-seq r))))

(defn fact-title
  "TODO"
  [title]
  (fact hasTitle (literal title :lang "en")))

(defn fact-author
  "TODO"
  [author]
  (fact hasAuthor (literal author :lang "en")))

(defn create-paper
  "Pattern - defines paper instances. TODO"
  [name title authors]
  (individual name
              :type Paper
              :fact
              (fact-title title)
              (map fact-author authors)))

;; MAIN
(def ^{:doc "The paper information read in from resources text
  file as java.lang.Cons."
       :private true}
  string-results (get-lines (.getFile (io/resource "./Input/papers.log"))))

(def ^{:doc "The resolution information read in from resources text
  file as LazySeq."
       :private true}
  results (for [r string-results]
            (read-string r)))

(doseq [r results]
    (apply create-paper r))
