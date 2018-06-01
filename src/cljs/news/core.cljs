(ns news.core
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [cognitect.transit :as t]))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something went wrong: " status " " status-text)))

(def json (t/reader :json))

(def feed (r/atom nil))
(def user (r/atom 1))

(defn get-news [user-id]
  (GET (str "/api/news/" user-id)
       {:handler (fn [r] (reset! feed r))
        :error-handler error-handler}))

(defn feed-map [] (t/read json @feed))

(defn news-app []
  (get-news @user)
  (fn [] [:div
          [:ul {:id "news"}
           (for [news (feed-map)] ^{:key (get news "id")} [:li (get news "text")])]]))


(defn ^:export start []
  (r/render-component [news-app]
                      (.getElementById js/document "root")))