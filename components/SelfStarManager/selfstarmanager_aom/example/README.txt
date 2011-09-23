Self-star manager example
=================
This example shows how to use the Ontology Manager in a
self-management scenario.

The scenario involves a set of SUN Spots, each measuring temperature
and transmitting that via the Event Manager. The sampling rate
is dependent on the battery level of the connected device

To build
-------

1) build the Hydra middleware (in the root of Hydra):

   ant

2) build the selfstarmanager repository (in the root):

   ant

3) deploy the Event Manager bridge (in selfstarmanager/)

   ant -Ddistribution.dir=/tmp/em deploy-test-eventmanager

4) deploy the test Self-Star Manager (in TemperatureSelfStarManager/)

   ant -Ddistribution.dir=/tmp/ssm deploy
  

To run
-----

1) Run the socket proxy (in a SPOT project directory):

   ant socket-proxy-gui

2) Run the event manager bridge 
   
   cd /tmp/em
   java -Deventmanager.registermanager=true -Dorg.osgi.service.http.port=8000 -jar bin/felix.jar

3) Run the Self-Star Manager:

   cd /tmp/ssm
   java -Dorg.osgi.service.http.port=8001 -Deventmanager.host=localhost:8000 -jar bin/felix.jar

4) Connect and run SUN Spots (e.g., by doing the following in the TemperatureSensor directory):

   ant -DremoteID=0014.4F01.0000.0FF7 deploy run
   ant -DremoteID=0014.4F01.0000.1057 deploy run

To demo
-------
- The LEDs indicate battery level (red = low, green=high, yellow = medium)
- The sampling of a measurement is indicated by the LEDs flashing
- The number of LEDs turned on indicate temperature (0.25 degrees resolution)
