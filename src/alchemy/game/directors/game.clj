(ns alchemy.game.directors.game
  (:require [alchemy.game.directors.core :as core]
            [alchemy.entity :as entity]))

(defn simple-test-rotate
  "test! simple rotation around origin"
  [[x y] angle]
  ; yoinked from here http://stackoverflow.com/a/12161405/1404338
  (let [new-x (- (* x (Math/cos angle))
                 (* y (Math/sin angle)))
        new-y (+ (* x (Math/sin angle))
                 (* y (Math/cos angle)))]
    [new-x new-y]))

(defn rotate-entity
  "test! simple rotation of entity vertices"
  [entity]
  (let [vertices (:vertices entity)
        vertices (map #(simple-test-rotate % 0.01) vertices)
        entity (assoc entity :vertices vertices)]
    entity))

(defn director
  "game director: plays the game, moves entities, handles logic"
  [state]
  (let [entities (:entities state)
        entities (map rotate-entity entities)
        state (assoc state :entities entities)]
    state))

(defmethod core/load-director director
  [state]
  ; !! testing with opengl primatives before making real entities
  (let [triangle (entity/new-triangle :triangle [0 0.5] [0.5 -0.5] [-0.5 -0.5])
        state (assoc state :entities [triangle])]
    state))
