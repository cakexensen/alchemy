(ns alchemy.entity)

;; !! before making entity tools, test with simple opengl primatives

(defn new-triangle
  "creates a new triangle"
  [position]
  ;; store triangle vertices
  {:id (gensym)
   :type :triangle
   :position position
   :angle 0})

(defn position
  "gets the position of an entity at a given time"
  [entity time]
  ;; call the position fn with the current time
  ((:position entity) time))
