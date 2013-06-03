using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.IO;
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
            //We have to have it in the main function because the DLL gets loaded before we can add the HandleUnresolvedAssemblies in SubscriptionStore
            AssemblyResolver.HandleUnresovledAssemblies();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new EventManagerForm());
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
                System.Console.WriteLine("Path:" + path);
                System.Reflection.Assembly assembly = System.Reflection.Assembly.LoadFrom(path);
                return assembly;
            }

            return null;
        }
    }
}
