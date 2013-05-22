// -----------------------------------------------------------------------
// <copyright file="Program_Unmanaged.cs" company="">
// TODO: Update copyright text.
// </copyright>
// -----------------------------------------------------------------------

namespace EventManager
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Runtime.InteropServices;
    using System.Reflection;
    using System.Runtime.CompilerServices;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    partial class ProgramHelper
    {
        public void Start()
        {
            HandlerRoutine myRoutine = new HandlerRoutine(ConsoleCtrlCheck);

            bool success = SetConsoleCtrlHandler(myRoutine, true);
        }
        public bool ConsoleCtrlCheck(CtrlTypes ctrlType)
        {

            // Put your own handler here

            switch (ctrlType)
            {

                case CtrlTypes.CTRL_C_EVENT:



                    Console.WriteLine("CTRL+C received!");

                    ShutDown();

                    break;



                case CtrlTypes.CTRL_BREAK_EVENT:



                    Console.WriteLine("CTRL+BREAK received!");

                    ShutDown();

                    break;



                case CtrlTypes.CTRL_CLOSE_EVENT:

                    Console.WriteLine("Program being closed!");

                    ShutDown();

                    break;

                case CtrlTypes.CTRL_LOGOFF_EVENT:

                case CtrlTypes.CTRL_SHUTDOWN_EVENT:



                    Console.WriteLine("User is logging off!");

                    ShutDown();

                    break;



            }

            return true;

        }

        private void ShutDown()
        {
            if (Properties.Settings.UseNetworkManager)
            {
                EventManagerImplementation.DeregisterAtNetworkManager();
                Console.WriteLine("Exit cleanup.");
                Console.ReadLine();
            }
        }

           #region unmanaged

        // Declare the SetConsoleCtrlHandler function

        // as external and receiving a delegate. 


       [DllImport("Kernel32")]
        public static extern bool SetConsoleCtrlHandler(HandlerRoutine Handler, bool Add);


        // A delegate type to be used as the handler routine 

        // for SetConsoleCtrlHandler.

       public delegate bool HandlerRoutine(CtrlTypes CtrlType);


        // An enumerated type for the control messages

        // sent to the handler routine.

        public enum CtrlTypes
        {

            CTRL_C_EVENT = 0,

            CTRL_BREAK_EVENT,

            CTRL_CLOSE_EVENT,

            CTRL_LOGOFF_EVENT = 5,

            CTRL_SHUTDOWN_EVENT

        }



        #endregion
    }
}
