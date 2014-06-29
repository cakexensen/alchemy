(ns alchemy.space)

; space - 2d grid of width/length
; for current purposes height doesn't matter much,
; so just worry about x and z dimensions.
; map [x z] to [entities] is a little naive,
; but it will work for now.

(defn new-space
  "creates a physical space, size measured in tiles"
  [width length]
  ; space is a map of [x z] to a vec of entities
  (with-meta {} {:width width
                 :length length}))

(defn bounds-check?
  "checks if coordinates are valid in a space"
  [space x z]
  (let [specs (meta space)
        width (:width specs)
        length (:length specs)]
    (and (< 0 x width)
         (< 0 z length))))

(defn get-space
  "gets the entities at a specified location"
  [space x z]
  (when (bounds-check? space x z)
    ; get at [x z] or an empty vec
    (get space [x z] [])))

(defn insert-space
  "inserts an entity at a specified location"
  [space x z entity]
  (when (bounds-check? space x z)
    (let [; get the entities at [x z]
          old-xz-entities (get-space space [x z])
          ; add the entity
          new-xz-entities (conj old-xz-entities entity)
          ; update space
          new-space (assoc space [x z] new-xz-entities)]
      new-space)))
