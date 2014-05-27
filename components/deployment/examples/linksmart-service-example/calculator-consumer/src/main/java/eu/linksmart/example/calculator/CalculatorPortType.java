package eu.linksmart.example.calculator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.0
 * 2014-05-09T11:47:50.736+02:00
 * Generated source version: 2.6.0
 * 
 */
@WebService(targetNamespace = "http://calculator.example.linksmart.eu/", name = "CalculatorPortType")
@XmlSeeAlso({ObjectFactory.class})
public interface CalculatorPortType {

    @WebResult(name = "return", targetNamespace = "http://calculator.example.linksmart.eu/")
    @RequestWrapper(localName = "add", targetNamespace = "http://calculator.example.linksmart.eu/", className = "eu.linksmart.example.calculator.Add")
    @WebMethod
    @ResponseWrapper(localName = "addResponse", targetNamespace = "http://calculator.example.linksmart.eu/", className = "eu.linksmart.example.calculator.AddResponse")
    public int add(
        @WebParam(name = "arg0", targetNamespace = "http://calculator.example.linksmart.eu/")
        int arg0,
        @WebParam(name = "arg1", targetNamespace = "http://calculator.example.linksmart.eu/")
        int arg1
    );
}
