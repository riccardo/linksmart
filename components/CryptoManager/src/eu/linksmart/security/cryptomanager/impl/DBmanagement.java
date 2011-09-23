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
 * Copyright (C) 2006-2010 Fraunhofer SIT,
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

package eu.linksmart.security.cryptomanager.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Class for managing the CryptoManager database. <p> This implementation makes
 * use of Apache Derby as a DBMS.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
public class DBmanagement implements KeyStore.Entry {

	private final static Logger logger = Logger.getLogger(DBmanagement.class
			.getName());
	private static DBmanagement instance;
	private static Connection con = null;

	//
	/**
	 * Lazy singleton instantiation. <p> Instance is created upon the first call
	 * to this method.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static DBmanagement getInstance() throws SQLException {
		if (instance == null) {
			instance = new DBmanagement();
		}
		return instance;
	}

	/**
	 * Private constructor. <p> Initializes the database.
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	// --------------------------------
	private DBmanagement() throws SQLException {
		// Initialize the database
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver", true, this
					.getClass().getClassLoader());
			try {
				con =
						DriverManager
								.getConnection("jdbc:derby:cryptomanager_db;create=false;");
				con.setAutoCommit(true);
				con.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
			} catch (SQLException e) {
				// Database does not exist. Create it and run scripts
				con =
						DriverManager
								.getConnection("jdbc:derby:cryptomanager_db;create=true;");
				con.setAutoCommit(true);
				con.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
				logger.info("Creating Derby database "
						+ con.getMetaData().getURL());
				// executeScript(new File(Activator.CONFIGFOLDERPATH +
				// "/delete_cryptomanager_db.sql"));
				executeScript(new File(CryptoManagerImpl.CONFIGFOLDERPATH
						+ "/create_cryptomanager_db.sql"));
			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		}
	}

	/**
	 * Returns <code>true</code> if the given identifier already exists in the
	 * database.
	 * 
	 * @param identifier
	 * @return
	 * @throws SQLException
	 */
	public boolean identifierExists(String identifier) throws SQLException {
		if (identifier != null) {
			Statement stat2 = con.createStatement(); 
			ResultSet result =
					stat2.executeQuery("select * from identifiers WHERE identifier='"
							+ identifier.trim() + "'");
			if (result != null && result.next()) {
				logger.info("Identifier " + identifier + " exists");
				stat2.close();
				return true;
			} else {
				logger.info("Identifier " + identifier + " does not exist");
				stat2.close();
				return false;
			}
		}
		return false;
	}

