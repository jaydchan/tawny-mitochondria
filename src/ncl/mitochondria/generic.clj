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
  ncl.mitochondria.generic
  (:use [clojure.java.shell :only [sh]]
        [clojure.java.io :only [as-file reader]])
  (:require [tawny.owl :only save-ontology]))

(defonce output-file-path "./output/")

(defn save-ontology
  "'Overloads' save-ontology function."
  [o name format]
  (if-not (.exists
           (clojure.java.io/as-file (str output-file-path "ontologies")))
    (sh "mkdir" "-p" (str output-file-path "ontologies")))
  (tawny.owl/save-ontology o (str output-file-path "ontologies/" name) format))

(defn get-lines
  "Reads in file line by line. Returns a java.lang.Cons."
  [file-name]
  (with-open [r (reader file-name)]
    (doall (line-seq r))))

(defn read-file
  "Reads in file line by line as Clojure atoms. Returns a LazySeq."
  [file-name]
  (for [r (get-lines file-name)] (read-string r)))

(defn get-resource
  "TODO"
  [file-name]
  (.getFile (clojure.java.io/resource file-name)))

(defn make-safe-ignore-case
  "TODO"
  [term]
  (clojure.string/replace
   (clojure.string/replace term #"\s" "_") #"'|>" ""))

(defn make-safe
  "TODO"
  [term]
  (clojure.string/lower-case (make-safe-ignore-case term)))

(defn output
  "APPENDs STRING to OUTPUT-FILE unless there is an ERROR"
  [output-file string append error]
  (try
    (spit output-file
          string
          :append append)
    (catch
        Exception exp (println error exp))))

(defn substring?
  "Does STR contains SUBSTR?"
  [substr str]
  (.contains str substr))