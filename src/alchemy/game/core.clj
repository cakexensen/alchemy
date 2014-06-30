(ns alchemy.game.core
  (:use [alchemy.state]))

(defn get-time
  "gets the current time"
  []
  (.getTime (java.util.Date.)))

(defn process
  "processes the next state"
  [state]
  (let [; update the computation timestamp on the state
        state (assoc state :time (get-time))
        ; get the director responsible for updating the state
        director (:director state)
        ; process the state
        state (director state)]
    state))

(defn await-tick
  "waits until the next tick should be processed"
  [state]
  ; wait-time = state-time + tick-delta - current-time
  (let [state-time (:time state)
        current-time (get-time)
        ticks-per-second (:ticks-per-second state)
        ; tick-delta: 1000 milliseconds / ticks-per-second
        tick-delta (/ 1000 ticks-per-second)
        next-tick (+ state-time tick-delta)
        wait-time (- next-tick current-time)
        wait-time (Math/max (double wait-time) (double 0))]
    (Thread/sleep wait-time)))

(defn run-game
  "continuously runs the game logic"
  [shared-state]
  (loop [state @shared-state]
    (let [next-state (process state)]
      ; update shared-state
      (reset! shared-state next-state)
      ; wait until next tick before recurring
      (await-tick next-state)
      (recur next-state))))
