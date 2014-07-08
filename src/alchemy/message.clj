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
  [{:keys [system address] :as mailbox} recipient message]
  ; attach address as sender of message?
  (let [old-messages (get @system recipient [])
        new-messages (conj old-messages message)]
    (swap! system assoc recipient new-messages)))

(defn receive
  "receives the messages in a mailbox"
  [{:keys [system address] :as mailbox}]
  (let [messages (get @system address [])]
    (swap! system assoc address [])
    messages))
