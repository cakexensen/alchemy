(ns alchemy.state)

(defn new-state
  "creates a new game state"
  [director]
  {; control and directing
   :director director ; director is responsible for state updates
   ; global collections
   :entities []
   :space nil
   ; timing fields:
   :time 0 ; time at which this state was computed
   :ticks-per-second 60
   :frames-per-second 60})
