<?xml version="1.0" encoding="UTF-8"?>
<!--
	_________________________________________________
	| IoT/WP6/CNET/MA
	| Device Discovery Resolver 
	| XSL-t rules IoT
	| 2008-06-18  v 1.0   
	| ________________________________________________
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:cnet="http://www.cnet.se" xmlns:ontology="urn:IoT_ontologymanager"
  xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  exclude-result-prefixes="rdf owl cnet xs">
  <!-- ====================================== -->
  <!-- -->
  <xsl:output method="xml" version="1.0" encoding="UTF-8" omit-xml-declaration="no" indent="yes"/>

  <xsl:template match="/">
    <root xmlns="urn:schemas-upnp-org:device-1-0">
      <specVersion>
        <major>1</major>
        <minor>0</minor>
      </specVersion>
      <xsl:apply-templates select="*"/>
    </root>
  </xsl:template>
  <!-- ============ Ontology lookup function ==================== -->
  <xsl:template name="ontologyResolve">
    <!-- Ontology class switch, should call ontology to determine this  -->
    <xsl:param name="key"/>
    <xsl:param name="discoInfo"/>
    <xsl:choose>
      <xsl:when test="contains($key,'BT')">
        <xsl:choose>
          <xsl:when test="$discoInfo/majordevicetype = 'Phone'">
            <xsl:choose>
              <xsl:when test="$discoInfo/deviceclass='CellPhonePhone'">
                <!--     dialupnetwokring or  serialport  -->
                <xsl:choose>
                <xsl:when
                  test=".//bluetoothservice//servicetype[. ='Dialup Networking' or . = 'Serial Port']">basicphonedevice</xsl:when>
                <xsl:otherwise>bluetoothdevice</xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:when test="$discoInfo//servicename='mobilecamera'">basiccameradevice</xsl:when>
              <xsl:when test="$discoInfo/deviceclass='SmartPhone'">smartphonedevice</xsl:when>
              <xsl:otherwise>bluetoothdevice</xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'IoTdevice'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="contains($key,'switch')">
        <xsl:choose>    
          <!-- note must deal with case-->
          <!-- xsl:when test="contains(.//name,':'')">enhancedswitch</xsl:when -->
          <xsl:when test="contains((.//name),'Light') or contains((.//name),'Lamp')">enhancedswitchdevice</xsl:when><!-- higher order sematics  -->
          <xsl:otherwise>basicswitchdevice</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="contains($key,'RFID')">
        <xsl:value-of select="'rfidtagdevice'"/>
      </xsl:when>
      <xsl:when test="contains($key,'WIA')">
        <xsl:value-of select="$discoInfo/deviceclass"/>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:value-of select="'unknownGrunka'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ============ Protocol type: RFID ============================================== -->
  <!-- rfidtagdevice, tag , manufaturercode -->
  <xsl:template match="rfidtagdevice">
    <xsl:variable name="discoveryInfo"></xsl:variable>

    <!-- Check Ontology class  -->
    <xsl:variable name="deviceOntologyClass">
      <xsl:call-template name="ontologyResolve">
        <xsl:with-param name="key" select="'RFID'"/>
        <xsl:with-param name="discoInfo" select="."/>
      </xsl:call-template>
    </xsl:variable>

    <!-- create SCPD struct for this device type -->
    <xsl:element name="device">
      <xsl:element name="{$deviceOntologyClass}" xmlns="IoT">
        <xsl:attribute name="protocolType">rfid</xsl:attribute>
      </xsl:element>
      <deviceType>urn:schemas-upnp-org:IoTdevice:rfidtagdevice:1</deviceType>

      <friendlyName>RFID Tag Device</friendlyName>
      <manufacturer>
        <xsl:value-of select="manufacturercode"/>
      </manufacturer>
      <manufacturerURL/>
      <modelDescription>
        <xsl:value-of select="$deviceOntologyClass"/>
      </modelDescription>
      <modelName>
        <xsl:value-of select="name"/>
      </modelName>
      <modelNumber></modelNumber>
      <UDN/>
      <serviceList/>
      <tagid xmlns="IoT">
        <xsl:value-of select="tagid"/>
      </tagid>
      <hasService xmlns="IoT"/>
      <hasIP xmlns="IoT"/>
      <hasWSDLDocument xmlns="IoT"/>
      
    </xsl:element>
    <!-- device -->
  </xsl:template>

  <!-- =========== Protocol type: Bluetooth============================================== -->

  <xsl:template match="bluetoothdevice">
    <xsl:variable name="discoveryInfo">
      <name>
        <xsl:value-of select="name"/>
      </name>
      <majordevicetype>
        <xsl:value-of select="majordevicetype"/>
      </majordevicetype>
      <deviceclass>
        <xsl:value-of select="deviceclass"/>
      </deviceclass>
      <vendor>
        <xsl:value-of select="vendor"/>
      </vendor>
      <deviceid/>
    </xsl:variable>

    <!-- Check Ontology class  -->
    <!-- 'BT',$discoveryInfo) -->
    <xsl:variable name="deviceOntologyClass">
      <xsl:call-template name="ontologyResolve">
        <xsl:with-param name="key" select="'BT'"/>
        <xsl:with-param name="discoInfo" select="."/>
      </xsl:call-template>
    </xsl:variable>

    <!-- create SCPD struct for this device type -->
    <xsl:element name="device">
      <xsl:element name="{$deviceOntologyClass}" xmlns="IoT">
        <xsl:attribute name="protocolType">bluetooth</xsl:attribute>
      </xsl:element>
      <deviceType>urn:schemas-upnp-org:IoTdevice:bluetooth:1</deviceType>

      <friendlyName>
        <xsl:value-of select="name"/>
      </friendlyName>
      <manufacturer>CNet</manufacturer>
      <manufacturerURL>http://www.cnet.se</manufacturerURL>
      <modelDescription>
        <xsl:value-of select="$deviceOntologyClass"/>
      </modelDescription>
      <modelName>
        <xsl:value-of select="name"/>
      </modelName>
      <modelNumber/>
      <UDN/>
      <serviceList/>
      <hasService xmlns="IoT"/>
      <hasIP xmlns="IoT"/>
      <hasWSDLDocument xmlns="IoT"/>
      <securityInfo xmlns="IoT">

        <property name="bluetooth.api.version">
          <value>1.1</value>
        </property>
        <property name="bluetooth.mode">
          <value>2</value>
        </property>
        <property name="EncryptionProtocol">
          <value>E0</value>
        </property>
        <property name="HostMechanism">
          <value>plain</value>
          <value>AES_256_storage</value>
        </property>
        <property name="KeystoreTypes">
          <value>JKS</value>
          <value>BC</value>
        </property>

      </securityInfo>
    </xsl:element>
    <!-- device -->
  </xsl:template>


  <!-- ===============  Protocol type:   RF Switch ========================================== -->
  <!-- Telldus Tellstick supported device manufacturers: get from vocabulary/ontology Nexa, Sartano, Proove, Waveman   
 Physical  discovery info 
 
  <tellstickdevce>
    <name></name>
    <vendor></vendor> 
    <deviceid></deviceid> 
  </tellstickdevce>
  -->

  <xsl:template match="tellstickdevice" name="tellstick">
    <xsl:variable name="discoveryInfo">
      <name>
        <xsl:value-of select="name"/>
      </name>
      <majordevicetype>RFSwitch</majordevicetype>
      <vendor>
        <xsl:value-of select="vendor"/>
      </vendor>
      <deviceid/>
    </xsl:variable>

    <xsl:variable name="deviceOntologyClass">
      <xsl:call-template name="ontologyResolve">
        <xsl:with-param name="key" select="'RFswitch'"/>
        <xsl:with-param name="discoInfo" select="$discoveryInfo"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:element name="device">
      <xsl:element name="{$deviceOntologyClass}" xmlns="IoT">
        <xsl:attribute name="protocolType">tellstick</xsl:attribute>
      </xsl:element>
      <deviceType>urn:schemas-upnp-org:IoTdevice:basicswitch:NEXA:1</deviceType>
      <modelDescription/>
      <modelName/>
      <manufacturerURL/>
      <manufacturer>
        <xsl:value-of select="vendor"/>
      </manufacturer>
      <friendlyName>
        <xsl:choose>
          <xsl:when test="contains(name,':')">
            <xsl:value-of select="substring-before(name,':')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="name"/>
          </xsl:otherwise>
        </xsl:choose>
      </friendlyName>
      <deviceid xmlns="IoT">
        <xsl:value-of select="deviceid"/>
      </deviceid>
      <hasService xmlns="IoT"/>
      <hasIP xmlns="IoT"/>
      <hasWSDLDocument xmlns="IoT"/>
      <!-- An RF switch controls an elctrical device of some type, this can be specified by a string as part of the device name e.g.,  "switch1:lamp1" -->
      <xsl:if test="contains(name,':')">
        
      <slaveDevice xmlns="IoT">
        <xsl:value-of select="substring-after(name,':')"/>
      </slaveDevice>
      </xsl:if>

      <securityInfo xmlns="IoT">

        <property name="tellstick.api.version">
          <value>2.1</value>
        </property>
        <property name="switch.mode">
          <value>2</value>
        </property>
        <property name="EncryptionProtocol">
          <value>None</value>
        </property>
        
        

      </securityInfo>
    </xsl:element>
    <!-- device -->
  </xsl:template>
  <!-- ===============  Protocol type:   Serial port  ========================================== -->
  <!-- No discovery into available 
    
    <semanticdevice>
    <weatherstation>
    <rainmeter/>
    <thermometer/>
    <windmeter/>
    <airpressure/>
    </weatherstation>
    -->
  <!--xsl:template match="Phidget">
    <name>
      Light
    </name>
    <majordevicetype>
      Sensor
    </majordevicetype>
    <deviceclass>
      Lightsensor
    </deviceclass>
    <vendor>
     Phidget
    </vendor>
  </xsl:template-->
  
