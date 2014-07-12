(ns alchemy.gui.lwjgl.vertex
  (:import [org.lwjgl.opengl GL11 GL15 GL20 GL30]
           [org.lwjgl BufferUtils]
           [java.nio FloatBuffer]))

;; withs used to encapsulate actions which require pre and post actions
;; usage:
;;   (with-vao vao-id
;;     (do stuff here with vao at vao-id index))
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

;; gen fns generate new vertex ids
(defn gen-vao-id
  "generates a new vertex array id"
  []
  (GL30/glGenVertexArrays))

(defn gen-vbo-id
  "generates a new vertex buffer id"
  []
  (GL15/glGenBuffers))

;; buffer fns are used to load data into a vertex buffer
;; be cautious of the similarly named "buffers" used to transfer the data
(defn vertices-to-buffer
  "converts vertex data to buffer usable by vbo"
  [vertices]
  (let [vert-array (map float (flatten vertices)) ; force to float array
        buff (BufferUtils/createFloatBuffer (count vert-array))]
    ;; add each vertex part to the buffer
    (doseq [va vert-array] (.put buff va))
    ;; flip it because i guess you're supposed to
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
        ;; not sure what the other params are, tutorial uses them
        ]
    (GL20/glVertexAttribPointer index size type false 0 0)))

