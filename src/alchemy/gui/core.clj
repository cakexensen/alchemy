(ns alchemy.gui.core
  (:import [org.lwjgl.opengl Display DisplayMode]))

(defn run-gui
  "runs a lwjgl window application and renders the state"
  [shared-state]
  (Display/setDisplayMode (DisplayMode. 800 600))
  (Display/create)
  (loop [state @shared-state]
    (Display/sync (:frames-per-second state))
    (Display/update)
    (when (not (Display/isCloseRequested))
      (recur @shared-state)))
  (Display/destroy))
