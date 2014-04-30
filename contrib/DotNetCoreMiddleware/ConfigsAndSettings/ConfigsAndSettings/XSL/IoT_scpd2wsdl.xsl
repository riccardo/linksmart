<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    IoT / CNet MA
    Tansforms a device SCPD to a WSDL, for the generic IoT device services
    v1.3, 20080821
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:scpd="urn:schemas-upnp-org:service-1-0" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
    xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:wsam="http://www.w3.org/2007/05/addressing/metadata" xmlns:tns="http://tempuri.org/"
    xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
    xmlns:wsp="http://schemas.xmlsoap.org/ws/2004/09/policy"
    xmlns:wsap="http://schemas.xmlsoap.org/ws/2004/08/addressing/policy"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:msc="http://schemas.microsoft.com/ws/2005/12/wsdl/contract"
    xmlns:wsaw="http://www.w3.org/2006/05/addressing/wsdl"
    xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/"
    xmlns:wsa10="http://www.w3.org/2005/08/addressing"
    xmlns:wsx="http://schemas.xmlsoap.org/ws/2004/09/mex" xmlns:IoT="IoT">

  <xsl:output encoding="utf-8" method="xml"/>
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
            <xsl:when test="$type = 'r4' or $type= 'r8' or $type='number' or $type='float' or $type='fixed.14.4' ">float</xsl:when>
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
    <xsl:template match="/services">
        <!-- ===================  Create WSDL ===================== -->
        <wsdl:definitions name="IoTDeviceWS" targetNamespace="http://tempuri.org/">
            <wsdl:types>
              <xsd:schema targetNamespace="http://tempuri.org/">
                    <xsl:comment>====================== Schema types ======================</xsl:comment>
                    <xsl:for-each select="scpd:scpd">
                        <xsl:call-template name="createTypes"/>
                    </xsl:for-each>
                </xsd:schema>
            </wsdl:types>
            <xsl:comment>====================== Messages ======================</xsl:comment>
            <xsl:for-each select="scpd:scpd">
                <xsl:call-template name="createMsgs"/>
            </xsl:for-each>
            <xsl:comment>====================== Operations ======================</xsl:comment>
            <xsl:for-each select="scpd:scpd">
                <xsl:call-template name="createOperations"/>
            </xsl:for-each>
            <xsl:comment>====================== Bindings ======================</xsl:comment>
            <wsdl:binding name="BasicHttpBinding_IIoTDeviceWSService"
                type="tns:IIoTDeviceWSService">
                <soap:binding transport="http://schemas.xmlsoap.org/soap/http"/>
                <xsl:for-each select="scpd:scpd">
                    <xsl:call-template name="createBindings">
                        <xsl:with-param name="serviceID" select="@IoT:serviceid"/>
                    </xsl:call-template>
                </xsl:for-each>
            </wsdl:binding>

            <xsl:comment>====================== Service ======================</xsl:comment>
            <wsdl:service name="IoTDeviceWS">
                <wsdl:port name="BasicHttpBinding_IIoTDeviceWSService"
                    binding="tns:BasicHttpBinding_IIoTDeviceWSService">
                    <soap:address location="http://192.168.0.107:57229"/>
                </wsdl:port>
            </wsdl:service>

        </wsdl:definitions>
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
                                            <xsl:with-param name="type" select=" $stateVar/scpd:dataType"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsd:element name="{scpd:name}"
                                        type="{concat('xsd:',$type)}"
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

    <!-- ======== messages ======== -->
    <xsl:template name="createMsgs">
        <xsl:for-each select=".//scpd:action">
            <xsl:variable name="name" select="scpd:name"/>

            <wsdl:message name="{concat('IIoTDeviceWSService_', $name,'_InputMessage')}">
                <wsdl:part name="parameters" element="{concat('tns:', $name)}"/>
            </wsdl:message>
            <wsdl:message name="{concat('IIoTDeviceWSService_', $name,'_OutputMessage')}">
                <wsdl:part name="parameters" element="{concat('tns:', $name,'Response')}"/>
            </wsdl:message>

        </xsl:for-each>

    </xsl:template>

    <!-- ============== IoT device operations ==================== -->
    <xsl:template name="createOperations">

        <!--wsdl:portType name="{concat('IIoTDeviceWSService',@IoT:serviceid)}"-->
          <wsdl:portType name="IIoTDeviceWSService">
            
            <xsl:for-each select=".//scpd:action">
                <xsl:variable name="name" select="scpd:name"/>

                <wsdl:operation name="{$name}">
                    <wsdl:input
                        wsaw:Action="{concat('http://tempuri.org/IIoTDeviceWSService/',$name)}"
                        message="{concat('tns:IIoTDeviceWSService_',$name,'_InputMessage')}"/>
                    <wsdl:output
                        wsaw:Action="{concat('http://tempuri.org/IIoTDeviceWSService/',$name, 'Response')}"
                        message="{concat('tns:IIoTDeviceWSService_',$name,'_OutputMessage')}"/>
                </wsdl:operation>
            </xsl:for-each>
        </wsdl:portType>

    </xsl:template>

    <xsl:template name="createBindings">
        <xsl:param name="serviceID"/>
        <xsl:for-each select=".//scpd:action">
            <xsl:variable name="name" select="scpd:name"/>
            <wsdl:operation name="{$name}">
                <soap:operation soapAction="{concat($serviceID, '#',$name)}" style="document"/>
                <wsdl:input>
                    <soap:body use="literal"/>
                </wsdl:input>
                <wsdl:output>
                    <soap:body use="literal"/>
                </wsdl:output>
            </wsdl:operation>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
