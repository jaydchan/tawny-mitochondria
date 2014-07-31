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
  (:require [ncl.mitochondria
             [generic :as g]]))

;; Auxiliary functions
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
                         (filter (partial g/substring? term)
                                 s1))))))

;; fuzzy tests
;; (let [capture (set ["hello world" "hello" "world" "simon"])
;;       omim (set ["hello" "mon"])]
;;   (println (type (first capture)))
;;   (println (g/substring? "hello" "hello world"))
;;   (println (g/substring? "yello" "hello world"))
;;   (println (filter (partial substring? "hello") capture))
;;   (println (fuzzy 33 capture omim))
;;   (println (filter #(< (count %) 50) [(repeat 100 1) (repeat 2 1)]))
;; )

(defn- duplicate?
  "Does VECTOR contain more than one ITEM?"
  [vector item]
  (> (count (filterv #(= item %) vector)) 1))

(defn get-duplicates
  "Returns all 'duplicate' (equivalent) terms."
  [data]
  (filterv #(duplicate? (map first data) (first %)) data))

(defn- get-pduplicate
  "Returns vector: [ITEM A] where A is all possible 'duplicates' (terms that
  contains the ITEM substring) found in COLL."
  [coll item]
  [item (filter
         #(and (g/substring? item %) (not (= item %))) coll)])

(defn get-pduplicates
  "Returns a subset of S1 terms that are possible dupilictes of S2
  terms. S1 terms are the possible substrings while S2 is the base
  collection. This search is not commutative (unless s1 = s2)."
  [s1 s2]
  (remove #(empty? (second %))
          (for [term s1]
            (get-pduplicate s2 term))))

(defn get-words
  "Returns a set of words that can be found in the ITEM term/cq
  and not found in the set of ENGlish words."
  [eng item]
  (clojure.set/difference (into #{} (clojure.string/split item #"\s")) eng))

(defn- get-pwordduplicate
  "Returns vector: [ITEM A] where A is all possible 'word
  duplicates' (terms that contain the ITEM substring but are not
  ENGlish words) found in COLL."
  [eng coll item]
  (let [words (get-words eng item)]
    [item (clojure.set/union
           (for [word words]
             (filter (partial g/substring? word) coll)))]))

;; (defn get-pwordduplicates
;;   "Returns a list of terms that have possible word dupilictes. Should
;;   remove terms that are of length 1 as they were already checked in
;;   pduplicates."
;;   [eng set]
;;   (let [subset (filter
;;                 #(> (count (get-words eng %)) 1)
;;                 set)]
;;     (remove #(empty? (second %))
;;             (for [term subset]
;;               (get-pwordduplicate eng set term)))))

(defn get-pwordduplicates
  "Returns a subset of S1 terms/cqs that are possible word duplicates
of S2 terms. S1 terms (split into words) are the possible substrings
while S2 is the base collection. This search is not commutative, due
to the removal of S1 terms of length <= 1 (unless s1 == s2). We remove
the terms that are of length 1 as they are (alreadyy) checked in
pduplictaes."
  [eng s1 s2]
  (let [subset (filter
                #(> (count (clojure.string/split % #"\s")) 1)
                s1)]
    (remove #(empty? (second %))
            (for [term subset]
              (get-pwordduplicate eng s2 term)))))

(defn- get-acronym
  "Returns 'acronym' of given TERM i.e. first letter of each word in
TERM."
  [term]
  (clojure.string/join
   (map first (clojure.string/split term #"\s"))))

(defn- get-pacronym
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

;; DNA Substitution
;; REF http://www.hgmd.cf.ac.uk/docs/mut_nom.html
(defn- dna-mutation? [str]
  "Does STR contain dna-mutation pattern?"
  (re-find #"[acgt]>[acgt]" str))

(defn get-pdmutations
  "Returns all possible 'dna mutation' terms."
  [set]
  (filterv dna-mutation? set))

;; Protein Substitution
;; REF http://www.hgmd.cf.ac.uk/docs/mut_nom.html
(defn- protein-mutation? [str]
  "Does STR contain protein-mutation pattern?"
  (re-find
   #"[gpavlimcfywhkrqnedst]\d+[gpavlimcfywhkrqnedst]"
   str))

(defn get-ppmutations
  "Returns all possible 'protein mutation' terms."
  [set]
  (filterv protein-mutation? set))

;; TODO -- think this through a bit
(defn- without-punct
  [term]
  (clojure.string/replace
   (clojure.string/replace term #"[\_\/]" " ")
   #"[^a-z\s]" ""))

;; ;; without-punct tests
;; (let [tests ["hello" "hello world"
;;              "hello_world" "hello/world"
;;              "hello-world" "hello'world"
;;              "hello\"" "hello?" "hello>world"
;;              "(hello)" "[hello]"]]
;;   (doseq [test tests]
;;     (try
;;       (println (str test ": " (without-punct test)))
;;       (catch Exception e (println (str "error: " test))))
;;     ))

;; MAIN
(defn driver
  []
  ;; read files and create sets
  (let [cfiles (map #(str "Paper" % "_terms.txt") (range 1 31))
        cterms (for [f cfiles]
                 (g/get-lines
                  (g/get-resource
                   (str "./input/Terms/" f))))
        allterms (apply clojure.set/union cterms)
        data (map
              #(clojure.string/split
                %
                #"\s=>\spaper:\spaper|\sline:\s|\spos:\s")
              allterms)
        cmap (apply merge (map #(sorted-map (first %) (into [] (rest %)))
                               (sort-by #(read-string (second %))
                                        #(compare %2 %1)
                                        data)))
        capture (into #{} (keys cmap))

        ofiles (rest (file-seq (clojure.java.io/file "./output/omim")))
        oterms (for [f ofiles]
               (g/get-lines f))
        omim (into #{} (apply clojure.set/union oterms))

        english (into #{} (g/get-lines "./output/cenglish.txt"))
        filtered (clojure.set/difference omim english)
        disease (clojure.set/intersection capture filtered)
        related (into #{} (fuzzy 33 capture filtered))

        refined (clojure.set/union disease related)
        refined_full (select-keys cmap refined)
        nrefined (for [i (range 0 100)]
                   (rand-nth (seq refined)))
        quarantined (clojure.set/difference capture refined)
        quarantined_full (select-keys cmap quarantined)
        nquarantined (for [i (range 0 100)]
                       (rand-nth (seq quarantined)))

        duplicate_full (sort-by first (get-duplicates data))
        duplicate (map first (get-duplicates data))

        pduplicate (get-pduplicates capture capture)
        pwordduplicate (get-pwordduplicates english capture capture)
        pacronym (get-pacronyms capture)
        pdmutation (get-pdmutations capture)
        ppmutation (get-ppmutations capture)

        cq (into #{} (map clojure.string/lower-case
                          (g/get-lines
                           (g/get-resource "./input/cq.txt"))))
        refined_cq (fuzzy 10 cq filtered)
        quarantined_cq (clojure.set/difference cq refined_cq)

        pcqwordterm (get-pwordduplicates english cq capture)

        outfile "refine_results.txt"
        ]

    ;; output stats
    ;; cterms -- includes redundant terms
    (let [name "cterms_results.txt"
          outfile (str "./output/stats/" name)
          error (str "Error: output stats to " name)]
      ;; clear old file
      (g/output outfile "" false error)

      ;; number of terms per paper
      (g/output outfile
                (clojure.string/join "\n" (map count cterms))
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

    ;; output stats
    ;; oterms -- includes redundant terms
    (let [name "oterms_results.txt"
          outfile (str "./output/stats/" name)
          error (str "Error: output stats to " name)]
      ;; clear old file
      (g/output outfile "" false error)

      ;; number of terms per paper
      (g/output outfile
                (clojure.string/join "\n" (map count oterms))
                true error))

    ;; clear old file
    (g/output (str "./output/stats/" outfile)
              ""
              false
              (str "Error: output stats to " outfile))

    ;; save terms
    (let [coll [
                allterms
                duplicate duplicate_full
                cmap capture
                omim english filtered disease related
                refined refined_full nrefined
                quarantined quarantined_full nquarantined
                pduplicate pwordduplicate pacronym
                ppmutation pdmutation
                ]
          name [
                "allterms"
                "duplicate" "duplicate_full"
                "cmap" "capture"
                "omim" "english" "filtered" "disease" "related"
                "refined" "refined_full" "nrefined"
                "quarantined" "quarantined_full" "nquarantined"
                "pduplicate" "pwordduplicate" "pacronym"
                "ppmutation" "pdmutation"
                ]
          ]
      (doseq [i (range 0 (count coll))]
        (let [n (get name i)
              file (str "./output/terms/" n ".txt") ;; TODO make sure this exists
              error (str "Error: output stats to " n ".txt")
              s (get coll i)]

          ;; print number of terms for each coll
          (g/output (str "./output/stats/" outfile)
                    (str "Total number of " n " terms: " (count s) "\n")
                    true
                    (str "Error: output stats to " outfile))

          ;; clear old file
          (g/output file "" false error)

          ;; save term coll to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))

    ;; save cqs
    (let [set [
               cq refined_cq quarantined_cq
               pcqwordterm
               ]
          name [
                "cq" "refined_cq" "quarantined_cq"
                "pcqwordterm"
                ]]
      (doseq [i (range 0 (count set))]
        (let [n (get name i)
              file (str "./output/cqs/" n ".txt") ;; TODO make sure exists
              error (str "Error: output stats to " n ".txt")
              s (get set i)]

          ;; print number of cqs for each set
          (g/output (str "./output/stats/" outfile)
                    (str "Total number of " n "s: " (count s) "\n")
                    true
                    (str "Error: output stats to " outfile))

          ;; clear old file
          (g/output file "" false error)

          ;; save cq set to file
          (g/output file
                    (clojure.string/join "\n" s)
                    true error))))
    ))