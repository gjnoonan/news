# About

This project is part of my clojure learning experience.
What's in here is a full-stack, cljs/clojure/redis solution
for a simple user/posts scenario.

Functionality and UI wise, it's as simple as it gets.
I will continue to add to it as I see fit for my purposes.

It uses cemerik.friend for securing the app.

## Prerequisites

You will need Leiningen 2.0.0 or above installed.
https://leiningen.org/

You will also need a Redis server installed and running:
https://redis.io/download

## Running

#### Redis
(OSX) To have launchd start redis now and restart at login:
  brew services start redis
Or, if you don't want/need a background service you can just run:
  redis-server /usr/local/etc/redis.conf

#### App
To start a web server for the application (port 8080), simply run the main
fn in api.secure

## TODO

- make UI friendlier (e.g. only show "Logout" when logged in)
- address usability issues (e.g. login with non-existing user should have a friendly message)
