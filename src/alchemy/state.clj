(ns alchemy.state)

(defn new-state
  "creates a new game state"
  []
  {:entities []
   :space nil
   :ticks-per-second 60
   :frames-per-second 60})
