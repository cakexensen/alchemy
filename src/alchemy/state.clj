(ns alchemy.state)

(defn new-state
  "creates a new game state"
  []
  {; control and directing
   :director nil ; director is responsible for state updates
   :continue? true ; set false to end the program
   ; temporary! fields for testing
   :temp-direction 0
   ; global collections
   :entities []
   :space nil
   ; timing fields:
   :time 0 ; time at which this state was computed
   :ticks-per-second 60
   :frames-per-second 60})
