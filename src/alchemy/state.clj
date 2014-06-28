(ns alchemy.state)

(defn new-state
  "creates a new game state"
  []
  {:entities []
   :space nil
   :time 0
   :ticks-per-second 60
   :frames-per-second 60})
