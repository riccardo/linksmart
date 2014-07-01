/*

In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
   of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
   such damages shall be statute barred within 12 months subsequent to the delivery of the software.
4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
   and consequential damages - except in cases of intent - is excluded.
This limitation of liability shall also apply if this license agreement shall be subject to law 
stipulating liability clauses corresponding to German law.


GNU LESSER GENERAL PUBLIC LICENSE

Version 3, 29 June 2007

Copyright © 2007 Free Software Foundation, Inc. <http://fsf.org/>

Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.

This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
0. Additional Definitions.

As used herein, “this License” refers to version 3 of the GNU Lesser General Public License, and the “GNU GPL” refers to version 3 of the GNU General Public License.

“The Library” refers to a covered work governed by this License, other than an Application or a Combined Work as defined below.

An “Application” is any work that makes use of an interface provided by the Library, but which is not otherwise based on the Library. Defining a subclass of a class defined by the Library is deemed a mode of using an interface provided by the Library.

A “Combined Work” is a work produced by combining or linking an Application with the Library. The particular version of the Library with which the Combined Work was made is also called the “Linked Version”.

The “Minimal Corresponding Source” for a Combined Work means the Corresponding Source for the Combined Work, excluding any source code for portions of the Combined Work that, considered in isolation, are based on the Application, and not on the Linked Version.

The “Corresponding Application Code” for a Combined Work means the object code and/or source code for the Application, including any data and utility programs needed for reproducing the Combined Work from the Application, but excluding the System Libraries of the Combined Work.
1. Exception to Section 3 of the GNU GPL.

You may convey a covered work under sections 3 and 4 of this License without being bound by section 3 of the GNU GPL.
2. Conveying Modified Versions.

If you modify a copy of the Library, and, in your modifications, a facility refers to a function or data to be supplied by an Application that uses the facility (other than as an argument passed when the facility is invoked), then you may convey a copy of the modified version:

    a) under this License, provided that you make a good faith effort to ensure that, in the event an Application does not supply the function or data, the facility still operates, and performs whatever part of its purpose remains meaningful, or
    b) under the GNU GPL, with none of the additional permissions of this License applicable to that copy.

3. Object Code Incorporating Material from Library Header Files.

The object code form of an Application may incorporate material from a header file that is part of the Library. You may convey such object code under terms of your choice, provided that, if the incorporated material is not limited to numerical parameters, data structure layouts and accessors, or small macros, inline functions and templates (ten or fewer lines in length), you do both of the following:

    a) Give prominent notice with each copy of the object code that the Library is used in it and that the Library and its use are covered by this License.
    b) Accompany the object code with a copy of the GNU GPL and this license document.

4. Combined Works.

You may convey a Combined Work under terms of your choice that, taken together, effectively do not restrict modification of the portions of the Library contained in the Combined Work and reverse engineering for debugging such modifications, if you also do each of the following:

    a) Give prominent notice with each copy of the Combined Work that the Library is used in it and that the Library and its use are covered by this License.
    b) Accompany the Combined Work with a copy of the GNU GPL and this license document.
    c) For a Combined Work that displays copyright notices during execution, include the copyright notice for the Library among these notices, as well as a reference directing the user to the copies of the GNU GPL and this license document.
    d) Do one of the following:
        0) Convey the Minimal Corresponding Source under the terms of this License, and the Corresponding Application Code in a form suitable for, and under terms that permit, the user to recombine or relink the Application with a modified version of the Linked Version to produce a modified Combined Work, in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.
        1) Use a suitable shared library mechanism for linking with the Library. A suitable mechanism is one that (a) uses at run time a copy of the Library already present on the user's computer system, and (b) will operate properly with a modified version of the Library that is interface-compatible with the Linked Version.
    e) Provide Installation Information, but only if you would otherwise be required to provide such information under section 6 of the GNU GPL, and only to the extent that such information is necessary to install and execute a modified version of the Combined Work produced by recombining or relinking the Application with a modified version of the Linked Version. (If you use option 4d0, the Installation Information must accompany the Minimal Corresponding Source and Corresponding Application Code. If you use option 4d1, you must provide the Installation Information in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.)

5. Combined Libraries.

You may place library facilities that are a work based on the Library side by side in a single library together with other library facilities that are not Applications and are not covered by this License, and convey such a combined library under terms of your choice, if you do both of the following:

    a) Accompany the combined library with a copy of the same work based on the Library, uncombined with any other library facilities, conveyed under the terms of this License.
    b) Give prominent notice with the combined library that part of it is a work based on the Library, and explaining where to find the accompanying uncombined form of the same work.

6. Revised Versions of the GNU Lesser General Public License.

The Free Software Foundation may publish revised and/or new versions of the GNU Lesser General Public License from time to time. Such new versions will be similar in spirit to the present version, but may differ in detail to address new problems or concerns.

Each version is given a distinguishing version number. If the Library as you received it specifies that a certain numbered version of the GNU Lesser General Public License “or any later version” applies to it, you have the option of following the terms and conditions either of that published version or of any later version published by the Free Software Foundation. If the Library as you received it does not specify a version number of the GNU Lesser General Public License, you may choose any version of the GNU Lesser General Public License ever published by the Free Software Foundation.

If the Library as you received it specifies that a proxy can decide whether future versions of the GNU Lesser General Public License shall apply, that proxy's public statement of acceptance of any version is permanent authorization for you to choose that version for the Library.
*/
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.IO;
using System.Diagnostics;
using System.Globalization;
using System.Data.SQLite;
using System.Threading;
using System.Data;
using System.Data.Common;

