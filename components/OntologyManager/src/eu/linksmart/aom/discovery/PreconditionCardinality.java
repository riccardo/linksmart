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
 * Copyright (C) 2006-2010 Technical University of Kosice
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

package eu.linksmart.aom.discovery;

/**
 * Creates the precondition cardinality constraints from string.
 * The cardinality syntax is as follows:
 * Exactly : cardinality="3"    generates: exactly=3
 * Interval: cardinality="3..5" generates: min=3, max=5  
 * Interval: cardinality="3..M" generates: min=3, max=-1 (means at least 3)
 * 
 * Any other syntax is wrong and generates: min=0, max=-1 (means at least 1)
 * 
 * @author Peter Kostelnik
 */
public class PreconditionCardinality{
  public int exactly = -1;
  public int min = 0;
  public int max = -1;
  
  private void createDefault(){
    this.exactly = -1;
    this.min = 0;
    this.max = -1;
  }
  private void parseExactly(String cardinality){
    try{
      this.exactly = Integer.parseInt(cardinality.trim());
    }
    catch(Exception e){
      createDefault();
    }
  }
  private void parseInterval(String min, String max){
    try{
      this.min = Integer.parseInt(min.trim());
      if(max.trim().equalsIgnoreCase("m")){
        this.max = -1;
      }
      else{
        this.max = Integer.parseInt(max.trim());
      }
    }
    catch(Exception e){
      createDefault();
    }
  }
  public PreconditionCardinality(String cardinality){
    String[] parts = cardinality.split("\\.\\.");
    
    if(parts.length == 1){
      parseExactly(parts[0]);
    }
    else if(parts.length == 2){
      parseInterval(parts[0], parts[1]);
    }
  }

  /**
   * Applies the precondition satisfaction rules to the number and evaluates the precondition. 
   * @param number Number to be evaluated against the precondition.
   * @return Flag, if precondition is satisfied.
   */
  public boolean isSatisfied(int number){
    if(this.exactly != -1) {
    	return (this.exactly == number);
    }
    
    if(this.min != -1 && this.max != -1){
      return (number >= this.min && number <= this.max);
    }
    
    if(this.max == -1){
      return (number >= this.min);
    }
    return false;
  }
  
  @Override 
  public String toString(){
    return "[Precondition: [exactly="+this.exactly+"][min="+this.min+"][max="+this.max+"]]";
  }

}

