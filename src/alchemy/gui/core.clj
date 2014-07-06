(ns alchemy.gui.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL15 GL20 GL30 PixelFormat ContextAttribs]
           [java.nio FloatBuffer]
           [org.lwjgl BufferUtils]))

(defmacro with-vao
  "performs actions while a particular vertex array is loaded"
  [vao-id & body]
  `(let [_# (GL30/glBindVertexArray ~vao-id) ; bind array
         result# (do ~@body)]
     (GL30/glBindVertexArray 0) ; unbind array
     result#))

(defmacro with-vbo
  "performs actions while a particular vertex buffer is loaded"
  [vbo-id & body]
  `(let [_# (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER ~vbo-id) ; bind buffer
         result# (do ~@body)]
     (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER 0) ; unbind buffer
     result#))

(defmacro with-attrib
  "performs actions while a particular vertex array attribute is loaded"
  [index & body]
  `(let [_# (GL20/glEnableVertexAttribArray ~index) ; select attrib
         result# (do ~@body)]
     (GL20/glDisableVertexAttribArray ~index) ; deselect attrib
     result#))

(defn vertices-to-buffer
  "converts vertex data to buffer usable by vbo"
  [vertices]
  (let [vert-array (map float (flatten vertices)) ; force to float array
        buff (BufferUtils/createFloatBuffer (count vert-array))]
    ; add each vertex part to the buffer
    (doseq [va vert-array] (.put buff va))
    ; flip it because i guess you're supposed to
    (.flip buff)
    buff))

(defn add-vertices-to-buffer
  "adds vertices (pairs of vertex coords) to a buffer"
  [vertices]
  (GL15/glBufferData GL15/GL_ARRAY_BUFFER
                     (vertices-to-buffer vertices)
                     GL15/GL_STREAM_DRAW))

(defn update-vertices-in-buffer
  "updates vertices (pairs of vertex coords) in a buffer"
  [vertices]
  (GL15/glBufferSubData GL15/GL_ARRAY_BUFFER
                        0 ; index?
                        (vertices-to-buffer vertices)))

(defn add-buffer-to-vertex-attributes
  "adds a vbo to a vao's attributes"
  [] ; might need to parameterize index, size, type, etc
  (let [index 0
        size 3
        type GL11/GL_FLOAT
        ; not sure what the other params are, tutorial uses them
        ]
    (GL20/glVertexAttribPointer index size type false 0 0)))

(defn add-buffer
  "adds an entity buffer"
  [buffers entity]
  (let [vao-id (GL30/glGenVertexArrays)]
    (with-vao vao-id
      (let [vbo-id (GL15/glGenBuffers)]
        (with-vbo vbo-id
          (add-vertices-to-buffer (:vertices entity))
          (add-buffer-to-vertex-attributes))
        ; add buffer meta data for the entity
        (assoc buffers (:id entity) {:type (:type entity)
                                     :vao-id vao-id
                                     :vbo-id vbo-id})))))

(defn update-buffer
  "updates an entity buffer"
  [buffers entity]
  ; if we have valid buffers and entity
  (when (and (seq buffers) entity)
    (let [buffer (buffers (:id entity))]
      (with-vbo (:vbo-id buffer)
        (update-vertices-in-buffer (:vertices entity))))))

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
        buffer-keys (keys buffers)
        ; remove missing entities from buffers
        removed-ids (filter #(not-contains? (map :id entities) %) buffer-keys)
        buffers (reduce remove-buffer buffers removed-ids)
        ; update existing buffers that remain
        _ (doseq [entity entities] (update-buffer buffers entity))
        ; add new entities to buffers
        new-entities (filter #(not-contains? buffer-keys (:id %)) entities)
        buffers (reduce add-buffer buffers new-entities)]
    buffers))

(def vertex-renderers
  {:triangle #(GL11/glDrawArrays GL11/GL_TRIANGLES 0 3)})

(defn render
  "renders the game state"
  [state buffers]
  (doseq [[key buffer] buffers]
    (with-vao (:vao-id buffer)
      (with-attrib 0
        ; draw the vertices
        ((vertex-renderers (:type buffer)))))))

(defn setup-display
  "sets up the lwjgl/opengl display"
  [width height [color-red color-green color-blue]]
  (let [pixel (PixelFormat.)
        ; specify which opengl version to use
        context (. (ContextAttribs. 3 2)
                   (withForwardCompatible true)
                   (withProfileCore true))]
    ; create window display
    (Display/setDisplayMode (DisplayMode. width height))
    (Display/create pixel context))
  ; set background color
  (GL11/glClearColor color-red color-green color-blue 0)
  ; set viewport (change this for scaling?)
  (GL11/glViewport 0 0 width height))

(defn await-frame
  "waits until the next frame should be processed"
  [state]
  (Display/sync (:frames-per-second state)))

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

(defn run-gui
  "runs a lwjgl window application and renders the state"
  [shared-state]
  (setup-display 800 600 [0 0 0])
  ; loop for each frame using the state and gl buffers
  (loop [state @shared-state
         buffers {}]
    (await-frame state)
    (clear-screen)
    (let [buffers (manage-buffers state buffers)]
      (render state buffers)
      (update-display)
      (when-not (display-closed?)
        (recur @shared-state buffers))))
  (close-display))
