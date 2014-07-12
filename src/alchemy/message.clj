(ns alchemy.message)

(defn message-system
  "creates a new message system"
  []
  (atom {}))

(defn mailbox
  "creates a mailbox in a message system"
  [system address]
  {:system system :address address})

(defn send
  "sends a message to a mailbox"
  ([{:keys [system address] :as mailbox} recipient tag data]
     ;; attach address as sender of message?
     (let [message {:tag tag :data data}
           old-messages (get @system recipient [])
           new-messages (conj old-messages message)]
       (swap! system assoc recipient new-messages)))
  ([mailbox recipient tag]
     ;; for sending redundant signal messages like (.. :close :close)
     (send mailbox recipient tag tag)))

(defn receive
  "receives the messages in a mailbox"
  [{:keys [system address] :as mailbox}]
  (let [messages (get @system address [])]
    (swap! system assoc address [])
    messages))
