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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.filesystemdevice.client;



public interface FileSystemDeviceLimboClientPort {

		public java.lang.String clearFile(java.lang.String path );

	public java.lang.String copy(java.lang.String source, java.lang.String destination );

	public java.lang.String createDirectory(java.lang.String path );

	public java.lang.String createFile(java.lang.String path, java.lang.String properties );

	public java.lang.String existsPath(java.lang.String path );

	public java.lang.String getDirectoryEntries(java.lang.String path );

	public java.lang.String getFile(java.lang.String path );

	public java.lang.String getFreeSpace();

	public java.lang.String getID();

	public java.lang.String getSize();

	public java.lang.String getStatFS();

	public java.lang.String move(java.lang.String source, java.lang.String destination );

	public java.lang.String readFile(java.lang.String path, java.lang.String start, java.lang.String size );

	public java.lang.String removeDirectory(java.lang.String path, java.lang.Boolean recursive );

	public java.lang.String removeFile(java.lang.String path );

	public java.lang.String setFileProperties(java.lang.String path, java.lang.String properties );

	public java.lang.String setFileProperty(java.lang.String path, java.lang.String propertiesName, java.lang.String propertiesValue );

	public java.lang.String truncateFile(java.lang.String path, java.lang.String size );

	public java.lang.String writeFile(java.lang.String path, java.lang.String start, java.lang.String data );


	
}