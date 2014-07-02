(ns alchemy.gui.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL15 GL20 GL30 PixelFormat ContextAttribs]
           [java.nio FloatBuffer]
           [org.lwjgl BufferUtils]))

(defn add-buffer
  "adds an entity buffer"
  [buffers entity]
  (let [; set up array
        vao-id (GL30/glGenVertexArrays)
        _ (GL30/glBindVertexArray vao-id)
        ; set up buffer
        vbo-id (GL15/glGenBuffers)
        _ (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo-id)
        entity-vertices (map float (flatten (:vertices entity)))
        vertices (BufferUtils/createFloatBuffer (count entity-vertices))
        _ (doseq [ev entity-vertices] (.put vertices ev))
        _ (.flip vertices)
        _ (GL15/glBufferData GL15/GL_ARRAY_BUFFER
                             vertices
                             GL15/GL_STREAM_DRAW)
        ; put buffer into array attributes
        index 0 ; first slot
        size 3 ; vertex components per point - calculate later?
        type GL11/GL_FLOAT ; type of vertex values
        ; other params are unknown to me at this moment, just following tut
        _ (GL20/glVertexAttribPointer index size type false 0 0)
        ; deselect buffer and array
        _ (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0)
        _ (GL30/glBindVertexArray 0)
        buffer {:type (:type entity)
                :vbo-id vbo-id
                :vao-id vao-id}]
    ; add buffer for entity
    (assoc buffers (:id entity) buffer)))

(defn update-buffer
  "updates an entity buffer"
  [buffers entity]
  (when (and (seq buffers)
             entity)
    (let [buffer (buffers (:id entity))]
      ; bind buffer
      (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER (:vbo-id buffer))
      ; update vertices in buffer
      (let [entity-vertices (map float (flatten (:vertices entity)))
            length (count entity-vertices)
            vertices (BufferUtils/createFloatBuffer length)]
        (doseq [ev entity-vertices] (.put vertices ev))
        (.flip vertices)
        (GL15/glBufferSubData GL15/GL_ARRAY_BUFFER 0 vertices))
      ; unbind buffer
      (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0))))

(defn remove-buffer
  "removes an entity buffer"
  [buffers id]
  (let [buffer (buffers id)
        vbo-id (:vbo-id buffer)
        vao-id (:vao-id buffer)]
    ; disable buffer index from array attrib list
    (GL20/glDisableVertexAttribArray 0)
    ; delete buffer
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0)
    (GL15/glDeleteBuffers vbo-id)
    ; delete array
    (GL30/glBindVertexArray 0)
    (GL30/glDeleteVertexArrays vao-id)
    ; remove from buffers
    (dissoc buffers id)))

(defn not-contains?
  "fixed not contains? for lazy and key seqs"
  [col x]
  (not-any? #(= x %) col))

(defn manage-buffers
  "manages opengl vertex array/buffer objects"
  [state buffers]
  (let [entities (:entities state)
        ; remove missing entities from buffers
        removed-ids (filter #(not-contains? (map :id entities) %) (keys buffers))
        buffers (reduce remove-buffer buffers removed-ids)
        ; update existing buffers that remain
        _ (doseq [entity entities] (update-buffer buffers entity))
        ; add new entities to buffers
        new-entities (filter #(not-contains? (keys buffers) (:id %)) entities)
        buffers (reduce add-buffer buffers new-entities)]
    buffers))

(def vertex-renderers
  {:triangle #(GL11/glDrawArrays GL11/GL_TRIANGLES 0 3)})

(defn render
  "renders the game state"
  [state buffers]
  (doseq [[key buffer] buffers]
    ; bind the array
    (GL30/glBindVertexArray (:vao-id buffer))
    (GL20/glEnableVertexAttribArray 0)
    ; draw the vertices
    ((vertex-renderers (:type buffer)))
    ; deselect
    (GL20/glDisableVertexAttribArray 0)
    (GL30/glBindVertexArray 0)))

(defn run-gui
  "runs a lwjgl window application and renders the state"
  [shared-state]
  ; set up the display
  (let [pixel (PixelFormat.)
        context (. (ContextAttribs. 3 2)
                   (withForwardCompatible true)
                   (withProfileCore true))]
    (Display/setDisplayMode (DisplayMode. 800 600))
    (Display/create pixel context))
  (GL11/glClearColor 0.4 0.6 0.9 0)
  (GL11/glViewport 0 0 800 600)
  ; loop for each frame
  (loop [state @shared-state
         buffers {}]
    (Display/sync (:frames-per-second state))
    ;; clear screen - get bg color from state?
    (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
    (let [buffers (manage-buffers state buffers)]
      (render state buffers)
      (Display/update)
      (when (not (Display/isCloseRequested))
        (recur @shared-state buffers))))
  ; clean up before quitting
  (Display/destroy))
