;; STRIPS Instance problem for the Deploy domain

(define (problem pb1)
  (:domain deployment)
  (:objects C1 C2 D1 client server srvtype P1)

  (:init
   ;; types
   (Component C1) (Component C2)
   (Device D1)
   (Package P1)
   (Service client)
   (Service server)
   (Interface srvtype)

   ;; relations
    (At D1 C2)
    (Provides C1 P1)
    (Provides C1 client)
    (Provides C1 server)
    (Provides server srvtype)
    (Requires client srvtype)

    ;;states
    (initiated D1)   

    ;;(not (At D1 C1))
  )

  (:goal
    (and
       (BoundTo client server srvtype) 
       ;;(AvailableAt D1 srvtype) 
       (not (At D1 C2))
    )
  )
)
