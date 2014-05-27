<?xml version="1.0" encoding="UTF-8"?>
<!--
	_________________________________________________
	| Filter för konvertering till TTSTVR.
	_________________________________________________
	| Utvecklad av:  CNet Svenska AB (MA), http://www.cnet.se
	| Datum:	2008-01-24
	| Version: 	1.2
	-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
 xmlns:cnet="http://www.cnet.se"
    xmlns:local="http://www.cnet.se/local"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:upnp="urn:schemas-upnp-org:device-1-0"
                xmlns:IoT="IoT"
    >


    
   
    <xsl:output method="xml" version="1.0" encoding="UTF-8" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="/">
      <binding>       
            
            <xsl:apply-templates select="*"/>
      </binding>
    </xsl:template>

<xsl:template match="upnp:device">

<!--xsl:if test="IoT:gateway='Peters Portable'">
<IoTicon><xsl:value-of select="upnp:friendlyName"/>.jpg</IoTicon>
</xsl:if-->

  <xsl:if test="upnp:deviceType='urn:schemas-upnp-org:IoTdevice:basicswitchdevice:1' or upnp:deviceType!='urn:schemas-upnp-org:IoTdevice:enhancedswitchdevice:1'">


    <xsl:if test="upnp:friendlyName='DiscoBall'">
      <IoTUDN>DiscoBall</IoTUDN>
      <locationdata>
        <building>CNet Office</building>
        <room>Main</room>
        <position>Table</position>
      </locationdata>
    </xsl:if>

    <xsl:if test="upnp:friendlyName='PetersLight' and IoT:gateway='Peters Portable'">
      <IoTUDN>DemoLight</IoTUDN>

      <locationdata>
        <building>CNet Office</building>
        <room>Main</room>
        <position>Table</position>
      </locationdata>
    </xsl:if>

    <xsl:if test="IoT:gateway='Casa Domotica'">
      <IoTUDN><xsl:value-of select="upnp:friendlyName"/></IoTUDN>
        <locationdata>
          <building>Casa Domotica</building>
          <room>
            <xsl:value-of select="upnp:friendlyName"/>
          </room>
        </locationdata>
        
    </xsl:if>

    <xsl:if test="upnp:friendlyName!='DiscoBall' and upnp:friendlyName!='LyonLight'">
      <IoTUDN><xsl:value-of select="upnp:friendlyName"/></IoTUDN>
    </xsl:if>
    
  </xsl:if>

  <xsl:if test="upnp:deviceType!='urn:schemas-upnp-org:IoTdevice:basicswitchdevice:1' and upnp:deviceType!='urn:schemas-upnp-org:IoTdevice:enhancedswitchdevice:1'">

    <IoTUDN><xsl:value-of select="upnp:friendlyName"/></IoTUDN>

  </xsl:if>
  

</xsl:template>
  
    

</xsl:stylesheet>
