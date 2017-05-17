(ns clj-http-playground.core
  (:require [clj-http.client :as http]
            [clj-http.cookies :as cookies])
  (:import [org.jsoup Jsoup]))

(let [cookie-store (cookies/cookie-store)
      login-page (http/get "https://darksky.net/dev/login"
                           {:cookie-store cookie-store})
      login-doc (-> login-page
                    :body
                    Jsoup/parse)]
  (:status login-page))

(def response (http/get "https://api.darksky.net/forecast/dc6265a401305dd6da2db3db0e014bd6/37.8267,-122.4233" {:as :json}))

(:status response)
(:server (:headers response))
(:body response)
