(ns alchemy.core
  (:require [alchemy.state :as state]
            [alchemy.game.core :as game]
            [alchemy.game.directors.init :as init]))

(defn -main
  "starts the game"
  [& args]
  (let [; initialize data shared by the engine processes
        state (state/new-state init/director)
        shared-state (atom state)]
    ; run the engine processes
    (game/run-game shared-state)))
