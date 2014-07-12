(ns alchemy.core
  (:require [alchemy.game.core :as game]
            [alchemy.gui.core :as gui]
            [alchemy.message :as message]))

(defn -main
  "starts the game"
  [& args]
  (let [;; initialize data shared by the engine processes
        message-system (message/message-system)]
    ;; run the engine processes
    (future (game/run-game (message/mailbox message-system :game)))
    (gui/run-gui (message/mailbox message-system :gui))
    (shutdown-agents)))
