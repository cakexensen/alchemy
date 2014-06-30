(ns alchemy.entity)

; !! before making entity tools, test with simple opengl primatives

(defn new-triangle
  "creates a new triangle"
  [id [v1x v1y] [v2x v2y] [v3x v3y]]
  ; store triangle vertices
  {:id id
   :vertices [[v1x v1y]
              [v2x v2y]
              [v3x v3y]]})
