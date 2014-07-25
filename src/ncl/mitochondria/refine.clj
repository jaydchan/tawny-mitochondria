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
  ncl.mitochondria.refine
  (:use (incanter core io excel))
  (:require [ncl.mitochondria
             [generic :as g]]))

;; Auxiliary functions

;; used by fuzzy
(defn substring?
  "Does STR contains SUBSTR?"
  [substr str]
  (.contains str substr))

(defn get-pduplicate
  "Returns vector: [ITEM A] where A is all possible 'duplicates' (terms that
  contains the ITEM substring) found in COLL."
  [coll item]
  [item (filter
         #(and (substring? item %) (not (= item %))) coll)])

(defn get-pduplicates
  "Returns a list of terms that have possible dupilictes."
  [set]
  (remove #(empty? (second %))
          (for [term set]
            (get-pduplicate set term))))

(defn get-acronym
  "Returns 'acronym' of given TERM i.e. first letter of each word in
TERM."
  [term]
  (clojure.string/join
   (map first (clojure.string/split term #"\s"))))

(defn get-pacronym
  "Returns vector: [ITEM A] where A is all possible 'acronyms' found
in COLL."
  [coll item]
  [item (filter
         #(= (get-acronym item) %)
         coll)])

(defn get-pacronyms
  "Returns list of terms that have possible acronyms."
  [set]
  (remove #(empty? (second %))
          (for [term set]
            (get-pacronym set term))))

(defn duplicate?
  "Does VECTOR contain more than one ITEM?"
  [vector item]
  (> (count (filterv #(= item %) vector)) 1))

(defn get-duplicates
  "Returns all 'duplicate' (equivalent) terms."
  [data]
  (filterv #(duplicate? (map first data) (first %)) data))

(defn fuzzy
  "Returns a list of s1 terms that contain s2 term(s) (i.e. the s2 terms
  are used as possible substrings). This is done by combining subsets
  of s1. The results are restricted such that all found s1 subsets
  should have less than N items."
  [n s1 s2]
  (apply clojure.set/union
         (filter #(< (count %) n)
                 (for [term s2]
                   (into #{}
                         (filter (partial substring? term)
                                 s1))))))

;; fuzzy tests
;; (let [capture (set ["hello world" "hello" "world" "simon"])
;;       omim (set ["hello" "mon"])]
;;   (println (type (first capture)))
;;   (println (substring? "hello" "hello world"))
;;   (println (substring? "yello" "hello world"))
;;   (println (filter (partial substring? "hello") capture))
;;   (println (fuzzy-test 33 capture omim))
;;   (println (filter #(< (count %) 50) [(repeat 100 1) (repeat 2 1)]))
;; )

;; MAIN
(defn driver
  []
  ;; read files and create sets
  (let [files (map #(str "Paper" % "_terms.txt") (range 1 31))
        all (for [f files]
              (g/get-lines
               (g/get-resource
                (str "./input/Terms/" f))))
        input (apply clojure.set/union all)
        data (map
              #(clojure.string/split  % #"\s=>\spaper:\spaper|\sline:\s|\spos:\s")
              input)
        duplicate (sort-by first (get-duplicates data))
        cmap (apply merge (map #(sorted-map (first %) (into [] (rest %)))
                               (sort-by second data)))
        capture (into #{} (keys cmap))
        omim (into #{} (g/get-lines
                        (g/get-resource "omim.txt")))
        english (into #{} (g/get-lines
                           (g/get-resource "cenglish.txt")))
        filtered (clojure.set/difference omim english)
        disease (clojure.set/intersection capture filtered)
        related (into #{} (fuzzy 33 capture filtered))
        refined (clojure.set/union disease related)
        refined_full (select-keys cmap refined)
        quarentined (clojure.set/difference capture refined)
        quarentined_full (select-keys cmap quarentined)
        rrefined (for [i (range 0 100)]
                   (rand-nth (seq refined)))
        rquarentined (for [i (range 0 100)]
                       (rand-nth (seq quarentined)))
        pduplicate (get-pduplicates refined)
        pacronym (get-pacronyms refined)
        cq (into #{} (map clojure.string/lower-case
                          (g/get-lines
                           (g/get-resource "./input/cq.txt"))))
        refined_cq (fuzzy 10 cq filtered)
        quarentined_cq (clojure.set/difference cq refined_cq)
        ]

    ;; output stats
    ;; all -- includes redundant terms
    (let [name "all_results.txt"
          outfile (str "./output/stats/" name)
          error (str "Error: output stats to " name)]
      ;; clear old file
      (g/output outfile "" false error)

      ;; number of terms per paper
      (g/output outfile
                (clojure.string/join "\n" (map count all))
                true error))

    ;; output stats
    ;; capture -- excludes redundant terms
    (let [name "capture_results.txt"
          outfile (str "./output/stats/" name)
          error (str "Error: output stats to " name)]
      ;; clear old file
      (g/output outfile "" false error)

      ;; number of terms per paper
      (let [grouped (group-by #(first (get cmap (key %))) cmap)
            sorted (sort-by #(read-string (first %)) grouped)]
        (g/output outfile
                  (clojure.string/join
                   "\n" (map #(count (second %)) sorted))
                  true error)))

    ;; save terms
    (let [coll [duplicate cmap capture omim english filtered disease related
               refined refined_full quarentined quarentined_full rrefined
               rquarentined pduplicate pacronym]
          name ["duplicate" "cmap" "capture" "omim" "english" "filtered"
                "disease" "related" "refined" "refined_full" "quarentined"
                "quarentined_full" "rrefined" "rquarentined" "pduplicate"
                "pacronym"]]
      (doseq [i (range 0 (count coll))]
        (let [n (get name i)
              file (str "./output/terms/" n ".txt") ;; TODO make sure this exists
              error (str "Error: output stats to " n ".txt")
              s (get coll i)]

          ;; print number of terms for each coll
          (println (str "Total number of " n " terms: " (count s)))

          ;; clear old file
          (g/output file "" false error)

          ;; save term coll to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))

    ;; save cqs
    (let [set [cq refined_cq quarentined_cq]
          name ["cq" "refined_cq" "quarentined_cq"]]
      (doseq [i (range 0 (count set))]
        (let [n (get name i)
              file (str "./output/cqs/" n ".txt") ;; TODO make sure exists
              error (str "Error: output stats to " n ".txt")
              s (get set i)]

          ;; print number of cqs for each set
          (println (str "Total number of " n "s: " (count s)))

          ;; clear old file
          (g/output file "" false error)

          ;; save cq set to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))
    ))