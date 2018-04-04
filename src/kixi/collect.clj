(ns kixi.collect
  (:require [clojure.spec.alpha :as s]
            [kixi.spec.conformers :as sc]
            [kixi.collect.request :as cr]
            [kixi.collect.request.reject :as crr]
            [kixi.collect.campaign :as cc]
            [kixi.collect.process-manager.collection-request :as pmcr]
            [kixi.datastore.metadatastore :as ms]
            [kixi.comms :as comms]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Commands

(defmethod comms/command-payload
  [:kixi.collect/request-collection "1.0.0"]
  [_]
  (s/keys :req [::cr/message
                ::cr/requested-groups
                ::cr/receiving-groups
                ::ms/id]))

(defmethod comms/command-payload
  [:kixi.collect.process-manager.collection-request/complete-process "1.0.0"]
  [_]
  (s/keys :req [::cc/id]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Events

(defmethod comms/event-payload
  [:kixi.collect/collection-requested "1.0.0"]
  [_]
  (s/keys :req [::cc/id
                ::cr/message
                ::cr/sender
                ::cr/receiving-groups
                ::cr/group-collection-requests
                ::ms/id]))

(defmethod comms/event-payload
  [:kixi.collect/collection-request-rejected "1.0.0"]
  [_]
  (s/keys :req [::crr/reason]
          :opt [::crr/message
                ::ms/id]))

(defmethod comms/event-payload
  [:kixi.collect.process-manager.collection-request/process-completed "1.0.0"]
  [_]
  (s/keys :req [::cc/id
                ::pmcr/results
                ::cr/message
                ::cr/sender
                ::cr/group-collection-requests]))

(defmethod comms/event-payload
  [:kixi.collect.process-manager.collection-request/complete-process-rejected "1.0.0"]
  [_]
  (s/keys :req [::cc/id]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Commands -> Events

(defmethod comms/command-type->event-types
  [:kixi.collect/request-collection "1.0.0"]
  [_]
  #{[:kixi.collect/collection-requested "1.0.0"]
    [:kixi.collect/collection-request-rejected "1.0.0"]})

(defmethod comms/command-type->event-types
  [:kixi.collect.process-manager.collection-request/complete-process "1.0.0"]
  [_]
  #{[:kixi.collect.process-manager.collection-request/process-completed "1.0.0"]
    [:kixi.collect.process-manager.collection-request/complete-process-rejected "1.0.0"]})
