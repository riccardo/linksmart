;; STRIPS Instance problem for the Deploy domain

(define (problem pb1)
  (:domain protocoldomain)
  (:objects Cs Cc Ctcp Cudp Ds Dc Sc Ss Sudp Stcp tcptype udptype propertykey Iudp Itcp Is)

  (:init
   ;; types
   (Component Cs) (Component Cc) (Component Ctcp)(Component Cudp)
   (Device Ds)(Device Dc)
   (Service Sc)(Service Ss)(Service Sudp)(Service Stcp)
   (Protocol tcptype)(Protocol udptype)
   (Property propertykey)
   (Interface Itcp)(Interface Iudp)(Interface Is)

   ;; relations
    (At Ds Cs)(At Dc Cc)(At Ds Ctcp)
    (Has Ds Ss)(Has Dc Sc)(Has Ds Ctcp)
    (Provides Cudp Sudp)(Provides Ctcp Stcp)
    (Provides Cs Ss)(Provides Cc Sc)
    (Provides Stcp Itcp)(Provides Sudp Iudp)
	(AvailableAt Ds Itcp)
    (Channel Dc Ds)
    (Property propertykey)
    (Protocol tcptype)(Protocol udptype)
    (HasProperty Dc propertykey)
    (PropertyValue Dc propertykey tcptype) ;; illustrate change to udp, so we set it to tcp initially
	(Realizes Itcp tcptype)(Realizes Iudp udptype)

    ;;states
    (initiated Dc)(initiated Ds)   
	(started Sc)(started Ss)(started Stcp)
    ;;(not (At D1 C1))
  )

  (:goal
    ;;(and
    	 (PropertyValue Dc propertykey udptype)
       ;;(AvailableAt Ds Iudp)
       ;;(AvailableAt D1 srvtype) 
      ;; (not (AvailableAt ))
    ;;)
  )
)
