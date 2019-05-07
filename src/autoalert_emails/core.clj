(ns autoalert-emails.core
  (:require [clojure.java.io :as io])
  (:require [clojure.spec.alpha :as alpha])
  (:require [clojure.string :as string])
  (:gen-class))

(defn get-text [file-path]
  (slurp file-path))

(defn get-text-emails [file-path]
  (let [text (get-text file-path)]
  (re-find #"(\S+\@\S+)" text)))

(defstruct ingraph :file-name :text :emails)

(defn create-ingraph [file]
  (let [file-name (.toString file)
        text (get-text file)
        emails (get-text-emails file)]
    (struct ingraph file-name text emails)))

(defn replace-path [str]
  (string/replace str "/Users/ccrowe/Documents/ingraphs/inmail/" ""))

(defn ingraph-text [ingraph]
  (str (:file-name ingraph) (:emails ingraph) "\n"))

(defn -main
  [& args]
  (let [directory "/Users/ccrowe/Documents/ingraphs/inmail"
        d (io/file directory)
        fs (filter #(.isFile %) (file-seq d))
        ingraphs (map #(create-ingraph %) fs)
        as-text (map replace-path (map ingraph-text ingraphs))]
    (println as-text)
  ))
