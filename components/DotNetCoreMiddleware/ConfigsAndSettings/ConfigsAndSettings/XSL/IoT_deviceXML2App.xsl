<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    IoT / CNet MA
    Tansforms generic device XML to simplified verison
   v1, 20090916
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:scpd="urn:schemas-upnp-org:service-1-0" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns="urn:schemas-upnp-org:device-1-0" 
    xmlns:device="urn:schemas-upnp-org:device-1-0"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:IoT="IoT">

    <xsl:output encoding="utf-8" method="xml" indent="yes" omit-xml-declaration="no"/>
    <!--- =================== ========================= -->
    <!-- Datatype map: UPnP  to XSD  -->

    <xsl:template name="upnp2xsd">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type = 'ui1' ">insgnedByte</xsl:when>
            <xsl:when test="$type = 'ui2' ">unsignedShort</xsl:when>
            <xsl:when test="$type = 'ui4' ">unsignedInt</xsl:when>
            <xsl:when test="$type = 'i1' ">byte</xsl:when>
            <xsl:when test="$type = 'i2' ">short</xsl:when>
            <xsl:when test="$type = 'i4' ">int</xsl:when>
            <xsl:when test="$type = 'int' ">int</xsl:when>
            <xsl:when
                test="$type = 'r4' or $type= 'r8' or $type='number' or $type='float' or $type='fixed.14.4' "
                >float</xsl:when>
            <xsl:when test="$type = 'char' or $type= 'string' ">string</xsl:when>
            <xsl:when test="$type = 'date' ">date</xsl:when>
            <xsl:when test="$type = 'dateTime'  or $type = 'dateTime.tz' ">dateTime</xsl:when>
            <xsl:when test="$type = 'time'  or $type = 'time.tz' ">time</xsl:when>
            <xsl:when test="$type = 'boolean' ">boolean</xsl:when>
            <xsl:when test="$type = 'uri' ">string</xsl:when>
            <xsl:when test="$type = 'bin.base64' ">base64Binary</xsl:when>
            <xsl:when test="$type = 'bin.hex' ">hexBinary</xsl:when>
            <xsl:when test="$type = 'uid' ">string</xsl:when>
            <xsl:otherwise>apskaft</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--- =================== ========================= -->
    <xsl:template match="/device:root">
        <xsl:copy>
            <xsl:comment> =================== IoT Special Spanish Device XML ===================== </xsl:comment>

            <xsl:apply-templates select="device:device"/>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="device:device">
        <xsl:element name="device">
            <xsl:copy-of select="device:deviceType"/>
            <xsl:element name="IoTPID">
                <xsl:value-of select="IoT:IoTUDN"/>
            </xsl:element>
            <xsl:copy-of select="device:UDN"/>
            <xsl:copy-of select="device:friendlyName"/>
            <xsl:copy-of select="device:icon"/>
            <xsl:copy-of select="device:manufacturer"/>
            <xsl:copy-of select="device:manufacturerURL"/>
            <xsl:copy-of select="device:modelDescription"/>
            <xsl:copy-of select="device:modelName"/>
            <xsl:element name="gateway">
            <xsl:value-of select="IoT:gateway"/>
            </xsl:element>
          <xsl:element name="deviceURI">
                <xsl:value-of select="IoT:deviceURI"/>
            </xsl:element>
          <xsl:element name="OriginalURN">
            <xsl:value-of select="IoT:OriginalURN"/>
          </xsl:element>

            <!-- 
               IoTidIoTWS   ->  IoTWSEndpoint   =    metadata stuff                                      
                IoTidEnergyWS -> energywsendpoint   =      energy 
                IoTidLocationWS -> locationwsendpoint =      location
                IoTidMemoryyWS -> memorywsendpoint    = memory 
                IoTidStaticWS  ->    wsendpoint        = device specifi e.g., switchservice               
                -->

            <xsl:element name="serviceList">
                <xsl:for-each select="device:serviceList/device:service">
                    <xsl:element name="service">
                        <xsl:variable name="sName"
                            select="substring-before(substring-after(device:serviceType, 'upnp-org:'), '::')"/>
                        <xsl:variable name="sNumber"
                            select="substring-after(device:serviceType, '::')"/>
                        <xsl:variable name="sNameKey" select="concat($sName, '_', $sNumber)"/>

                        <xsl:copy-of select="device:serviceType"/>
                        <xsl:copy-of select="device:serviceId"/>

                        <xsl:choose>
                            <xsl:when test="device:serviceId = 'urn:upnp-org:serviceId:1' or device:serviceId='urn:upnp-org:serviceId:IoTServicePort' ">
                                <xsl:element name="IoTId">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidIoTWS"/>
                                </xsl:element>
                                <xsl:element name="serviceWSDL">
                                    <xsl:value-of select="ancestor::*//IoT:IoTWSEndpoint"/>
                                </xsl:element>
                                <xsl:element name="description">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidIoTWSDescription"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:when
                                test="device:serviceId = 'urn:schemas-upnp-org:energyservice:1' ">
                                <xsl:element name="IoTId">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidEnergyWS"/>
                                </xsl:element>
                                <xsl:element name="serviceWSDL">
                                    <xsl:value-of select="ancestor::*//IoT:energywsendpoint"/>
                                </xsl:element>
                                <xsl:element name="description">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidEnergyWSDescription"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:when
                                test="device:serviceId = 'urn:schemas-upnp-org:locationservice:1' ">
                                <xsl:element name="IoTId">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidLocationWS"/>
                                </xsl:element>
                                <xsl:element name="serviceWSDL">
                                    <xsl:value-of select="ancestor::*//IoT:locationwsendpoint"/>
                                </xsl:element>
                                <xsl:element name="description">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidLocationWSDescription"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:when
                                test="device:serviceId = 'urn:schemas-upnp-org:memoryservice:1' ">
                                <xsl:element name="IoTId">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidMemoryWS"/>
                                </xsl:element>
                                <xsl:element name="serviceWSDL">
                                    <xsl:value-of select="ancestor::*//IoT:memorywsendpoint"/>
                                </xsl:element>
                                <xsl:element name="description">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidMemoryWSDescription"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:comment> IoT static WS, for this device: <xsl:value-of
                                        select="$sNameKey"/>
                                </xsl:comment>
                                <xsl:element name="IoTId">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidStaticWS"/>
                                </xsl:element>
                                <xsl:element name="serviceWSDL">
                                    <xsl:value-of select="ancestor::*//IoT:staticWSwsdl"/>
                                </xsl:element>
                                <xsl:element name="description">
                                    <xsl:value-of select="ancestor::*//IoT:IoTidStaticWSDescription"/>
                                </xsl:element>
                            </xsl:otherwise>
                        </xsl:choose>
                     
                    </xsl:element>

                </xsl:for-each>

            </xsl:element>


            <xsl:comment>====================== Event list ======================</xsl:comment>
            <xsl:element name="eventInfo">
                <!-- output xml, without prolog    substring-after  ?&gt; -->
