(ns clj-http-playground.core
  (:require [clj-http.client :as http]
            [clj-http.cookies :as cookies])
  (:import [org.jsoup Jsoup]))

;;(def email "test@example.com")
;;(def password "insert-password-here")

(defn parse-body [page]
  (-> page
      :body
      Jsoup/parse))

(defn parse-csrf [doc]
  (-> doc
      (.select "[name=_csrf]")
      first
      (.attr "value")))

(defn login [cookie-store email password]
  (let [login-page (http/get "https://darksky.net/dev/login"
                             {:cookie-store cookie-store})
        login-doc (parse-body login-page)
        _csrf (parse-csrf login-doc)
        home-page (http/post "https://darksky.net/dev/login"
                        {:force-redirects true
                         :cookie-store cookie-store
                         :form-params {:_csrf _csrf
                                       :email email
                                       :password password}})]
    home-page))

(defn parse-api-key [doc]
  (-> doc
      (.select "[id=api-key]")
      first
      (.attr "value")))

(defn fetch-home-doc [cookie-store email password]
  (let [home-page (login cookie-store email password)
        home-doc (parse-body home-page)]
    home-doc))

(defn fetch-api-key [email password]
  (let [cookie-store (cookies/cookie-store)
        home-doc (fetch-home-doc cookie-store email password)
        api-key (parse-api-key home-doc)]
    api-key))

(defn fetch-reset-key-doc [cookie-store csrf api-key]
  (let [reset-key-page (http/post "https://darksky.net/dev/account/reset/api-key"
                                  {:force-redirects true
                                   :cookie-store cookie-store
                                   :form-params {:_csrf csrf
                                                 :api-key api-key}})
        reset-key-doc (parse-body reset-key-page)]
    reset-key-doc))

(defn reset-api-key [email password]
  (let [cookie-store (cookies/cookie-store)
        home-doc (fetch-home-doc cookie-store  email password)
        csrf (parse-csrf home-doc)
        api-key (parse-api-key home-doc)
        reset-key-doc (fetch-reset-key-doc cookie-store csrf api-key)
        reset-api-key (parse-api-key reset-key-doc)]
    reset-api-key))

;;(reset-api-key email password)
;;(fetch-api-key email password)
