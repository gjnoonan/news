(ns api.secure
  (:require [api.core :as api]
            [infrastructure.persistence :as db]
            [compojure.core :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [cemerick.friend [workflows :as workflows] [credentials :as creds]]
            [cemerick.friend :as friend]
            [compojure.route :as route]))

(defn user [id]
  "Looks up a user by id and transforms it to a friend auth map
   with 'user' role"
  (let [usr (db/get-user-by-id id)]
    (hash-map :username (:id usr), :password (:password usr), :roles #{::user})))

(defroutes app-routes
           api/api-routes
           (GET "/news" [] "<html><head><title>News</title></head><body><div id=\"root\"></div><script src=\"js/main.js\"></script><script>news.core.start()</script></body></html>")
           (friend/logout (POST "/logout" [] "Logged out")))

(defroutes public-routes
           (GET "/login" [] "<form action=\"login\" method=\"post\"><input type=\"text\" name=\"username\"/><input type=\"text\" name=\"password\"/><button>Login</button></form>"))


(defroutes static-routes
           (route/resources "/public"))

(defroutes secured-app-routes
           (-> (wrap-routes app-routes
                            friend/wrap-authorize
                            #{::user})
               (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn user)
                                     :workflows     [(workflows/interactive-form)]})
               wrap-session
               wrap-params
               wrap-keyword-params))

(defn- wrap-root [handler]
  (fn [req]
    (handler
      (update-in req [:uri]
                 #(if (= "/" %) "/index.html" %)))))

(def app (->
           (routes
             static-routes
             public-routes
             (wrap-defaults secured-app-routes
                            (assoc-in site-defaults [:security :anti-forgery] false))
             (route/not-found "Not Found"))
           wrap-root
           wrap-json-response))

(defn -main [& args]
  (db/init-db)
  (run-jetty #'app {:port 8080}))