<!--                <xsl:value-of select="translate(IoT:eventlist, '&#10;', '')"
disable-output-escaping="yes" xml:space="default" />     -->  
                
                <xsl:variable name="el" select="IoT:eventlist" />
                <xsl:value-of select="IoT:eventlist"
                        disable-output-escaping="yes" />
            </xsl:element>
        </xsl:element>
    </xsl:template>


    <xsl:template name="createTypes">
        <!-- ====================== Schema types ====================== -->
        <xsl:for-each select=".//scpd:action">
            <xsl:variable name="name" select="scpd:name"/>
            <xsl:comment>====================== <xsl:value-of select="$name"/>
                ======================</xsl:comment>
            <xsl:choose>
                <!-- check if there are any named input args -->
                <!-- these are named according to the corresponding UPnP state variables -->
                <xsl:when test=".//scpd:direction[.='in']">
                    <xsd:element name="{$name}">
                        <xsd:complexType>
                            <xsd:sequence>
                                <xsl:for-each select=".//scpd:argument[scpd:direction[.= 'in']]">
                                    <xsl:variable name="stateVar"
                                        select="//scpd:stateVariable[scpd:name = current()/scpd:relatedStateVariable]"/>
                                    <xsl:variable name="type">
                                        <xsl:call-template name="upnp2xsd">
                                            <xsl:with-param name="type"
                                                select=" $stateVar/scpd:dataType"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsd:element name="{scpd:name}" type="{concat('xsd:',$type)}"
                                        minOccurs="1"/>
                                </xsl:for-each>
                            </xsd:sequence>
                        </xsd:complexType>
                    </xsd:element>
                </xsl:when>
                <xsl:otherwise>
                    <!-- create an empty element  as default-->
                    <xsd:element name="{$name}">
                        <xsd:complexType>
                            <xsd:sequence> </xsd:sequence>
                        </xsd:complexType>
                    </xsd:element>
                </xsl:otherwise>
            </xsl:choose>

            <xsd:element name="{concat($name,'Response')}">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsl:for-each select=".//scpd:argument[scpd:direction[.= 'out']]">
                            <xsl:variable name="stateVar"
                                select="//scpd:stateVariable[scpd:name = current()/scpd:relatedStateVariable]"/>
                            <xsl:variable name="type">
                                <xsl:call-template name="upnp2xsd">
                                    <xsl:with-param name="type" select=" $stateVar/scpd:dataType"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsd:element name="{concat(scpd:name,'Result')}"
                                type="{concat('xsd:',$type)}" minOccurs="1"/>
                        </xsl:for-each>
                    </xsd:sequence>
                </xsd:complexType>
            </xsd:element>
        </xsl:for-each>

    </xsl:template>



</xsl:stylesheet>
