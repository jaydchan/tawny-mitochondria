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
  ncl.mitochondria.graph
  (:use (incanter core stats charts)
        [ncl.mitochondria.generic :only [read-file]])
  ;;  (:require [])
)

(defn- tc-plot
  "Produces (and saves) line chart showing term capture results using given
DATA."
  [name data]
  (let [plot (line-chart
              (keys data)
              (vals data)
              :title "Paper results"
              :x-label "Paper number"
              :y-label "Number of unique terms")]
    ;; (view plot)
    (save plot (str "./output/graphs/term-capture-" name ".png"))))

(defn driver
  "TODO"
  []

  ;; reads in data -- results.txt
  (let [results (read-file "./output/stats/capture_results.txt")
        one (apply merge (map #(sorted-map %1 %2) (range 1 31) results))
        five (apply merge
                    (map #(sorted-map %1 %2)
                         (range 5 36 5)
                         (map #(apply + %) (partition 5 results))))]

    ;; create line chart showing the number of unique terms per paper
    (tc-plot "one" one)

    ;; create line chart showing the number of unique terms per 5 papers
    (tc-plot "five" five)))