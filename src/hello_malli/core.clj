(ns hello-malli.core
  (:require [malli.core :as m]
            [malli.generator :as mg]
            [malli.dev.pretty :as pretty]
            [malli.error :as me]
            )
  (:gen-class))


(def UserId :string)


(def Address
  [:map
   [:street :string]
   [:country [:enum "FI" "UA"]]])



(def User
  [:map
   [:id #'UserId]
   [:address #'Address]
   [:friends [:set {:gen/max 2} [:ref #'User]]]])

(def contact 
  [:map 
   [:name [:string {:min 1, :max 3}]]
   ])

(def place [:tuple {:title "location"} :double :double])

(def NonEmptyString 
 (m/schema [:string {:min 1}]))

; for performance (optimized use validator)
(def valid? (m/validator 
             [:map 
              [:name :string]
              [:email {:optional true} :string]
              ]
             )
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment     
  
  
(def multi-schema-valid? 
   (m/validator 
    [:multi {:dispatch :type}
      ["type-one" [:map 
                     [:type :string]
                     [:name :string]
                    ]
       ]
      ["type-one" [:map {:closed true}
                      [:type :string]
                      [:value pos-int?]
                  ]
                  ]
      [::m/default :string]
    ]       
   ) 
)

  )





(comment 
  (mg/generate User)
  (m/validate User *1)

  (m/validate nil? nil)
  (m/validate [:map [:a uuid?]] {:a (random-uuid)})

  (m/validate zero? 0)

  (m/validate [:or :int :string] 1)
  (m/validate [:or :int :string] "")
  (m/validate [:or :int :string] false)
  
  (m/validate NonEmptyString "")
  (m/validate NonEmptyString nil)
  (m/validate NonEmptyString "abc")
  (m/validate [:enum "a" "b"] "b")

  (valid? {:name "ike" :email "ike@gmail.com"})

  (m/validate [:map [:x :int] ] {:x 1})

  (m/validate [:map [:x :int] ] {:x 1 :extra "extra"})

  (m/validate [:map {:closed true} [:x :int] ] {:x 1 :extra "extra"})

  (pretty/explain [:map {:closed true} [:x :int] ] {:x 1 :extra "extra"})

  (m/validate [:map 
               ["status" [:enum "ok"]]
               [1 :any]
               [nil :any]
               ]
              {"status" "ok"
               1 "1"
               nil :yay 
               }
              
              )

  (m/validate [:map-of :string :int] {"asdf" 1 "zxcy" 4})
  
  (m/validate [:map-of :string [:map [:x pos-int?] [:y pos-int?] ] ]  {"a" {:x 1 :y 2} "b" {:x 1 :y 1}})

  (pretty/explain [:map-of :string [:map [:x pos-int?] [:y pos-int?] ] ]  {"a" {:x 1 :y 2} "b" {:x 1 :y -1}})
  
  (m/validate [:vector int?] [1 2 3 "c"])

  (pretty/explain [:vector int?] [1 2 "e"])
  
  (m/validate [:tuple keyword? string? number?] [:age "day" 40]) 

  (m/validate [:maybe string?] "a")

  (m/validate [:maybe string?] nil)

  (m/validate [:map [:s {:optional true} :string]] {})

  (m/validate [:map [:s {:optional true} :string]] {:s "ike"})

  (->> {}
      (m/explain User)
      (me/humanize)
      )
  

  

  )