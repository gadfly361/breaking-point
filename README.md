# BREAKING-POINT

> " I don’t know; every man has his breaking point."
> - Red, The Shawshank Redemption

BREAKING-POINT lets you quickly name and define screen breakpoints in
your [re-frame](https://github.com/Day8/re-frame)
application. BREAKING-POINT will automatically register several
convenience subscriptions for you based on your breakpoint
definitions.

Add the following to the :dependencies vector in your project.clj file.

```clojure
[breaking-point "0.1.0"]
```

And in your ns:
```clojure
(ns your-ns
  (:require [breaking-point.core :as bp]))
```

![breaking-point gif not found](breaking-point.gif)

# API

## ::bp/set-breakpoints

`::bp/set-breakpoints` takes a hash-map with the following shape:

| key                       | type                                  | default   | required? |
|---------------------------|---------------------------------------|-----------|-----------|
| :breakpoints              | [keyword int keyword int ... keyword] |           | **yes**   |
| :debounce-ms              | init                                  | 0         | no        |

Breakpoints takes a series of keywords alternating with ints that will
be used as breakpoints. Make sure the breakpoints are **ascending** in
value.  Here is an example:

```clojure
(re-frame/dispatch-sync [::bp/set-breakpoints
                         {;; required
                          :breakpoints [:mobile
                                        768
                                        :tablet
                                        992
                                        :small-monitor
                                        1200
                                        :large-monitor]

                          ;; optional
                          :debounce-ms 166
                          }])
```

You can have as many breakpoints as you want and name them
whatever you want, just be sure to alternate keywords with *ascending* ints.

When you disptach `::bp/set-breakpoints`, it will register
subscriptions based on the provided keywords and breakpoints that you
supplied.

```clojure
(rf/subscribe [:bp/screen-width]) ;; will be an int
(rf/subscribe [:bp/screen]) ;; will be one of the following: :mobile, :tablet, :small-monitor, :large-monitor

(rf/subscribe [:bp/mobile?]) ;; true if screen-width is < 768
(rf/subscribe [:bp/tablet?]) ;; true if screen-width is >= 768 and < 992
(rf/subscribe [:bp/small-monitor?]) ;; will be true if window width is >= 992 and < 1200
(rf/subscribe [:bp/large-monitor?]) ;; will be true if window width is >= 1200
```

Note, `::bp/set-breakpoints` should only be dispatched **once** when
the application *first* loads.


# Usage

Create a new re-frame application.

```
lein new re-frame foo
```

Add the following to the `:dependencies` vector of your *project.clj*
file.

```clojure
[breaking-point "0.1.0"]
```

Then require breaking-point in the core namespace, and add the
`::bp/set-breakpoints` event. Note, this needs to be dispatched **only
once**, when the application *first* loads.

```clojure
(ns foo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]

            ;; Add this (1 of 2)
            [breaking-point.core :as bp]

            [foo.events :as events]
            [foo.views :as views]
            [foo.config :as config]
            ))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])

  ;; And this (2 of 2)
  (re-frame/dispatch-sync [::bp/set-breakpoints
                           {;; required
                            :breakpoints [:mobile
                                          768
                                          :tablet
                                          992
                                          :small-monitor
                                          1200
                                          :large-monitor]
  
                            ;; optional
                            :debounce-ms 166
                            }])

  (dev-setup)
  (mount-root))
```

# Questions

If you have questions, I can usually be found hanging out in
the [clojurians](http://clojurians.net/) #reagent slack channel (my
handle is [@gadfly361](https://twitter.com/gadfly361)).

# License

Copyright © 2018 Matthew Jaoudi

Distributed under the The MIT License (MIT).
