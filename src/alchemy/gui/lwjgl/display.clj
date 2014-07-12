(ns alchemy.gui.lwjgl.display
  (:import [org.lwjgl.opengl Display DisplayMode PixelFormat ContextAttribs GL11]))

(defn setup-display
  "sets up the lwjgl/opengl display"
  [width height [color-red color-green color-blue]]
  (let [pixel (PixelFormat.)
        ;; specify which opengl version to use
        context (. (ContextAttribs. 3 2)
                   (withForwardCompatible true)
                   (withProfileCore true))]
    ;; create window display
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/create pixel context))
  ;; set background color
  (GL11/glClearColor color-red color-green color-blue 0)
  ;; set viewport (change this for scaling?)
  (GL11/glViewport 0 0 width height))

(defn await-frame
  "waits until the next frame should be processed"
  [state]
  (Display/sync (get state :frames-per-second 60)))

(defn clear-screen
  "clears the screen in preparation of the next rendering"
  []
  ;; clear screen - get bg color from state?
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT))

(defn update-display
  "updates the display with the rendered graphics"
  []
  (Display/update))

(defn display-closed?
  "indicates if the display has been closed"
  []
  (Display/isCloseRequested))

(defn close-display
  "closes the display and cleans up its resources"
  []
  (Display/destroy))
