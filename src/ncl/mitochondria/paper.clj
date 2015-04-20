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

(ns ^{:doc "Translating paper information."
      :author "Jennifer Warrender"}
  ncl.mitochondria.paper
  (:use [tawny.owl])
  (:require [clojure.string :only split]
            [ncl.mitochondria.generic :as g]))

(defontology paper
  :iri "http://homepages.cs.ncl.ac.uk/jennifer.warrender/mitochondria/latest/paper"
  :prefix "ppr:")

;; OWL CLASSES
 (defclass Paper)

;; ;; define data properties
;; (defdproperty hasTitle
;;   :domain Paper)
;; (defdproperty hasAuthor
;;    :domain Paper)
;; (defdproperty hasPMID
;;    :domain Paper)

;; define annotation properties
(defaproperty hasTitle)
(defaproperty hasAuthor)
(defaproperty hasPMID)

;; Auxiliary functions
(defn title-fact
  "TODO"
  [title]
  ;; (fact hasTitle (literal title :lang "en"))
  (annotation hasTitle (literal title :lang "en")))

(defn author-fact
  "TODO"
  [author]
  ;; (fact hasAuthor (literal author :lang "en"))
  (annotation hasAuthor (literal author :lang "en")))

(defn pmid-fact
  "TODO"
  [pmid]
  ;; (fact hasPMID (literal (str "PMID:" pmid) :RDF :RDF_Literal_String))
  (annotation hasPMID (literal (str "PMID:" pmid) :RDF :RDF_Literal_String)))

(defn paper-class
  "Pattern - defines paper instances."
  [name title authors pmid]
  ;; (individual name
  ;;             :type Paper
  ;;             :fact
  ;;             (title-fact title)
  ;;             (map author-fact authors)
  ;;             (pmid-fact pmid)))
  (owl-class name
             :super Paper
             :annotation
             (title-fact title)
             (map author-fact authors)
             (pmid-fact pmid)))


;; MAIN
;; read file
(let [papers (g/read-file
              (g/get-resource
               "./input/papers.log"))]

  ;; generate paper classes
  (doseq [p papers]
    (apply paper-class p)))
