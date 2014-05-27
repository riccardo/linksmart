using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.IO;
using EventManager;
using System.Threading;

namespace WindowsEventManager
{





    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            try
            {
                //We have to have it in the main function because the DLL gets loaded before we can add the HandleUnresolvedAssemblies in SubscriptionStore
                AssemblyResolver.HandleUnresovledAssemblies();
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.ThreadException += new ThreadExceptionEventHandler(ExceptionHandlingMethod);
                AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(ExceptionHandlingMethod);

                Application.Run(new EventManagerForm());
            }
            catch (Exception fatal)
            {
                Log.Fatal("Uncaught Exception in Event Manager Main", fatal);
            }
        }

        private static void ExceptionHandlingMethod(object sender, EventArgs t)
        {
            if (t.GetType().Equals(typeof(ThreadExceptionEventArgs))) {
                Log.Fatal("Unhandled Exception in Event Manager ", ((ThreadExceptionEventArgs)t).Exception);
            } else {
                Log.Fatal("Unhandled Exception in Event Manager  " + ((UnhandledExceptionEventArgs)t).ExceptionObject.ToString());
            }
            
        }
    }

    public class AssemblyResolver
    {
        public static void HandleUnresovledAssemblies()
        {
            AppDomain currentDomain = AppDomain.CurrentDomain;
            currentDomain.AssemblyResolve += currentDomain_AssemblyResolve;
        }

        private static System.Reflection.Assembly currentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
        {
            if (args.Name.StartsWith( "System.Data.SQLite"))
            {
                var path = Path.GetDirectoryName(System.Reflection.Assembly.GetEntryAssembly().Location);

                if (Environment.Is64BitProcess) // or for .NET4 use Environment.Is64BitProcess
                {
                    path = Path.Combine(path, "64");
                }
                else
                {
                    path = Path.Combine(path, "32");
                }

                path = Path.Combine(path, "System.Data.SQLite.dll");
                Log.Debug("Path:" + path);
                System.Reflection.Assembly assembly = System.Reflection.Assembly.LoadFrom(path);
                return assembly;
            }

            return null;
        }
    }
}
