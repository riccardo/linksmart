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
package eu.linksmart.upnp;

import java.util.Date;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPStateVariable;

/**
 * <b>Class UPnPStateVariableImpl</b>
 * This class represents the information of a state variable as declared in the device's service state table (SST).
 * This class implements the org.osgi.service.upnp.UPnPStateVariable interface.
 *
 */
public class UPnPStateVariableImpl
{
  private static Hashtable<String, Class> upnp2javaTable = null;

  private static Hashtable<Class, String> java2upnpTable = null;

  private static Hashtable<String, Class> stringToClass = null;

  private static Hashtable<Class, String> classToString = null;
  private String variableName;
  private Class variableValue;
  private Number minimumValue;
  private Number maximumValue;
  private String[] allowedValues;
  private Number step;
  private String upnpType;
  private boolean isSendEvents;
  private String typeConstant;
  private String dataType;

  static
  {
    upnp2javaTable = new Hashtable(30);
    String[] upnpType = (String[])null;
    upnpType = new String[] { "ui1", "ui2", "i1", "i2", "i4", "int" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Integer.class);
    }

    upnpType = new String[] { "ui4", "time", "time.tz" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Long.class);
    }

    upnpType = new String[] { "r4", "float" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Float.class);
    }

    upnpType = new String[] { "r8", "number", "fixed.14.4", "double" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Double.class);
    }

    upnpType = new String[] { "char" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Character.class);
    }

    upnpType = new String[] { "string", "uri", "uuid" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], String.class);
    }

    upnpType = new String[] { "date", "dateTime", "dateTime.tz" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Date.class);
    }

    upnpType = new String[] { "boolean" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], Boolean.class);
    }

    upnpType = new String[] { "bin.base64", "bin.hex" };
    for (int i = 0; i < upnpType.length; i++) {
      upnp2javaTable.put(upnpType[i], byte.class);
    }

    java2upnpTable = new Hashtable();
    java2upnpTable.put(Integer.class, "int");
    java2upnpTable.put(Float.class, "float");
    java2upnpTable.put(Double.class, "number");
    java2upnpTable.put(Long.class, "ui4");
    java2upnpTable.put(String.class, "string");
    java2upnpTable.put(byte.class, "bin.base64");
    java2upnpTable.put(Boolean.class, "boolean");
    java2upnpTable.put(Character.class, "char");
    java2upnpTable.put(Date.class, "date");

    stringToClass = new Hashtable(30);
    stringToClass.put("Integer", Integer.class);
    stringToClass.put("Float", Float.class);
    stringToClass.put("Double", Double.class);
    stringToClass.put("Long", Long.class);
    stringToClass.put("String", String.class);
    stringToClass.put("Byte[]", byte.class);
    stringToClass.put("Boolean", Boolean.class);
    stringToClass.put("Char", Character.class);
    stringToClass.put("Date", Date.class);

    classToString = new Hashtable(30);
    classToString.put(Integer.class, "Integer");
    classToString.put(Float.class, "Float");
    classToString.put(Double.class, "Double");
    classToString.put(Long.class, "Long");
    classToString.put(String.class, "String");
    classToString.put(byte.class, "Byte[]");
    classToString.put(Boolean.class, "Boolean");
    classToString.put(Character.class, "Char");
    classToString.put(Date.class, "Date");
  }

  public UPnPStateVariableImpl(String variableName, Class value, Number minimumValue, Number maximumValue, String[] allowedValues, Number step, boolean isSendEvents)
  {
    this.variableName = variableName;
    this.variableValue = value;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
    this.allowedValues = allowedValues;
    this.step = step;
    this.isSendEvents = isSendEvents;
  }

  public String getName()
  {
    return this.variableName;
  }

  public void setTypeConstant(String constant) {
    this.typeConstant = constant;
  }

  public String getTypeConstant() {
    return this.typeConstant;
  }

  public Class getJavaDataType()
  {
    return (Class)upnp2javaTable.get(this.variableValue.getClass());
  }

  public Class getType() {
    return this.variableValue;
  }

  public static Class getJavaDataType(String type)
  {
    return (Class)upnp2javaTable.get(type);
  }

  public String getUPnPType()
  {
    return (String)java2upnpTable.get(this.variableValue);
  }

  public String getUPnPDataType()
  {
    return this.variableValue.getClass().toString();
  }

  public String getVarDataType()
  {
    String result = (String)classToString.get(this.variableValue);
    if (result == null) {
      return "complex";
    }
    return result;
  }

  public static Class getClassType(String name)
  {
    return (Class)stringToClass.get(name);
  }

  public Object getDefaultValue()
  {
    return null;
  }

  public String[] getAllowedValues()
  {
    if (this.allowedValues != null) {
      return this.allowedValues;
    }
    return null;
  }

  public Number getMinimum()
  {
    return this.minimumValue;
  }

  public Number getMaximum()
  {
    return this.maximumValue;
  }

  public Number getStep()
  {
    return this.step;
  }

  public boolean sendsEvents()
  {
    return this.isSendEvents;
  }

  public String getDataType()
  {
    return this.dataType;
  }

  public void setDataType(String dataType)
  {
    this.dataType = dataType;
  }
}