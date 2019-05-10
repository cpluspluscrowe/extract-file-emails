(ns autoalert-emails.core
  (:require [clojure.java.io :as io])
  (:require [clojure.spec.alpha :as alpha])
  (:require [clojure.string :as string])
  (:gen-class))

(defn get-text [file-path]
  (let [answer (deref (future (slurp file-path)) 100 :timeout)]
    (if (= answer :timeout) "" answer)
    ))

(defn get-text-emails [file-path]
  (let [text (get-text file-path)
        f (future (re-find #"(\S+\@\S+)" text))
        answer (deref f 100 :timeout)]
    (if (= answer :timeout) "" answer)
    ))

(defn get-line-count [file-path]
  (let [text (get-text file-path)
        lines (string/split text #"\n")]
    (count lines)))

(defstruct ingraph :file-name :emails)

(defn create-ingraph [file]
  (let [file-name (.toString file)
        emails (get-text-emails file)]
    (struct ingraph file-name emails)))

(def directory "some-path-to-files")

(defn replace-path [path]
  (string/replace path (str directory "/") ""))

(defn ingraph-text [ingraph]
  (str (:file-name ingraph) (:emails ingraph) "\n"))

(defn has-email [ingraph]
  (let [email (:emails ingraph)]
    (if (= email :timeout) nil email)
    ))

(defn -main
  [& args]
  (let [d (io/file directory)
        fs (filter #(.isFile %) (file-seq d))
        ingraphs (doall (pmap #(create-ingraph %) fs))
        with-emails (filter has-email ingraphs)
        as-text (map replace-path (map ingraph-text with-emails))]
    (println (count as-text))
    (println as-text)
  ))

                                        ; something I was playing with that didn't pan out
                                        ;(defn kill-slow-futures [f]
                                        ;  (Thread/sleep 1)
                                        ;  (if (not (future-done? f))
                                        ;    (do
                                        ;      (println "Cancelling future")
                                        ;    (future-cancel f))))
