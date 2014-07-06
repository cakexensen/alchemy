(ns alchemy.gui.lwjgl.render
  (:import [org.lwjgl.opengl GL11])
  (:require [alchemy.gui.lwjgl.vertex :as vertex]))

(def vertex-renderers
  {:triangle #(GL11/glDrawArrays GL11/GL_TRIANGLES 0 3)})

(defn render
  "renders the game state"
  [state buffers]
  (doseq [[key buffer] buffers]
    (vertex/with-vao (:vao-id buffer)
      (vertex/with-attrib 0
        ; draw the vertices
        ((vertex-renderers (:type buffer)))))))
