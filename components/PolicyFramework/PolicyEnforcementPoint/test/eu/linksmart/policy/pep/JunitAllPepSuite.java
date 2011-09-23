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
package eu.linksmart.policy.pep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.linksmart.policy.pep.cache.impl.JunitMemPdpSessionMemoryTest;
import eu.linksmart.policy.pep.cache.impl.JunitPdpSessionBufferTest;
import eu.linksmart.policy.pep.cache.impl.JunitPdpSessionItemTest;
import eu.linksmart.policy.pep.impl.JunitComponentHidManagerTest;
import eu.linksmart.policy.pep.impl.JunitLinkSmartAttributeFactoryTest;
import eu.linksmart.policy.pep.impl.JunitPepApplicationTest;
import eu.linksmart.policy.pep.impl.JunitPepConfiguratorTest;
import eu.linksmart.policy.pep.impl.JunitPepXacmlConstantsTest;
import eu.linksmart.policy.pep.impl.JunitPropertyParserTest;
import eu.linksmart.policy.pep.request.impl.JunitDomSoapAttrParserTest;
import eu.linksmart.policy.pep.request.impl.JunitPepRequestTest;
import eu.linksmart.policy.pep.request.impl.JunitStaxSoapAttrParserTest;
import eu.linksmart.policy.pep.response.impl.JunitBundlePepObligationObserverWrapperTest;
import eu.linksmart.policy.pep.response.impl.JunitDebugPepObligationObserverTest;
import eu.linksmart.policy.pep.response.impl.JunitOsgiTrackerPepObligationObserverTest;
import eu.linksmart.policy.pep.response.impl.JunitSessionCachePepObligationObserverTest;

/**
 * Unit test suite for PEP
 * 
 * @author Marco Tiemann
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	JunitMemPdpSessionMemoryTest.class,
	JunitPdpSessionBufferTest.class,
	JunitPdpSessionItemTest.class,
	JunitComponentHidManagerTest.class,
	JunitLinkSmartAttributeFactoryTest.class,
	JunitPepApplicationTest.class,
	JunitPepConfiguratorTest.class,
	JunitPepXacmlConstantsTest.class,
	JunitPropertyParserTest.class,
	JunitDomSoapAttrParserTest.class,
	JunitPepRequestTest.class,
	JunitStaxSoapAttrParserTest.class,
	JunitBundlePepObligationObserverWrapperTest.class,
	JunitDebugPepObligationObserverTest.class,
	JunitOsgiTrackerPepObligationObserverTest.class,
	JunitSessionCachePepObligationObserverTest.class
})
public class JunitAllPepSuite {
	// intentionally left blank
}
