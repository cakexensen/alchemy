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

(defn rotate-fn
  "creates a rotation position fn"
  [position base-time angle]
  (fn [time]
    (let [;; determine how much to rotate given the time
          time-delta (- time base-time)
          angle-delta (* time-delta angle)
          ;; rotate the vertices of the position
          new-position (map #(simple-test-rotate % angle-delta) position)]
     new-position)))

(defn rotate-entity
  "rotates an entity after changing its angle"
  [entity angle-delta time]
  (let [entity (update-in entity [:angle] angle-delta) ; update the angle
        angle (:angle entity) ; get the new angle
        position (entity/position entity time) ; get the current position
        ;; store the new position fn
        entity (assoc entity :position
                      (rotate-fn position time angle))]
    entity))

;; !! test rotation rate fns: these are a little sloppy
;; but i need something more like this for describing rotation speed
(defn rotations-second
  "converts rotations-per-second to appropriately sized number"
  [rotations]
  (/ rotations 1000/6))

(defn seconds-rotation
  "converts seconds-per-rotation to appropriately sized number"
  [seconds]
  (rotations-second (/ 1 seconds)))

(defn process-inputs
  "applies appropriate state changes based on the inputs"
  [state inputs]
  (if (empty? inputs)
    (assoc state :inputs []) ; clear input buffer
    (let [input (first inputs)
          pressed? (:pressed? input)
          direction (if pressed? + -)
          time (:time state)
          ;; !! test: just get the id of our only triangle entity for now
          id (-> state :entities keys first)
          tri-id [:entities id]
          state (case (:key input)
                  :key-left (update-in state tri-id
                                       rotate-entity #(direction % (seconds-rotation 5)) time)
                  :key-right (update-in state tri-id
                                        rotate-entity #(direction % (seconds-rotation -5)) time)
                  state)]
      (recur state (rest inputs)))))

(defn director
  "game director: plays the game, moves entities, handles logic"
  [state]
  (let [inputs (:inputs state)
        state (process-inputs state inputs)]
    state))

(defmethod core/load-director director
  [state]
  ;; !! testing with opengl primatives before making real entities
  (let [triangle (entity/new-triangle
                  ;; start with a constant position
                  (fn [time] [[0 0.5 0] [0.5 -0.5 0] [-0.5 -0.5 0]]))
        state (assoc-in state [:entities (:id triangle)] triangle)]
    state))
