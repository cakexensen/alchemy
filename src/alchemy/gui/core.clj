(ns alchemy.gui.core
  (:require [alchemy.gui.lwjgl.display :as display]
            [alchemy.gui.lwjgl.render :as render]
            [alchemy.gui.lwjgl.buffer :as buffer]))

(defn run-gui
  "runs a lwjgl window application and renders the state"
  [shared-state]
  (display/setup-display 800 600 [0 0 0])
  ; loop for each frame using the state and gl buffers
  (loop [state @shared-state
         buffers {}]
    (display/await-frame state)
    (display/clear-screen)
    (let [buffers (buffer/manage-buffers state buffers)]
      (render/render state buffers)
      (display/update-display)
      (when-not (display/display-closed?)
        (recur @shared-state buffers))))
  (display/close-display))