	/**
	 * Stores an identifier into the database.
	 * 
	 * @param identifier
	 */
	public void storeIdentifier(String identifier) {
		try {
			Statement stat = con.createStatement();
			stat.execute("insert into identifiers(identifier) values ('"
					+ identifier + "')");
			logger.debug("Identifier was added to database: " + identifier);
			stat.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * Retrieves the keystore alias that is associated with an identifier and a
	 * certain algorithm. <p> If there is no entry for the given algorithms and
	 * identfier, this method returns null.<br> If there is no such identifier,
	 * this method also returns null.
	 * 
	 * @param identifier
	 * @param algorithm_id
	 * @return
	 * @throws SQLException
	 */
	public synchronized String getIdentifierAlias(String identifier,
			String algorithm_id) throws SQLException {
		if (identifier == null) {
			throw new NullPointerException("Identifier null");
		}

		// Check whether identifier with the selected algorithms exists
		String resultString = null;
		Statement stat = con.createStatement();
		try {
			String query;
			if (algorithm_id == null) {
				query =
						"SELECT alias FROM key_assoc WHERE identifier='"
								+ identifier + "'";
			} else {
				query =
						"select alias from key_assoc WHERE identifier='"
								+ identifier + "' AND algorithm_name='"
								+ algorithm_id + "'";
			}
			logger.debug(query);
			
			ResultSet result = stat.executeQuery(query);

			if (result != null) {
				result.next();
				resultString = result.getString(1);
			} else {
				logger.warn("DB does not contain an alias for identifier "
						+ identifier + " and algo " + algorithm_id);
				logger.debug("Table contents: ");
				query = "select * from key_assoc";
				logger.debug(query);
				ResultSet result2 = stat.executeQuery(query);
				ScriptExe.printResultSet(result2);

			}
		} catch (SQLException e) {
			logger.warn("Could not retrieve alias for identifier " + identifier
					+ ". SQL says " + e.getMessage());
		} finally {
			stat.close();
		}
		return resultString;
	}

	/**
	 * Retrieves a keystore alias that is associated with an identifier. <p> If
	 * there is no entry for the given identifier, this method returns null.<br>
	 * If there is no such identifier, this method also returns null.
	 * 
	 * @param identifier
	 * @return
	 * @throws SQLException
	 */
	public String getIdentifierAlias(String identifier) throws SQLException {
		return getIdentifierAlias(identifier, null);
	}

	/**
	 * 
	 * @param identifier
	 * @param algorithm_name
	 * @return
	 */
	public String setIdentifierAlias(String identifier, String algorithm_name) {
		String query = null;
		String algorithm_id = "";
		String alias = null;
		try {
			Statement stat = con.createStatement();
			// NICETOHAVE Slow command. Try to improve performance here
			// Create entry in algorithms table (if not existant)
			query =
					"INSERT INTO algorithms(name) VALUES ('" + algorithm_name
							+ "')";
			logger.debug(query);
			stat.execute(query);
			stat.close();
		} catch (SQLException e) {
			// This is probably okay
			logger.debug(e.getMessage());
		} 

		
		try {
			// Get id of that entry
			query =
					"SELECT algorithm_id FROM algorithms WHERE name LIKE '"
							+ algorithm_name + "'";
			logger.debug(query);
			Statement stat = con.createStatement();
			ResultSet result = stat.executeQuery(query);
			if (result != null) {
				result.next();
				algorithm_id = result.getString(1);
			}

			// Insert entry into keys table
			alias = identifier + '_' + algorithm_id;
			query =
					"INSERT INTO keys(alias, identifier, algorithm_id) VALUES ('"
							+ alias + "','" + identifier + "'," + algorithm_id
							+ ")";
			logger.debug(query);
			stat.execute(query);
			logger.info("An alias has been added to database: " + alias);
			stat.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return alias;

	}

	/**
	 * Executes an SQL script from an external file.
	 * 
	 * @param script
	 */
	public void executeScript(File script) {
		ScriptExe exe = new ScriptExe();
		try {
			exe.loadScript(script);
		} catch (IOException e) {
			logger.error("Could not execute script", e);
		}
	}

	/**
	 * Closes the database connection.
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		con.close();
		con = null;
	}

	private static class ScriptExe {

		/**
		 * Loads and executes an SQL script from a file.
		 * 
		 * @throws IOException
		 * @throws SQLException
		 */
		private final static char QUERY_ENDS = ';';

		protected void loadScript(File script) throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader(script));
			String line;
			StringBuffer query = new StringBuffer();
			boolean queryEnds = false;

			while ((line = reader.readLine()) != null) {
				if (isComment(line))
					continue;
				queryEnds = checkStatementEnds(line);
				query.append(line);

				if (queryEnds) {
					logger.debug("query->" + query);

					try {
						Statement stat = con.createStatement();
						stat.execute(query.toString().replace(";", ""));
						ResultSet resultSet = stat.getResultSet();
						printResultSet(resultSet);
						stat.close();
					} catch (SQLException e) {
						logger.warn("Failure when executing SQL statement: "
								+ e.getMessage(), e);
					}
					query.setLength(0);
				}

			}

		}

		/**
		 * Checks whether a string is an SQL comment.
		 * 
		 * @param line
		 * @return
		 */
		private boolean isComment(String line) {
			if ((line != null) && (line.length() > 0))
				return (line.trim().indexOf("--") == 0);
			return false;
		}

		private boolean checkStatementEnds(String s) {
			return (s.indexOf(QUERY_ENDS) != -1);
		}

		/**
		 * Prints out a result set to DEBUG log
		 * 
		 * @param resultSet
		 */
		public static void printResultSet(ResultSet resultSet) {
			try {
				if (resultSet != null) {
					for (int i = 1; i <= resultSet.getMetaData()
							.getColumnCount(); i++) {
						logger.debug(resultSet.getMetaData().getColumnName(i)
								+ "\t\t\t");
					}
					logger.debug("");
				}
				while (resultSet != null && resultSet.next()) {
					for (int i = 1; i <= resultSet.getMetaData()
							.getColumnCount(); i++) {
						logger.debug(resultSet.getString(i) + "\t\t");
					}
					logger.debug("\n");
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	public synchronized String[] getAllIdentifier() throws SQLException {

		Vector<String> resultVector = new Vector<String>();

		try {
			String query = "SELECT * FROM key_assoc";
			Statement stat = con.createStatement();
			ResultSet resultSet = stat.executeQuery(query);

			while (resultSet != null && resultSet.next()) {
				resultVector.add(resultSet.getString("identifier"));
			}
			stat.close();

		} catch (SQLException e) {
			logger.warn("Could not retrieve any identifer. SQL says "
					+ e.getMessage());
		}

		return (String[]) resultVector.toArray(new String[resultVector.size()]);

	}

	public synchronized Vector<Vector<String>> getAllIdentifierInfo()
			throws SQLException {

		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<String> infoSet;

		try {
			String query = "SELECT * FROM key_assoc";
			Statement stat = con.createStatement();
			ResultSet resultSet = stat.executeQuery(query);

			while (resultSet != null && resultSet.next()) {
				infoSet = new Vector<String>();
				infoSet.add(resultSet.getString("identifier"));
				infoSet.add(resultSet.getString("algorithm_name"));

				result.add(infoSet);
			}
			stat.close();

		} catch (SQLException e) {
			logger.warn("Could not retrieve any identifer. SQL says "
					+ e.getMessage());
		}

		return result;

	}

	public void removeTableEntry(String identifier) {

		String query =
				"delete from identifiers WHERE identifier='" + identifier + "'";
		// Entries in table "keys" should be deleted automatically
		logger.debug(query);
		try {
			Statement stat = con.createStatement();
			stat.execute(query);
			stat.close();
		} catch (SQLException e) {
			logger.error(e);
		}

	}
}
