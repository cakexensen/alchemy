(ns alchemy.gui.core
  (:require [alchemy.gui.lwjgl.display :as display]
            [alchemy.gui.lwjgl.render :as render]
            [alchemy.gui.lwjgl.buffer :as buffer]
            [alchemy.message :as message]))

; copied this from game, consider combining the similarities somehow
(defn process-messages
  "processes any received messages"
  [data mailbox]
  (loop [data data
         messages (message/receive mailbox)]
    (if (empty? messages)
      data
      ; process first message
      (let [message (first messages)
            ; update data based on message
            data (case message
                   :close (assoc data :continue? false)
                   data)]
        (recur data (rest messages))))))

(defn run-gui
  "runs a lwjgl window application and renders the state"
  [shared-state mailbox]
  (display/setup-display 800 600 [0 0 0])
  ; loop for each frame using the state and gl buffers
  (loop [state @shared-state
         data {:continue? true}
         buffers {}]
    (display/await-frame state)
    (display/clear-screen)
    (let [data (process-messages data mailbox)
          buffers (buffer/manage-buffers state buffers)]
      (render/render state buffers)
      (display/update-display)
      (if (and (not (display/display-closed?))
               (:continue? data))
        (recur @shared-state data buffers)
        (message/send mailbox :game :close))))
  (display/close-display))
