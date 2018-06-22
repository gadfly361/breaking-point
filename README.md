# BREAKING-POINT

> " I don’t know; every man has his breaking point."
> - Red, The Shawshank Redemption

BREAKING-POINT lets you quickly define and subscribe to screen breakpoints in
your [re-frame](https://github.com/Day8/re-frame)
application.

Add the following to the :dependencies vector in your project.clj file.

```clojure
[breaking-point "0.1.2"]
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
| :debounce-ms              | int                                   | 0         | no        |

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
(re-frame/subscribe [::bp/screen-width]) ;; will be an int
(re-frame/subscribe [::bp/screen-height]) ;; will be an int
(re-frame/subscribe [::bp/screen]) ;; will be one of the following: :mobile, :tablet, :small-monitor, :large-monitor

(re-frame/subscribe [::bp/orientation]) ;; will be either :portrait or :landscape
(re-frame/subscribe [::bp/landscape?]) ;; true if width is >= height
(re-frame/subscribe [::bp/portrait?]) ;; true if height > width

;; these will be based on the breakpoint names that you provide
(re-frame/subscribe [::bp/mobile?]) ;; true if screen-width is < 768
(re-frame/subscribe [::bp/tablet?]) ;; true if screen-width is >= 768 and < 992
(re-frame/subscribe [::bp/small-monitor?]) ;; true if window width is >= 992 and < 1200
(re-frame/subscribe [::bp/large-monitor?]) ;; true if window width is >= 1200
```

Note, `::bp/set-breakpoints` should only be dispatched **once** when
the application *first* loads.


# Usage

Create a new re-frame application and add the `+breaking-point` option.

```
lein new re-frame foo +breaking-point
```

# Questions

If you have questions, I can usually be found hanging out in
the [clojurians](http://clojurians.net/) #reagent slack channel (my
handle is [@gadfly361](https://twitter.com/gadfly361)).

# License

Copyright © 2018 Matthew Jaoudi

Distributed under the The MIT License (MIT).
