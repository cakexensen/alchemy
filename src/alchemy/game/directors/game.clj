(ns alchemy.game.directors.game
  (:require [alchemy.game.directors.core :as core]
            [alchemy.entity :as entity]))

(defn simple-test-rotate
  "test! simple rotation around origin"
  [[x y z] angle]
  ;; yoinked from here http://stackoverflow.com/a/12161405/1404338
  (let [new-x (- (* x (Math/cos angle))
                 (* y (Math/sin angle)))
        new-y (+ (* x (Math/sin angle))
                 (* y (Math/cos angle)))]
    [new-x new-y z]))

(defn rotate-entity
  "test! simple rotation of entity vertices"
  [entity speed]
  (let [vertices (:vertices entity)
        vertices (map #(simple-test-rotate % speed) vertices)
        entity (assoc entity :vertices vertices)]
    entity))

(defn process-inputs
  "applies appropriate state changes based on the inputs"
  [state inputs]
  (if (empty? inputs)
    state
    (let [input (first inputs)
          pressed? (:pressed? input)
          direction (if pressed? + -)
          state (case (:key input)
                  :key-left (update-in state [:temp-direction] direction 0.01)
                  :key-right (update-in state [:temp-direction] direction -0.01)
                  state)]
      (recur state (rest inputs)))))

(defn director
  "game director: plays the game, moves entities, handles logic"
  [state]
  (let [inputs (:inputs state)
        state (process-inputs state inputs)
        state (assoc state :inputs [])
        entities (:entities state)
        entities (map #(rotate-entity % (:temp-direction state)) entities)
        state (assoc state :entities entities)]
    state))

(defmethod core/load-director director
  [state]
  ;; !! testing with opengl primatives before making real entities
  (let [triangle (entity/new-triangle [0 0.5] [0.5 -0.5] [-0.5 -0.5])
        state (assoc state :entities [triangle])]
    state))