<xsl:template match="WIADevice">
    <xsl:variable name="discoveryInfo">
      <name>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Name']/value"/>
      </name>
      <majordevicetype>
        <xsl:value-of select="WIADeviceType"/>
      </majordevicetype>
      <deviceclass>
        <xsl:value-of select="WIADeviceType"/>
      </deviceclass>
      <vendor>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Manufacturer']/value"/>
      </vendor>
      <deviceid/>
    </xsl:variable>

    <!-- Check Ontology class  -->
    <!-- 'WIA',$discoveryInfo) -->
    <!--xsl:variable name="deviceOntologyClass">
      <xsl:call-template name="ontologyResolve">
        <xsl:with-param name="key" select="'WIADevice'"/>
        <xsl:with-param name="discoInfo" select="$discoveryInfo"/>
      </xsl:call-template>
    </xsl:variable-->

    <!-- create SCPD struct for this device type -->
    <xsl:element name="device">
      <!--xsl:element name="{$deviceOntologyClass}" xmlns="IoT"-->
      <xsl:element name="wiacameradevice" xmlns="IoT">
        <xsl:attribute name="protocolType">WIA</xsl:attribute>
      </xsl:element>
      <deviceType>urn:schemas-upnp-org:IoTdevice:wiadevice:1</deviceType>

      <friendlyName>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Name']/value"/>
      </friendlyName>
      <manufacturer>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Manufacturer']/value"/>
      </manufacturer>
      <manufacturerURL>http://www.cnet.se</manufacturerURL>
      <modelDescription>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Description']/value"/>
      </modelDescription>
      <modelName>
        <xsl:value-of select="WIAProperties/WIAProperty[name='Description']/value"/>
      </modelName>
      <modelNumber/>
      <UDN/>
      <serviceList/>
      <hasService xmlns="IoT"/>
      <hasIP xmlns="IoT"/>
      <hasWSDLDocument xmlns="IoT"/>
      <securityInfo xmlns="IoT">

       

      </securityInfo>
    </xsl:element>
    <!-- device -->
  </xsl:template>
</xsl:stylesheet>