namespace EventStorageService
{
    /// <summary>
    /// This Class stores the failed event notification after it has passed the retry queue. Generic SQL databases are supported as well as a specific SqlLite interface that uses optimised database calls
    /// </summary>
    [ServiceBehavior(Name = "EventSubscriberService", Namespace = "http://eventmanager.linksmart.eu")]
    public class EventStorageService
    {
        DbConnection eventDB = null;
		private static string SelectDBDriver()
		{
			if (Type.GetType("Mono.Runtime") != null)
				return "Mono.Data.SQLite";
			else
				return "System.Data.SQLite";
		}
		public EventStorageService()
		{
			//SQLiteConnection cnn = new SQLiteConnection();
			DbProviderFactory fact = DbProviderFactories.GetFactory(SelectDBDriver());
			eventDB = fact.CreateConnection();
			//eventDB = cnn;
		}
        public void Init(string type)
        {
    
            switch (type)
            {
                case "seam4us":
                    try
                    {
                        //// Event database with Callback address (Endpoint or HID)
                        //eventDB.ConnectionString = "Data Source=EbbitsEventDB.s3db;Pooling=true;FailIfMissing=true;Journal Mode=Off";
                        // Event databese without callback address, just event topic
                        eventDB.ConnectionString = "Data Source=Seam4UsDB.s3db; Pooling=true; FailIfMissing=true; Journal Mode=Off";

                        eventDB.Open();
                    }
                    catch (Exception e) { Console.WriteLine("EventStorageService.Init"+e.Message); }
                    break;
                case "retry":
                    try
                    {
                        //// Event database with Callback address (Endpoint or HID)
                        //eventDB.ConnectionString = "Data Source=EbbitsEventDB.s3db;Pooling=true;FailIfMissing=true;Journal Mode=Off";
                        // Event databese without callback address, just event topic
                        eventDB.ConnectionString = "Data Source=EbbitsRetryDB.s3db; Pooling=true; FailIfMissing=true; Journal Mode=Off";

                        eventDB.Open();
                    }
                    catch (Exception e) { Console.WriteLine("EventStorageService.Init" + e.Message); }
                    break;
                case "ebbits":
                    try
                    {
                        //// Event database with Callback address (Endpoint or HID)
                        //eventDB.ConnectionString = "Data Source=EbbitsEventDB.s3db;Pooling=true;FailIfMissing=true;Journal Mode=Off";
                        // Event databese without callback address, just event topic
                        eventDB.ConnectionString = "Data Source=EventDB.s3db; Pooling=true; FailIfMissing=true; Journal Mode=Off";

                        eventDB.Open();
                    }
                    catch (Exception e) { Console.WriteLine("EventStorageService.Init" + e.Message); }
                    break;
                default:
                    try
                    {
                        //// Event database with Callback address (Endpoint or HID)
                        //eventDB.ConnectionString = "Data Source=EbbitsEventDB.s3db;Pooling=true;FailIfMissing=true;Journal Mode=Off";
                        // Event databese without callback address, just event topic
                        eventDB.ConnectionString = "Data Source=EventStoreDB.s3db; Pooling=true; FailIfMissing=false; Journal Mode=Off";

                        eventDB.Open();
                    }
                    catch (Exception e) { Console.WriteLine("EventStorageService.Init" + e.Message + e.StackTrace); }
                    break;
                  
            }
            CreateEventTableIfNotExists();
          
        }

