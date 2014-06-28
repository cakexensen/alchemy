(ns alchemy.core
  (:use [alchemy.state]
        [alchemy.game.core]))

(defn -main
  "starts the game"
  [& args]
  (let [; initialize data shared by the engine processes
        state (new-state)
        shared-state (atom state)]
    ; run the engine processes
    (run-game shared-state)))
