To generate the axis clients for a LinkSmart Manager you need to:

1. 	In build.xml make axis1.lib.dir point to the lib folder of your local Axis installation.
	Example: <property name="axis1.lib.dir" value="/Users/otto/Opt/axis-1_4/lib" />
	
2.	Change the property "wsdl" to the Web Service URL of the Manager you want to create stubs for. 
	Example: <property name="wsdl" value="http://localhost:8082/axis/services/NetworkManagerApplication?wsdl" />
	
3.	Start LinkSmart, with the Manager you want to create stubs for.

4.	Run target "make-axis"

5.	Clients stubs should have been created in src folder. 
	If needed, move the generated stubs to a ".client" subpackage.
	Example: eu.linksmart.network --> eu.linksmart.network.client
