(ns alchemy.entity)

; !! before making entity tools, test with simple opengl primatives

(defn new-triangle
  "creates a new triangle"
  [[v1x v1y] [v2x v2y] [v3x v3y]]
  ; store triangle vertices
  {:id (gensym)
   :type :triangle
   :vertices [[v1x v1y 0]
              [v2x v2y 0]
              [v3x v3y 0]]})
