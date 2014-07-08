(ns alchemy.core
  (:require [alchemy.state :as state]
            [alchemy.game.core :as game]
            [alchemy.gui.core :as gui]
            [alchemy.message :as message]
            [alchemy.game.directors.core :as dir-core]
            [alchemy.game.directors.game :as dir-game]))

(defn -main
  "starts the game"
  [& args]
  (let [; initialize data shared by the engine processes
        message-system (message/message-system)
        state (dir-core/change-director (state/new-state) dir-game/director)
        shared-state (atom state)]
    ; run the engine processes
    (future (game/run-game shared-state (message/mailbox message-system :game)))
    (gui/run-gui shared-state (message/mailbox message-system :gui))
    (shutdown-agents)))
