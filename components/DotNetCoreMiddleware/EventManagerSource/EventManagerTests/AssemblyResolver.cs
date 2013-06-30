
using NUnit.Framework;
using System.IO;
using System;
[SetUpFixture]
public class MySetUpClass
{
    [SetUp]
    public void RunBeforeAnyTests()
    {
        AssemblyResolver.HandleUnresovledAssemblies();
    }

    [TearDown]
    public void RunAfterAnyTests()
    {
        // ...
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
        if (args.Name.StartsWith("System.Data.SQLite"))
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
