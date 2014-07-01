// -----------------------------------------------------------------------
// <copyright file="Log.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace EventManager
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using log4net;

    /// <summary>
    /// A log helper for Event Manager. Handles both log4net logging and Console output.
    /// </summary>
    public class Log
    {
        internal static readonly ILog Log4Net = LogManager.GetLogger("EventManager");

        public static bool IsConsoleOutEnabled = true;
        public static void Debug(string message) {
            Log4Net.Debug(message);
            if (IsConsoleOutEnabled)
            {
                Console.Out.WriteLine(message);
            }
        }
        public static void Error(string message)
        {
             Log4Net.Error(message);
             if (IsConsoleOutEnabled)
             {
                 Console.Out.WriteLine(message);
             }
        }
         public static void Fatal(string message)
         {
             Log4Net.Fatal(message);
        }

         public static void Fatal(string message, Exception exception)
         {
             Log4Net.Fatal(message, exception);
         }
    }
}
