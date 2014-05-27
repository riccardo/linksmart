<?xml version="1.0" encoding="UTF-8"?>
<!--
	_________________________________________________
	| Filter för konvertering till TTSTVR.
	_________________________________________________
	| Utvecklad av:  CNet Svenska AB (MA), http://www.cnet.se
	| Datum:	2008-01-24
	| Version: 	1.2
	_________________________________________________
	| Revisionsinformation:
	| 2008-01-04	Release version 1
	| 2008-01-11	Release version 1.1 - datumfix + generering av ProgramPerson-element för skådespelare/roll görs lokalt i filtret. 
	| 2008-01-24	Release version 1.2:  lagt till Intro baserad på källans html class  "slaglinie" 
	| 2008-04-14	Release version 1.3:  datumfix, fel i datumfunktion, lagt till xmlns:local
	| 2008-04-15	Release version 1.4:  viss modifiering för att kunna mappa device discovery info till IoTs device ontologi
	
	_________________________________________________
	| Maps discovery info for protocols: 
	| BlueTooth
	| RF - Telldus
	_________________________________________________
	| Indataformat:
	|
	| XML  
	_________________________________________________
	| Övrig information:
	|  ________________________________________________
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
 xmlns:cnet="http://www.cnet.se"
    xmlns:local="http://www.cnet.se/local"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    >


    <!-- ====================================== -->
    <xsl:function name="cnet:parseDate-D">
        <xsl:param name="fullText"/>
       
        <xsl:value-of select="''"/>
    </xsl:function>
    <!-- ====================================== -->
    <!-- -->
    <xsl:output method="xml" version="1.0" encoding="UTF-8" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="/">
        <deviceInstance xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.cnet.se IoTDeviceOntology.xsd" id="0">
            
            
            <xsl:apply-templates select="*"/>
        </deviceInstance>
    </xsl:template>

<!-- ========================================================= -->
    <!-- 
    <basicphonedevice><name>Z600</name><deviceclass>Phone</deviceclass><vendor>Ericsson</vendor></basicphonedevice>
    -->
    <xsl:template match="bluetoothdevice">
  <bluetoothh>
   
            <xsl:apply-templates select="bluetoothservices"/></bluetoothh>
    </xsl:template>
    
    <xsl:template match="bluetoothservices">        
    </xsl:template>
    <!-- ========================================================= --> 
    <!-- Telldus supported device manufacturers
        
        Nex
        Sartano
        Proove
        Waveman
    -->

    <xsl:template match="tellstickdevice" name="tellstick">
        <xsl:element name="testickleDevice">
            <xsl:copy-of select="*"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
