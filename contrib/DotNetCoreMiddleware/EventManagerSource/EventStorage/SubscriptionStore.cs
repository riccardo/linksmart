// -----------------------------------------------------------------------
// <copyright file="SubscriptionStore.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace EventStorage
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Data.SQLite;
    using System.Data;
    using System.Data.Common;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public class SubscriptionStore
    {
        DbConnection eventDB = null;
        public static SubscriptionStore Store = new SubscriptionStore();

        private SubscriptionStore()
        {
            SQLiteConnection cnn = new SQLiteConnection();
            eventDB = cnn;
            eventDB.ConnectionString = "Data Source=Subscriptions.db3; Pooling=true; Journal Mode=Off";

            eventDB.Open();
            CreateSubscriptionTableIfNotExists();
        }


        protected void AddParameter(ref DbCommand dbcommand, string paramenterName, DbType dbType, object value)
        {
            DbParameter keyParam = dbcommand.CreateParameter();
            keyParam.ParameterName = paramenterName;
            keyParam.DbType = dbType;
            keyParam.Value = value;
            dbcommand.Parameters.Add(keyParam);
        }

        virtual public void CreateSubscriptionTableIfNotExists()
        {
            using (DbCommand cmd = eventDB.CreateCommand())
            {
                cmd.CommandText =
                 @"SELECT count(name) FROM sqlite_master WHERE type='table' AND tbl_name='Subscription';";
                int noTables = Convert.ToInt32(cmd.ExecuteScalar());
                if (0 == noTables)
                {
                    cmd.CommandText = @" CREATE TABLE [Subscription] (
                Topic nvarchar(200)
                , HID  nvarchar(200)
                , Endpoint  nvarchar(200)
                , Description  nvarchar(200)
                , Priority  integer
                , Timestamp nvarchar(200)
                , id integer primary key  autoincrement);";
                    cmd.ExecuteNonQuery();
                }

                cmd.CommandText =
                 @"SELECT count(name) FROM sqlite_master WHERE type='table' AND tbl_name='KeyValuePairs';";
                noTables = Convert.ToInt32(cmd.ExecuteScalar());
                if (0 == noTables)
                {
                    cmd.CommandText = @" CREATE TABLE [KeyValuePairs]  (
                 [SubScriptionId]  integer
                ,[Key] nvarchar(4000)
                , [Value] nvarchar(4000)                
                , IntId integer primary key  autoincrement);";
                    cmd.ExecuteNonQuery();
                }
            }
        }

        virtual public List<Components.Subscription> ListSubscriptions()
        {
            List<Components.Subscription> result = new List<Components.Subscription>();
            using (DbCommand cmd = eventDB.CreateCommand())
            {
                cmd.CommandText = @"SELECT [id], [Topic], [HID], [Endpoint], [Description], [Priority],[Timestamp]  FROM [Subscription] ";
                DbDataReader dbr = cmd.ExecuteReader();
                while (dbr.Read()) // Read() returns true if there is still a result line to read
                {
                    Components.Subscription s = new Components.Subscription();
                    s.Topic = dbr["Topic"] as string;
                    s.HID = dbr["HID"] as string;
                    s.Endpoint = dbr["Endpoint"] as string;
                    s.Description = dbr["Description"] as string;
                    s.Priority = Convert.ToInt32(dbr["Priority"]);
                    int id = Convert.ToInt32(dbr["id"]);
                    result.Add(s);
                    var contentSubscription = new List<Components.Part>();
                    using (DbCommand pCmd = eventDB.CreateCommand())
                    {
                        pCmd.CommandText = @"SELECT [SubscriptionId],[Key],[Value] FROM [KeyValuePairs] where [SubscriptionId]=" + id.ToString();
                        //AddParameter(ref pCmd, "@SubscriptionId", DbType.String,  e.InternalId.ToString());
                        DbDataReader pDbr = pCmd.ExecuteReader();
                        while (pDbr.Read()) // Read() returns true if there is still a result line to read
                        {
                            Components.Part p = new Components.Part();
                            p.key = pDbr["Key"] as string;
                            p.value = pDbr["Value"] as string;

                            contentSubscription.Add(p);
                        }
                        s.Parts = contentSubscription.ToArray();
                    }
                }
            }
            return result;
        }

        virtual public int GetSubscriptionId(Components.Subscription subscription)
        {

            int result = -1;

            DbCommand cmd = eventDB.CreateCommand();

            cmd.CommandText =
            @"SELECT id FROM [Subscription] WHERE Topic=@Topic AND HID=@HID AND Endpoint=@Endpoint AND Description=@Description LIMIT 1;";

            AddParameter(ref cmd, "@Topic", DbType.String, subscription.Topic ?? string.Empty);
            AddParameter(ref cmd, "@HID", DbType.String, subscription.HID ?? string.Empty);
            AddParameter(ref cmd, "@Endpoint", DbType.String, subscription.Endpoint ?? string.Empty);
            AddParameter(ref cmd, "@Description", DbType.String, subscription.Description ?? string.Empty);

            result = Convert.ToInt32(cmd.ExecuteScalar() ?? -1);

            return result;
        }

        public void SaveSubscription(Components.Subscription subscription)
        {
            try
            {
                int id = GetSubscriptionId(subscription);
                if (-1 == id)
                {
                    InsertSubscription(subscription);
                    id = GetSubscriptionId(subscription);
                    UpdateSubscriptionAttributes(id, subscription);
                    System.Console.WriteLine("Subscription with event matching expression (topic) [" + subscription.Topic + "] has been added.");
                }
                else
                {
                    UpdateSubscription(id, subscription);
                    UpdateSubscriptionAttributes(id, subscription);
                    System.Console.WriteLine("Subscription with event matching expression (topic) [" + subscription.Topic + "] has been updated.");
                }

            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        private void UpdateSubscriptionAttributes(int id, Components.Subscription subscription)
        {
            DbCommand dbcommand2 = eventDB.CreateCommand();
            dbcommand2.Connection = eventDB;
            dbcommand2.CommandType = CommandType.Text;
            dbcommand2.CommandText = "DELETE FROM [KeyValuePairs] WHERE [SubscriptionId]=" + id.ToString() + ";";
            foreach (Components.Part current in subscription.Parts ?? new Components.Part[0])
            {
                dbcommand2.CommandText += "INSERT INTO [KeyValuePairs] ([SubscriptionId], [Key], [Value]) VALUES(@SubscriptionId, @Key, @Value); ";
                string sVal = current.value;
                if (sVal == null) sVal = "";
                string sKey = current.key;
                if (sKey == null) sKey = "";
                AddParameter(ref dbcommand2, "@SubscriptionId", DbType.Int32, id);
                AddParameter(ref dbcommand2, "@Key", DbType.String, sKey);
                AddParameter(ref dbcommand2, "@Value", DbType.String, sVal);
            }
            dbcommand2.ExecuteScalar();
        }

        private void UpdateSubscription(int id, Components.Subscription subscription)
        {
            string topic = subscription.Topic;
            string hid = subscription.HID;
            string endpoint = subscription.Endpoint;

            string description = subscription.Description;
            int priority = subscription.Priority;

            DbCommand dbcommand = eventDB.CreateCommand();
            dbcommand.Connection = eventDB;
            dbcommand.CommandType = CommandType.Text;

            dbcommand.CommandText = "UPDATE [Subscription] SET Topic=@Topic,HID=@HID,Endpoint=@Endpoint,Description=@Description,Priority=@Priority,Timestamp=@Timestamp WHERE id=@Id; ";

            AddParameter(ref dbcommand, "@Topic", DbType.String, topic ?? string.Empty);
            AddParameter(ref dbcommand, "@HID", DbType.String, hid ?? string.Empty);
            AddParameter(ref dbcommand, "@Endpoint", DbType.String, endpoint ?? string.Empty);
            AddParameter(ref dbcommand, "@Description", DbType.String, description ?? string.Empty);
            AddParameter(ref dbcommand, "@Priority", DbType.Int32, priority);
            AddParameter(ref dbcommand, "@Timestamp", DbType.String, DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"));
            AddParameter(ref dbcommand, "@Id", DbType.Int32, id);

            dbcommand.ExecuteNonQuery();
        }

        public void RemoveSubscription(Components.Subscription subscription)
        {
            try
            {
                int id = GetSubscriptionId(subscription);
                if (-1 != id)
                {
                    DeleteSubscription(id);
                    System.Console.WriteLine("A subscription with event matching expression (topic) [" + subscription.Topic + "] has been removed.");
                }

            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        public void RemoveSubscriptions(DateTime olderThanThisDate)
        {
            try
            {
                DeleteSubscriptions(olderThanThisDate);
                System.Console.WriteLine("All subscriptions older than " + olderThanThisDate.ToString("yyyy-MM-dd HH:mm:ss") + " have been removed.");
            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        public void RemoveSubscriptions(Components.Subscription subscription)
        {
            try
            {
                DbCommand dbcommand = eventDB.CreateCommand();
                dbcommand.Connection = eventDB;
                dbcommand.CommandType = CommandType.Text;
                string address = string.Empty;
                if (!string.IsNullOrEmpty(subscription.Endpoint))
                {
                    dbcommand.CommandText = "DELETE FROM [Subscription] WHERE Endpoint=@Address; ";
                    address = subscription.Endpoint;
                }
                else if (!string.IsNullOrEmpty(subscription.HID))
                {
                    dbcommand.CommandText = "DELETE FROM [Subscription] WHERE HID=@Address; ";
                    address = subscription.HID;
                }
                else
                {
                    dbcommand.CommandText = "DELETE FROM [Subscription] WHERE Description=@Address; ";
                    address = subscription.Description;
                }


                AddParameter(ref dbcommand, "@Address", DbType.String, address);

                dbcommand.ExecuteNonQuery();
                System.Console.WriteLine("All subscriptions older mathing subscriber address {0} have been removed.", address);
            }
            catch (Exception e) { System.Console.WriteLine(e.Message); }
        }

        private void DeleteSubscription(int id)
        {
            DbCommand dbcommand = eventDB.CreateCommand();
            dbcommand.Connection = eventDB;
            dbcommand.CommandType = CommandType.Text;

            dbcommand.CommandText = "DELETE FROM [Subscription] WHERE id=@Id; ";

            AddParameter(ref dbcommand, "@Id", DbType.Int32, id);

            dbcommand.ExecuteNonQuery();
        }

        private void DeleteSubscriptions(DateTime olderThanThisDate)
        {

            DbCommand dbcommand = eventDB.CreateCommand();

            dbcommand.Connection = eventDB;
            dbcommand.CommandType = CommandType.Text;

            dbcommand.CommandText = "DELETE FROM [Subscription] WHERE Timestamp<@Timestamp; ";

            AddParameter(ref dbcommand, "@Timestamp", DbType.String, olderThanThisDate.ToString("yyyy-MM-dd HH:mm:ss"));

            dbcommand.ExecuteNonQuery();

        }


        private void InsertSubscription(Components.Subscription subscription)
        {
            string topic = subscription.Topic;
            string hid = subscription.HID;
            string endpoint = subscription.Endpoint;

            string description = subscription.Description;
            int priority = subscription.Priority;

            DbCommand dbcommand = eventDB.CreateCommand();

            dbcommand.Connection = eventDB;
            dbcommand.CommandType = CommandType.Text;

            dbcommand.CommandText = "INSERT INTO [Subscription] (Topic,HID,Endpoint,Description,Priority,Timestamp) VALUES(@Topic,@HID,@Endpoint,@Description,@Priority,@Timestamp); ";

            AddParameter(ref dbcommand, "@Topic", DbType.String, topic ?? string.Empty);
            AddParameter(ref dbcommand, "@HID", DbType.String, hid ?? string.Empty);
            AddParameter(ref dbcommand, "@Endpoint", DbType.String, endpoint ?? string.Empty);
            AddParameter(ref dbcommand, "@Description", DbType.String, description ?? string.Empty);
            AddParameter(ref dbcommand, "@Priority", DbType.Int32, priority);
            AddParameter(ref dbcommand, "@Timestamp", DbType.String, DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"));

            dbcommand.ExecuteNonQuery();

        }


        public void AddFailedNotification(Components.Subscription subscription, Components.LinkSmartEvent failedEvent)
        {
            // Maybe this should be in the same database as the events. TBD.
            SaveSubscription(subscription);
            int subId = GetSubscriptionId(subscription);
            CreateFailedEventSubscriptionTableIfNotExists();

            DbCommand cmd = eventDB.CreateCommand();

            cmd.Connection = eventDB;
            cmd.CommandType = CommandType.Text;
            cmd.CommandText =
             @"SELECT count(IntId) FROM FailedEventSubscription WHERE [SubscriptionId]=@SubscriptionId AND [EventId]=@EventId;";
            AddParameter(ref cmd, "@SubscriptionId", DbType.Int32, subId);
            AddParameter(ref cmd, "@EventId", DbType.String, failedEvent.InternalId.ToString());
            int noRows = Convert.ToInt32(cmd.ExecuteScalar());
            if (0 == noRows)
            {
                cmd.CommandText = @" INSERT INTO [FailedEventSubscription] ([EventId], [SubscriptionId]) VALUES(@EventId,@SubscriptionId);";
                AddParameter(ref cmd, "@SubscriptionId", DbType.Int32, subId);
                AddParameter(ref cmd, "@EventId", DbType.String, failedEvent.InternalId.ToString());
                cmd.ExecuteNonQuery();
            }
        }

        virtual public void CreateFailedEventSubscriptionTableIfNotExists()
        {
            DbCommand cmd = eventDB.CreateCommand();

            cmd.Connection = eventDB;
            cmd.CommandType = CommandType.Text;
            cmd.CommandText =
             @"SELECT count(name) FROM sqlite_master WHERE type='table' AND tbl_name='FailedEventSubscription';";
            int noTables = Convert.ToInt32(cmd.ExecuteScalar());
            if (0 == noTables)
            {
                cmd.CommandText = @" CREATE TABLE [FailedEventSubscription] (
                  [EventId]  nvarchar(200)
                , [SubscriptionId] integer
                , [IntId] integer primary key  autoincrement);";
                cmd.ExecuteNonQuery();
            }

        }
    }
}
