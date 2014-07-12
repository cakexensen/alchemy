(ns alchemy.game.directors.core)

(defmulti load-director :director)

(defn change-director
  "changes current director and performs load/unload operations"
  [state new-director]
  (let [old-director (:director state)]
    (if (= old-director new-director)
      state ; don't change anything since director didn't change
      (-> state
          ;; unload old, load new
          ;;(unload-director)
          (assoc :director new-director)
          (load-director)))))
