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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pdp.finder.impl;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.MatchResult;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

import eu.linksmart.policy.pdp.admin.impl.FileSystemPdpAdminService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>File-based {@link PolicyFinderModule} implementation</p>
 * 
 * @author Michael Crouch
 * @author Marco  Tiemann
 */
public class LocalFolderPolicyFinderModule extends PolicyFinderModule {
	
	/** logger */
	private static final Logger logger 
			= Logger.getLogger(LocalFolderPolicyFinderModule.class);

	/** {@link DocumentBuilder} */
	private DocumentBuilder xmlDocBuilder = null;
	
	/** {@link PolicyFinder} */
    private PolicyFinder finder = null;
    
    /** policy file directory */
    private File policyDir = null;
    
    /** {@link LocalPolicyCache} policy file cache */
    private LocalPolicyCache cache = new LocalPolicyCache();
    
    /**
     * Constructor
     * 
     * @param thePdpAdmin
     * 				the {@link FileSystemPdpAdminService}
     */
    public LocalFolderPolicyFinderModule(
    		FileSystemPdpAdminService thePdpAdmin) {
    	super();
    	policyDir = thePdpAdmin.getActivePolicyFolder();
   		DocumentBuilderFactory factory 
   				= DocumentBuilderFactory.newInstance();
   		factory.setIgnoringComments(true);
   		factory.setIgnoringElementContentWhitespace(true);
   		// we are namespace aware
   		factory.setNamespaceAware(true);
   		// we are not doing any validation
   		factory.setValidating(false);
   		try {
   			xmlDocBuilder = factory.newDocumentBuilder();
   		} catch (ParserConfigurationException pce) {
   			logger.error("Incorrect parser configuration: " 
   					+ pce.getLocalizedMessage());
   			if (logger.isDebugEnabled()) {
   				logger.debug("Stack trace: ", pce);
   			}
   		}
    }

    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#init(
     * 		com.sun.xacml.finder.PolicyFinder)
     */
    @Override
	public void init(PolicyFinder theFinder) {
        finder = theFinder;
    }
    
    /**
     * Outputs policy file names to log
     * 
     * @param thePolicyDir
     * 				policy directory
     */
    public void listFilePolicies(File thePolicyDir) {
    	if (thePolicyDir.isDirectory()) {
    		// print all policies in dir
            File[] policies = thePolicyDir.listFiles();
            if (logger.isInfoEnabled()) {
            	logger.info("Active Policy Folder has " + policies.length 
            			+ " policies");
            }
            int pl = policies.length;
            for (int i=0; i < pl; i++) {
            	File pol = policies[i];
            	if (logger.isInfoEnabled()) {
            		logger.info("Policy " + (i + 1) + ": " + pol.getName());
            	}
            }
    	}
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#findPolicy(
     * 		com.sun.xacml.EvaluationCtx)
     */
    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx theContext) {        
        if (policyDir == null) {
            return new PolicyFinderResult();
        }
        AbstractPolicy selectedPolicy = null;        
        // get list of policies in directory
        File[] policies = policyDir.listFiles();
        if (policies == null) {
        	policies = new File[0];
        }
        int pl = policies.length;
        for (int i=0; i < pl; i++) {
            File policy = policies[i];
            AbstractPolicy abPolicy = loadPolicy(policy);
            if (abPolicy != null) {
                // see if we match
                MatchResult match = abPolicy.match(theContext);
                int result = match.getResult();
                // if there was an error, we stop right away
                if (result == MatchResult.INDETERMINATE) {
                    return new PolicyFinderResult(match.getStatus());
                }
                if (result == MatchResult.MATCH) {
                    // if we matched before, this is an error                	
                    if (selectedPolicy != null) {
                    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    	selectedPolicy.encode(baos);
                    	baos.reset();
                    	abPolicy.encode(baos);
                        ArrayList<String> code = new ArrayList<String>();
                        code.add(Status.STATUS_PROCESSING_ERROR);
                        Status status = new Status(code, 
                        		"too many applicable top-level policies");
                        return new PolicyFinderResult(status);
                    }
                    // otherwise remember this policy
                    selectedPolicy = abPolicy;
                }
            }
        }
        // if we found a policy, return it, otherwise we're N/A
        if (selectedPolicy != null) {
            return new PolicyFinderResult(selectedPolicy);
        }
        return new PolicyFinderResult();
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#isIdReferenceSupported()
     */
    @Override
    public boolean isIdReferenceSupported() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#isRequestSupported()
     */
    @Override
    public boolean isRequestSupported() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return getClass().getName();
    }    
    
    
    /**
     * <p>Local policy file cache</p>
     * 
     * @author Marco Tiemann
     *
     */
    class LocalPolicyCache {    	
    	
    	/** policy cache list */
    	private ArrayList<PolicyCacheElement> policies 
    			= new ArrayList<PolicyCacheElement>();
    	
    	/**
    	 * @param theFileName
    	 * 			the file name
    	 * @param theDate
    	 * 			the last modified date
    	 * @return
    	 * 			{@link AbstractPolicy} or <code>null</code>
    	 */
    	public AbstractPolicy query(String theFileName, long theDate) {
    		PolicyCacheElement qe 
    				= new PolicyCacheElement(theFileName, theDate);
    		// this only compares by file name
    		int p = Collections.binarySearch(policies, qe);
    		// date check follows
    		if (p >= 0) {
    			PolicyCacheElement pce = policies.get(p);
    			if (pce.date == theDate) {
    				return policies.get(p).policy;
    			} 
    			// policy is somehow outdated, remove from cache
    			pce = null;
    			policies.remove(p);    			
    		}
    		return null;
    	}
    	
    	/**
    	 * @param theFileName
    	 * 			the file name
    	 * @param theDate
    	 * 			the last modified date
    	 * @param thePolicy
    	 * 			the {@link AbstractPolicy}
    	 */
    	public void add(String theFileName, long theDate, 
    			AbstractPolicy thePolicy) {
    		PolicyCacheElement ie 
					= new PolicyCacheElement(theFileName, theDate, thePolicy);
    		int p = Collections.binarySearch(policies, ie);
    		if (p >= 0) {
    			policies.set(p, ie);
    		} else {
    			policies.add((-p - 1), ie);
    		}
    	}
    	
    	/** clears cache */
    	public void flush() {
    		policies.clear();
    	}
    	
    }
    
    /**
     * Loads a policy from a file location
     * 
     * @param thePolicy
     * 				the policy <code>File</code> location
     * @return
     * 				the loaded {@link AbstractPolicy} or <code>null</code>
     */
    private AbstractPolicy loadPolicy(File thePolicy) {
    	if (xmlDocBuilder == null) {
			logger.error("DocumentBuilder not initialized, returning null");
			return null;
    	}
    	String path = thePolicy.getName();
    	long lastModified = thePolicy.lastModified();
		AbstractPolicy pol = cache.query(path, lastModified);					
		if (pol != null) {
			return pol;
		}
        try {
        	Document doc = xmlDocBuilder.parse(
        			new BufferedInputStream(new FileInputStream(thePolicy)));
            // handle the policy if it is a known type
            Element root = doc.getDocumentElement();
            String name = root.getTagName();
            if ("Policy".equals(name)) {
            	try {
            		pol = Policy.getInstance(root);
            		if (path != null) {
            			cache.add(path, lastModified, pol);
            		}
            		return pol;
            	} catch (IllegalArgumentException iae) {
            		logger.warn("Policy could not be represented internally");
            		return null;
            	}
            }
            if ("PolicySet".equals(name)) {
            	pol = PolicySet.getInstance(root, finder);
            	if (path != null) {
            		cache.add(path, lastModified, pol);
            	}
                return pol;
            }
            // this is not a root type that we know how to handle
            logger.warn("Cannot handle policy root type: "
            		+ root.getLocalName());
            return null;                
        } catch (SAXParseException spe) {
        	logger.warn("Exception when parsing policy " + thePolicy.getName()
        			+ ": " + spe.getLocalizedMessage());
		} catch (FileNotFoundException fnfe) {
			logger.error("Policy file " + thePolicy.getName() + " not found");
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", fnfe);
			}
		} catch (SAXException se) {
			logger.error("SAX exception occured: " + se.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", se);
			}
		} catch (IOException ioe) {
			logger.error("I/O exception occured: " + ioe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ioe);
			}
		} catch (ParsingException pe) {
			logger.error("Parsing exception occured: " 
					+ pe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", pe);
			}
		}
		// catch-all null return
    	return null;
    }
    
    
    /**
     * <p>Policy cache element</p>
     * 
     * @author Marco Tiemann
     *
     */
    private class PolicyCacheElement implements Comparable<PolicyCacheElement> {
    	
    	/** {@link AbstractPolicy} */
    	public AbstractPolicy policy = null;
    	
    	/** file name */
    	public String fileName = null;
    	
    	/** date */
    	public long date;
    	
    	/**
    	 * Constructor
    	 * 
    	 * @param theFileName
    	 * 			the file name
    	 * @param theDate
    	 * 			the date
    	 */
    	public PolicyCacheElement(String theFileName, long theDate) {
    		super();
    		fileName = theFileName;
    		date = theDate;
    	}
    	
    	/**
    	 * @param theFileName
    	 * 			the file name
    	 * @param theDate
    	 * 			the date
    	 * @param thePolicy
    	 * 			the {@link AbstractPolicy}
    	 */
    	public PolicyCacheElement(String theFileName, long theDate, 
    			AbstractPolicy thePolicy) {
    		super();
    		fileName = theFileName;
    		date = theDate;
    		policy = thePolicy;
    	}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PolicyCacheElement theOther) {
			if (theOther == null) {
				return -1;
			}
			if (fileName == null) {
				if (theOther.fileName == null) {
					return 0;
				} 
				return 1;
			}
			if (theOther.fileName == null) {
				return -1;
			}
			return fileName.compareTo(theOther.fileName);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Long.valueOf(date).intValue();
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object theObj) {
			if (this == theObj) {
				return true;
			}
			if (theObj == null) {
				return false;
			}
			if (getClass() != theObj.getClass()) {
				return false;
			}
			PolicyCacheElement other = (PolicyCacheElement) theObj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (date != other.date) {
				return false;
			}
			if (fileName == null) {
				if (other.fileName != null) {
					return false;
				}
			} else if (!fileName.equals(other.fileName)) {
				return false;
			}
			return true;
		}

		/**
		 * @return
		 * 			the outer type
		 */
		private LocalFolderPolicyFinderModule getOuterType() {
			return LocalFolderPolicyFinderModule.this;
		}
    	
    }
    
}