        virtual public void CreateEventTableIfNotExists()
        {
            using (DbCommand cmd = eventDB.CreateCommand())
            {
                cmd.CommandText =
                 @"SELECT count(name) FROM sqlite_master WHERE type='table' AND tbl_name='Events';";
                int noTables = Convert.ToInt32(cmd.ExecuteScalar());
                if (0 == noTables)
                {
                    Console.WriteLine("Creating Event Table...");
                    cmd.CommandText = @" CREATE TABLE [Events] (
                Topic nvarchar(4000)
                , [ID]  nvarchar(200)
                , [Priority]  integer
                , stored_timestamp nvarchar(200)
                , IntId integer primary key  autoincrement);";
                    cmd.ExecuteNonQuery();

                    cmd.CommandText = @" CREATE TABLE [KeyValuePairs]  (
                 [EventId]  nvarchar(200)
                ,[Key] nvarchar(4000)
                , [Value] nvarchar(4000)                
                , IntId integer primary key  autoincrement);";
                    cmd.ExecuteNonQuery();
                }
            }
        }

        
        /// <summary>
        /// Static method for adding an entry to a log file including a time stamp
        /// </summary>
        /// <param name="logMessage">The message to be added to the logfile</param>
        public static void WriteLogFile(string logMessage)
        {
            lock (typeof(Object))
            {
                try
                {
                    CultureInfo ci = Thread.CurrentThread.CurrentCulture;
                    Thread.CurrentThread.CurrentCulture = CultureInfo.CreateSpecificCulture("sv-SE");

                    string fileName = "test.txt";

                    string logFolder = "";

                    FileStream w = File.Open(logFolder + fileName, System.IO.FileMode.Append, System.IO.FileAccess.Write, System.IO.FileShare.Write);
                    StreamWriter sw = new StreamWriter(w, System.Text.Encoding.Default);

                    sw.WriteLine("{0}", logMessage);
                    sw.Close();

                    Thread.CurrentThread.CurrentCulture = ci;
                }
                catch (Exception) { }
            }
        }
        /// <summary>
        /// Method for adding an event to the event database using generic database interfaces
        /// </summary>
        /// <param name="failedEventNotification">Failed event notification info</param>
        public void WriteDatabase(Components.Subscription failedEventNotification)
        {
            try
            {
                DbProviderFactory provider = DbProviderFactories.GetFactory(EventStorage.Properties.Settings.Default.ProviderName);
                string topic = failedEventNotification.Topic;
                string callbackAddress = null;
                if (failedEventNotification.HID != null)
                    callbackAddress = failedEventNotification.HID;
                else
                    callbackAddress = failedEventNotification.Endpoint;
                Nullable<DateTime> dateTime = failedEventNotification.DateTime;
                int priority = failedEventNotification.Priority;
                Components.Part[] parts = failedEventNotification.Parts;

                using (DbConnection connection = provider.CreateConnection())
                {
                    connection.ConnectionString = EventStorage.Properties.Settings.Default.DatabaseConnection;

                    DbCommand dbcommand = connection.CreateCommand();
                    dbcommand.Connection = connection;
                    dbcommand.CommandType = CommandType.Text;
                    connection.Open();

                    dbcommand.CommandText = "INSERT INTO [Events] (Topic) VALUES(@Topic); ";
                    dbcommand.CommandText += " SELECT CAST(@@IDENTITY AS int) AS [ID];";

                    AddParameter(ref dbcommand, "@Topic", DbType.String, topic);

                    int newID = (int)dbcommand.ExecuteScalar();

                    foreach (Components.Part current in parts)
                    {
                        DbCommand dbcommand2 = connection.CreateCommand();
                        dbcommand2.Connection = connection;
                        dbcommand2.CommandType = CommandType.Text;
                        dbcommand2.CommandText = "INSERT INTO [KeyValuePairs] ([EventId],[Key],[Value]) VALUES(@EventID, @Key,@Value); ";
                        string sVal = current.value;
                        if (sVal == null) sVal = "";
                        string sKey = current.key;
                        if (sKey == null) sKey = "";
                        AddParameter(ref dbcommand2, "@EventID", DbType.Int32, newID);
                        AddParameter(ref dbcommand2, "@Key", DbType.String, sKey);
                        AddParameter(ref dbcommand2, "@Value", DbType.String, sVal);
                        dbcommand2.ExecuteScalar();
                    }
                    connection.Close();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                WriteLogFile("SQLLite Error:" + e.Message);
            }
        }

        protected void AddParameter(ref DbCommand dbcommand, string paramenterName, DbType dbType, object value)
        {
            DbParameter keyParam = dbcommand.CreateParameter();
            keyParam.ParameterName = paramenterName;
            keyParam.DbType = dbType;
            keyParam.Value = value;
            dbcommand.Parameters.Add(keyParam);
        }

        /// <summary>
        /// Method for adding an failed event notification to the database optimised for the Opebn Source Database SqlLite. Stores the event by topic, not involving any endpoint or hid or anything connected to subscribers
        /// </summary>
        /// <param name="failedEventNotification">Failed event notification info</param>
        public void WriteDatabaseSqlLite(Components.Subscription failedEventNotification)
        {
            try
            {
                string topic = failedEventNotification.Topic;
                string endpoint = failedEventNotification.Endpoint;
                string hid = failedEventNotification.HID;
                Nullable<DateTime> dateTime = failedEventNotification.DateTime;
                int priority = failedEventNotification.Priority;
                Components.Part[] parts = failedEventNotification.Parts;

                string myId = System.Guid.NewGuid().ToString();
                DbCommand dbcommand = eventDB.CreateCommand();
                dbcommand.Connection = eventDB;
                dbcommand.CommandType = CommandType.Text;

                dbcommand.CommandText = "INSERT INTO [Events] ([ID],stored_timestamp,Topic,Priority) VALUES(@ID,@stored_timestamp,@Topic,@Priority); ";

                AddParameter(ref dbcommand, "@ID", DbType.String, myId);
                AddParameter(ref dbcommand, "@stored_timestamp", DbType.DateTime, dateTime);
                AddParameter(ref dbcommand, "@Topic", DbType.String, topic);
                AddParameter(ref dbcommand, "@Priority", DbType.Int32, priority);

                dbcommand.ExecuteScalar();
                dbcommand.CommandText = "INSERT INTO [CallbackAddress] (Endpoint,HID,EventID) VALUES(@Endpoint,@HID,@EventID); ";

                AddParameter(ref dbcommand, "@Endpoint", DbType.String, endpoint);
                AddParameter(ref dbcommand, "@HID", DbType.String, hid);
                AddParameter(ref dbcommand, "@EventID", DbType.String, myId);
                dbcommand.ExecuteScalar();

                foreach (Components.Part current in parts)
                {
                    DbCommand dbcommand2 = eventDB.CreateCommand();
                    dbcommand2.Connection = eventDB;
                    dbcommand2.CommandType = CommandType.Text;
                    dbcommand2.CommandText = "INSERT INTO [KeyValuePairs] ([EventId], [Key], [Value]) VALUES(@EventID, @Key, @Value); ";
                    string sVal = current.value;
                    if (sVal == null) sVal = "";
                    string sKey = current.key;
                    if (sKey == null) sKey = "";
                    AddParameter(ref dbcommand2, "@EventID", DbType.String, myId);
                    AddParameter(ref dbcommand2, "@Key", DbType.String, sKey);
                    AddParameter(ref dbcommand2, "@Value", DbType.String, sVal);
                    dbcommand2.ExecuteScalar();
                }
            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        public Guid WriteToDB(string eventTopic, Components.Part[] eventParts)
        {
            try
            {
                string topic = eventTopic;
                Nullable<DateTime> timestamp = DateTime.Now;
                Components.Part[] parts = eventParts;

                Guid guidId =  System.Guid.NewGuid();
                string myId = guidId.ToString();
                DbCommand dbcommand = eventDB.CreateCommand();
                dbcommand.Connection = eventDB;
                dbcommand.CommandType = CommandType.Text;

                dbcommand.CommandText = "INSERT INTO [Events] ([ID],stored_timestamp,Topic) VALUES(@ID,@stored_timestamp,@Topic); ";

                AddParameter(ref dbcommand, "@ID", DbType.String, myId);
                AddParameter(ref dbcommand, "@stored_timestamp", DbType.DateTime, timestamp);
                AddParameter(ref dbcommand, "@Topic", DbType.String, topic);

                dbcommand.ExecuteScalar();

                foreach (Components.Part current in parts)
                {
                    DbCommand dbcommand2 = eventDB.CreateCommand();
                    dbcommand2.Connection = eventDB;
                    dbcommand2.CommandType = CommandType.Text;
                    dbcommand2.CommandText = "INSERT INTO [KeyValuePairs] ([EventId], [Key], [Value]) VALUES(@EventID, @Key, @Value); ";
                    string sVal = current.value;
                    if (sVal == null) sVal = "";
                    string sKey = current.key;
                    if (sKey == null) sKey = "";
                    AddParameter(ref dbcommand2, "@EventID", DbType.String, myId);
                    AddParameter(ref dbcommand2, "@Key", DbType.String, sKey);
                    AddParameter(ref dbcommand2, "@Value", DbType.String, sVal);
                    dbcommand2.ExecuteScalar();
                }
                return guidId;
            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
            return Guid.Empty;
        }

        /// <summary>
        /// Method for adding an event to the event database optimised for the Opebn Source Database SqlLite. Maps the failed event notification to its subscribers
        /// </summary>
        /// <param name="failedEventNotification">Failed event notification info</param>
        public void WriteDatabaseSqlLiteCallbackAddress(Components.Subscription failedEventNotification)
        {
            try
            {
                string topic = failedEventNotification.Topic;
                string endpoint = failedEventNotification.Endpoint;
                string hid = failedEventNotification.HID;
                Nullable<DateTime> dateTime = failedEventNotification.DateTime;
                int priority = failedEventNotification.Priority;
                Components.Part[] parts = failedEventNotification.Parts;

                string myId = System.Guid.NewGuid().ToString();
                DbCommand dbcommand = eventDB.CreateCommand();
                dbcommand.Connection = eventDB;
                dbcommand.CommandType = CommandType.Text;

                dbcommand.CommandText = "INSERT INTO [Events] ([ID],stored_timestamp,Topic,Priority) VALUES(@ID,@stored_timestamp,@Topic,@Priority); ";

                AddParameter(ref dbcommand, "@ID", DbType.String, myId);
                AddParameter(ref dbcommand, "@stored_timestamp", DbType.DateTime, dateTime);
                AddParameter(ref dbcommand, "@Topic", DbType.String, topic);
                AddParameter(ref dbcommand, "@Priority", DbType.Int32, priority);

                dbcommand.ExecuteScalar();
                dbcommand.CommandText = "INSERT INTO [CallbackAddress] (Endpoint,HID,EventID) VALUES(@Endpoint,@HID,@EventID); ";

                AddParameter(ref dbcommand, "@Endpoint", DbType.String, endpoint);
                AddParameter(ref dbcommand, "@HID", DbType.String, hid);
                AddParameter(ref dbcommand, "@EventID", DbType.String, myId);

                dbcommand.ExecuteScalar();

                foreach (Components.Part current in parts)
                {
                    DbCommand dbcommand2 = eventDB.CreateCommand();
                    dbcommand2.Connection = eventDB;
                    dbcommand2.CommandType = CommandType.Text;
                    dbcommand2.CommandText = "INSERT INTO [KeyValuePairs] ([EventId],[Key],[Value]) VALUES(@EventID, @Key,@Value); ";
                    string sVal = current.value;
                    if (sVal == null) sVal = "";
                    string sKey = current.key;
                    if (sKey == null) sKey = "";
                    AddParameter(ref dbcommand2, "@EventID", DbType.String, myId);
                    AddParameter(ref dbcommand2, "@Key", DbType.String, sKey);
                    AddParameter(ref dbcommand2, "@Value", DbType.String, sVal);
                    dbcommand2.ExecuteScalar();
                }
            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        virtual public List<Components.LinkSmartEvent> ListEvents(DateTime start, DateTime end)
        {
            var result = new List<Components.LinkSmartEvent>();
            DbCommand cmd = eventDB.CreateCommand();

            cmd.Connection = eventDB;
            cmd.CommandType = CommandType.Text;

            cmd.CommandText = @"SELECT [ID],stored_timestamp,Topic, key, value FROM [Events] 
LEFT JOIN [KeyValuePairs] ON[KeyValuePairs].EventId=[Events].ID
                        WHERE stored_timestamp<@End and stored_timestamp>@Start 
order by [ID]; ";
            AddParameter(ref cmd, "@Start", DbType.String, start.ToString("yyyy-MM-dd HH:mm:ss"));
            AddParameter(ref cmd, "@End", DbType.String, end.ToString("yyyy-MM-dd HH:mm:ss"));

            DbDataReader dbr = cmd.ExecuteReader();
            Components.LinkSmartEvent e = new Components.LinkSmartEvent();
            while (dbr.Read()) // Read() returns true if there is still a result line to read
            {
                Guid id = Guid.Parse(dbr["ID"] as string);

                if (!e.InternalId.Equals(id))
                {
                    e = new Components.LinkSmartEvent();
                    e.InternalId = id;
                    e.Topic = dbr["Topic"] as string;
                    e.Timestamp = Convert.ToDateTime(dbr["stored_timestamp"] ?? DateTime.Now.ToString());
                    result.Add(e);
                }
                string key = dbr["Key"] as string;
                string value = dbr["Value"] as string;

                if (!(string.IsNullOrEmpty(key) && string.IsNullOrEmpty(value)))
                {
                    Components.Part p = new Components.Part();
                    p.key = key;
                    p.value = value;
                    e.Parts.Add(p);
                }
            }

            return result;
        }

        virtual public Components.LinkSmartEvent GetEventById(Guid theEventDbId)
        {
            var result = new List<Components.LinkSmartEvent>();
            using (DbCommand cmd = eventDB.CreateCommand())
            {
                cmd.CommandText = @"SELECT [ID],stored_timestamp,Topic FROM [Events] where ID='" + theEventDbId.ToString()+"'";
                //AddParameter(ref cmd, "@EventID", DbType.String, theEventDbId.ToString());
                DbDataReader dbr = cmd.ExecuteReader();
                while (dbr.Read()) // Read() returns true if there is still a result line to read
                {
                    Components.LinkSmartEvent e = new Components.LinkSmartEvent();
                    e.Topic = dbr["Topic"] as string;
                   e.InternalId = Guid.Parse(dbr["ID"] as string);
                   e.Timestamp = Convert.ToDateTime(dbr["stored_timestamp"]??DateTime.Now.ToString());
                    result.Add(e);

                    using (DbCommand pCmd = eventDB.CreateCommand())
                    {
                        pCmd.CommandText = @"SELECT [EventId],[Key],[Value] FROM [KeyValuePairs] where [EventId]='" + theEventDbId.ToString() + "'";
                        //AddParameter(ref pCmd, "@EventID", DbType.String,  e.InternalId.ToString());
                        DbDataReader pDbr = pCmd.ExecuteReader();
                        while (pDbr.Read()) // Read() returns true if there is still a result line to read
                        {
                            Components.Part p = new Components.Part();
                            p.key = pDbr["Key"] as string;
                            p.value = pDbr["Value"] as string;
                        
                            e.Parts.Add(p);
                        }
                    }
                }
            }
            return result.FirstOrDefault() ;
        }

        /// <summary>
        /// Adds a parameter to supplied dbcommand
        /// </summary>
        /// <param name="dbcommand">DbCommand object that the parameter will be added to</param>
        /// <param name="paramenterName">Name of the parameter to be added</param>
        /// <param name="dbType">The database datatype of the parameter</param>
        /// <param name="value">The actual value</param>




        internal void CloseConnection()
        {
            eventDB.Close();
        }
    }
}