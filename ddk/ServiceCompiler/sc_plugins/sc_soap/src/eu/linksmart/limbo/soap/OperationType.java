/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.limbo.soap;


import java.util.List;

/**
 * <b>Class OperationType</b>
 * This class defines a Operation as defined by the WSDL file,
 * with an inputValue, an outputValue, the corresponding SOAPAction,
 * the inputParts(i.e. arguments) of the operation and the SOAPBody of
 * a SOAP message to this Operation (Request).
 * The methods in this class are basic selectors and modifiers.
 * 
 * see also: Parts.java
 * 
 */
public class OperationType {
	
	/**
	 * <b>opName</b> : The Operation Name. 
	 */
	private String opName;
	
	/**
	 * <b>inputValue</b> : The input value of this operation.
	 */
	private String inputValue;
	
	/**
	 * <b>outputValue</b> : The output value of this operation.
	 */
	private String outputValue;
	
	/**
	 * <b>SOAPAction</b> : The SOAP action of this operation.
	 */
	private String SOAPAction;
	
	/**
	 * <b>SOAPBody</b> : The SOAP body of this operation.
	 */
	private String SOAPBody;
	
	/**
	 * <b>parts</b> : List containing all the parts of this operation.
	 */
	private List<Parts> parts;
	
	/**
	 * <b>argumentLine</b> : String specifying the arguments of this operation.
	 */
	private String argumentLine;
	
	/**
	 * <b>outputType</b> : String specifying the output data type of this operation.
	 */
	private String outputType;
	
	/**
	 * <b>clientReturnLine</b> : client return line of this operation.
	 */
	private String clientReturnLine;
	
	/**
	 * <b>clientDefaultReturnLine</b> : default client return line of this operation. 
	 */
	private String clientDefaultReturnLine;
	
	/**
	 * <b>outputName</b> : Name of the output of this operation.
	 */
	private String outputName;
	
	private String operationPort;
	/**
	 * <b>OperationType</b>
	 * @param theOpName String specifying the operation name.
	 * @param theInput String specifying the input name.
	 * @param theOutput String specifying the output name.
	 */
	public OperationType(String operationPort, String theOpName, String theInput, String theOutput){
		this.opName = theOpName;
		this.inputValue = theInput;
		this.outputValue = theOutput;
		this.operationPort = operationPort;
	}
	
	public String getOperationPort(){
		return this.operationPort;
	}
	
	
	
	/**
	 * <b>getOpName</b>
	 * Returns the operation name.
	 * @return String representing the operation name.
	 */
	public String getOpName(){
		return this.opName;
	}
	
	/**
	 * <b>getInput</b>
	 * Returns the input name of this operation.
	 * @return String specifying the input value name of this operation.
	 */
	public String getInput(){
		return this.inputValue;
	}
	
	/**
	 * <b>getOutput</b>
	 * Returns the output name of this operation.
	 * @return String specifying the output value name of this operation.
	 */
	public String getOutput(){
		return this.outputValue;
	}
	
	/**
	 * <b>getSOAPAction</b>
	 * Returns the SOAPAction of this operation.
	 * @return String specifying the SOAPAction of this operation.
	 */
	public String getSOAPAction(){
		return this.SOAPAction;
	}
	
	/**
	 * <b>getSOAPBody</b>
	 * Returns the SOAPBody of this operation.
	 * @return String specifying the SOAPBody of this operation.
	 */
	public String getSOAPBody(){
		return this.SOAPBody;
	}
	
	/**
	 * <b>getInputParts</b>
	 * Returns all the input parts of this operation.
	 * @return a List with all the Input Parts of this operation.
	 */
	public List<Parts> getInputParts(){
		return this.parts;
	}
	
	/**
	 * <b>getArgumentLine</b>
	 * Returns the argument line of this operation.
	 * @return field argumentLine.
	 */
	public String getArgumentLine(){
		return this.argumentLine;
	}
	
	/**
	 * <b>getOutputType</b>
	 * Returns the output type of this operation.
	 * @return field outputType.
	 */
	public String getOutputType(){
		return this.outputType;
	}
	
