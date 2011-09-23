;; ADL domain of the ASL operations. ADL is needed because universal
;; quantification is needed to handle packages and interfaces correctly

(define (domain deployment)
  (:requirements :adl) ;; adl is needed b

  (:predicates 
           (At ?device ?component)
           (AvailableAt ?device ?package) ;;don't use predicates derivable from other predicates...
           (Provides ?component ?package)
  	    (Requires ?component ?package)
           (Has ?device ?service)
           (BoundTo ?s1 ?s2 ?i)


           ;; types: 
           (Package ?obj)
           (Component ?obj)
           (Device ?obj)
           (Service ?obj)
           (Interface ?obj)
           
 	   ;; states
           (initiated ?device)
           (started ?service)
           (Bound ?s ?i) 
                     
  )

  (:action STARTDEVICE 
     :parameters (?d)
     :precondition (not (initiated ?d))
     :effect (initiated ?d)
  )

  (:action STOPDEVICE
     :parameters (?d)
     :precondition (initiated ?d)
     :effect (not (initiated ?d))
  )

;;************************************
;;*  Deploy and Undeploy Components  *
;;************************************

  (:action DEPLOY
     :parameters (?d ?c)
     :precondition (and (Component ?c) (Device ?d) (initiated ?d)
             (not (At ?d ?c)))
     :effect  (and (At ?d ?c)
                  (forall (?p) 
                     (when (Provides ?c ?p)
                     (AvailableAt ?d ?p))
                  )
     )
  )
 
  (:action UNDEPLOY
     :parameters (?d ?c)
     :precondition (and (Component ?c) (Device ?d) (initiated ?d)
                    (At ?d ?c))
     :effect (and (not (At ?d ?c))
                  (forall (?p)  ;; TODO must check that p is actually a package...
                          (when (and (Provides ?c ?p)
                                     (not (exists (?cm)(and (Component ?cm)
                                                       (Provides ?cm ?p)
                                                       (not (= ?c ?cm))
                                                  )
                                           )
                                      )
                                )
                                (not (AvailableAt ?d ?p))
                          )
                    )
              )
  )

;;******************************
;;*  Start and stop services  *
;;******************************


 (:action STARTSERVICE
    :parameters (?d ?c ?s)
    :precondition (and (Provides ?c ?s)
                       (Service ?s)
                       (Device ?d)
                       (Component ?c)
                       (At ?d ?c)
                       (not (started ?s))
                        ;; all required interfaces are available:
                       (forall (?i)(or  (and (Interface ?i)
                                             (Requires ?s ?i)
                                             (AvailableAt ?d ?i))
                                        (and (Interface ?i)
                                  	     (not (Requires ?s ?i)))
                                        (not (Interface ?i))
 		                   )
                       )
                   )
    :effect (and (Has ?d ?s)
    	    	 (At ?d ?s)
                 (started ?s)
		 (forall (?i)(when (Provides ?s ?i)
		 	 	   (AvailableAt ?d ?i))))
 )

 (:action STOPSERVICE
    :parameters (?d ?s)
    :precondition  (and (Device ?d)(Service ?s)(started ?s)
    		   (forall (?i)(or ;; 1: it *is* and interface on the service, but not in use
				   (and (Interface ?i)
                                        (and (Requires ?s ?i)(Provides ?s ?i))
                                        (not (Bound ?s ?i)))
				   ;; 2: it is and interface, but *not* on this service
				   (and (Interface ?i)
                                        (and (not (Requires ?s ?i))(not (Provides ?s ?i))))
                                        ;; not not this service, so we don't care if it's bound
                                   ;; 3: it's not an interface at all...
				   (not (Interface ?i))
			       )
                   ))
     :effect (and (not (Has ?d ?s))(not (At ?d ?s))(not (started ?s))
                  (forall (?i)(when (and (Interface ?i)
		  	  	    	 (Provides ?s ?i)
					 (not (exists (?s2)(and (Service ?s2)
					      	      		(Provides ?s2 ?i)))))
				    (not (AvailableAt ?d ?i)))
                  )
              )
 )

;;*********************************
;;*   Bind and Unbind Interfaces  *
;;*********************************

;; Interfaces can only be bound once, to one other service, and they are then in the state Bound
 (:action BINDINTERFACES
    :parameters (?s1 ?s2 ?i)
    :precondition (and (not (Bound ?s1 ?i)) (not (Bound ?s2 ?i))
                       (or (and (Provides ?s1 ?i)(Requires ?s2 ?i))
		       	   (and (Requires ?s1 ?i)(Provides ?s2 ?i)))
		       (started ?s1)
		       (started ?s2))
    :effect (and (Bound ?s1 ?i)(Bound ?s2 ?i)(BoundTo ?s1 ?s2 ?i))
 )
 
 (:action UNBINDINTERFACES
    :parameters (?s1 ?s2 ?i) 
    :precondition (and (Bound ?s1 ?i)(Bound ?s2 ?i)(BoundTo ?s1 ?s2 ?i))
    :effect (and (not (Bound ?s1 ?i))(not (Bound ?s2 ?i))(not (BoundTo ?s1 ?s2 ?i)))
 )
)