	/**
	 * <b>getOutputName</b>
	 * Returns the output name of this operation.
	 * @return field outputName.
	 */
	public String getOutputName(){
		return this.outputName;
	}
	
	/**
	 * <b>getClientReturnLine</b>
	 * Returns the client return line of this operation.
	 * @return field clientReturnLine
	 */
	public String getClientReturnLine(){
		return this.clientReturnLine;
	}
	
	/**
	 * <b>getClientDefaultReturnLine</b>
	 * Returns the client default return line.
	 * @return field clientDefaultReturnLine.
	 */
	public String getClientDefaultReturnLine(){
		return this.clientDefaultReturnLine;
	}
	
	/**
	 * <b>setSOAPAction</b>
	 * Sets the SOAPAction of this operation to the given value.
	 * @param theSOAPAction String representing the new SOAPAction. 
	 */
	public void setSOAPAction(String theSOAPAction){
		this.SOAPAction = theSOAPAction;
	}
	
	/**
	 * <b>setSOAPBody</b>
	 * Sets the SOAPBody of this operation to the given value.
	 * @param theSOAPBody String representing the new SOAPBody. 
	 */
	public void setSOAPBody(String theSOAPBody){
		this.SOAPBody = theSOAPBody;
	}
	
	/**
	 * <b>setInputParts</b>
	 * Sets the input parts List of this operation to the given value. 
	 * @param theParts List representing the new inputParts.
	 */
	public void setInputParts(List<Parts> theParts){
		this.parts = theParts;
	}
	
	/**
	 * <b>setOutputType</b>
	 * Sets the output type of this operation to the given value.
	 * @param theOutputType String representing the new outputType.
	 */
	public void setOutputType(String theOutputType){
		this.outputType = theOutputType;
	}
	
	/**
	 * <b>setOutputName</b>
	 * Sets the output name of this operation to the given value.
	 * @param theOutputName String specifying the new outputName.
	 */
	public void setOutputName(String theOutputName){
		this.outputName = theOutputName;
	}
	
	/**
	 * <b>setInputArgumentsLine</b>
	 * Sets the input arguments line of this operation.
	 * @param theParts list of parts.
	 */
	public void setInputArgumentsLine(List<Parts> theParts){
		this.argumentLine = "";
		for(int i=0; i<theParts.size(); i++){
			if(i==theParts.size()-1)
				this.argumentLine = this.argumentLine.concat(theParts.get(i).getType()+" "+theParts.get(i).getName());
			else
				this.argumentLine = this.argumentLine.concat(theParts.get(i).getType()+" "+theParts.get(i).getName()+", ");
		}
	}
	
	/**
	 * <b>setClientResultLines</b>
	 * Sets the client result line and the client defaul result line to the given values.
	 * @param theClientResultLine the new theClientResultLine.
	 * @param theClientDefaultResultLine the new theClientDefaultResultLine.
	 */
	public void setClientResultLines(String theClientResultLine, String theClientDefaultResultLine){
		this.clientReturnLine = theClientResultLine;
		this.clientDefaultReturnLine = theClientDefaultResultLine;
	}
	
	public String getResultLine() {
		String resultLine;
		if(this.getOutputType().equalsIgnoreCase("int") || this.getOutputType().equalsIgnoreCase("java.lang.Integer"))
			resultLine = "return 0;";
		else if((this.getOutputType().equalsIgnoreCase("float"))||this.getOutputType().equalsIgnoreCase("java.lang.Float") || 
				(this.getOutputType().equalsIgnoreCase("double"))||this.getOutputType().equalsIgnoreCase("java.lang.Double") || 
				(this.getOutputType().equalsIgnoreCase("long"))|| this.getOutputType().equalsIgnoreCase("java.lang.Long"))
			resultLine = "return 0.0;";
		else if(this.getOutputType().equalsIgnoreCase("boolean") || this.getOutputType().equalsIgnoreCase("java.lang.Boolean"))
			resultLine = "return false;";
		else if(this.getOutputType().equalsIgnoreCase("void"))
			resultLine = "";
		else
			resultLine = "return null;";
		return resultLine;
	}
	
}
