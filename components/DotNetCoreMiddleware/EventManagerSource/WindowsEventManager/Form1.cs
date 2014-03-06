using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using EventManager;
using System.Threading;
using System.Text.RegularExpressions;

namespace WindowsEventManager
{
    public partial class EventManagerForm : Form
    {
        public EventManagerForm()
        {
            InitializeComponent();
        }

        private void EventManagerForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            buttonStart.Enabled = false;
            buttonStop.Enabled = false;
            //System.Text.StringBuilder messageBoxCS = new System.Text.StringBuilder();
            //messageBoxCS.AppendFormat("{0} = {1}", "CloseReason", e.CloseReason);
            //messageBoxCS.AppendLine();
            //messageBoxCS.AppendFormat("{0} = {1}", "Cancel", e.Cancel);
            //messageBoxCS.AppendLine();
            //MessageBox.Show(messageBoxCS.ToString(), "FormClosing Event");
            textBoxEmOutput.AppendText("Closing application.\n");
            //eMbackgroundWorker.CancelAsync();
            EventManagerImplementation.Stop();
            buttonStart.Enabled = false;
            buttonStop.Enabled = false;
            Application.DoEvents();
            for (int i = 0; i < 15; i++)
            {
                textBoxEmOutput.AppendText(".");
                Thread.Sleep(100);
            }
           
        }

        private void EventManagerForm_Load(object sender, EventArgs e)
        {
            try
            {
                if (Properties.Settings.Default.Autostart)
                {
                    StartEventManager();
                }
            }
            catch(Exception ex)
            { Log.Debug("Error with autostart config setting."+ex.Message); }
        }

        private void buttonStart_Click(object sender, EventArgs e)
        {
            StartEventManager();
            //if (!eMbackgroundWorker.IsBusy)
            //{
            //    eMbackgroundWorker.RunWorkerAsync();
            //}
        }

        private void StartEventManager()
        {
            if (!EventManagerImplementation.IsRunning)
            {
                buttonStart.Enabled = false;
                buttonStop.Enabled = true;
                var writer = new TextBoxStreamWriter(textBoxEmOutput);
                // Redirect the out Console stream
                consoleOutcheckBox.Checked = true;
                Console.SetOut(writer);
                EventManagerImplementation.AddressChanged += new EventHandler<EventManagerImplementation.EmAddressEventArgs>(EventManagerImplementation_AddressChanged);
                EventManagerImplementation.HidChanged += new EventHandler<EventManagerImplementation.EmHidEventArgs>(EventManagerImplementation_HidChanged);
                EventManagerImplementation.Start();
                textBoxDescription.Text = EventManagerImplementation.Description;
                var stdOut = new StreamWriter(Console.OpenStandardOutput());
                stdOut.AutoFlush = true;
                Console.SetOut(stdOut);
                consoleOutcheckBox.Checked = false;
          
            }
        }

        void EventManagerImplementation_HidChanged(object sender, EventManagerImplementation.EmHidEventArgs e)
        {
            if (this.textBoxHID.InvokeRequired)
            {
                this.textBoxHID.BeginInvoke(new Action(() => EventManagerImplementation_HidChanged(sender,e)));
            }
            else
            {
                this.textBoxHID.Text = e.Hid;
            }
        }

        void EventManagerImplementation_AddressChanged(object sender, EventManagerImplementation.EmAddressEventArgs e)
        {
            if (this.textBoxAddress.InvokeRequired)
            {
                this.textBoxAddress.BeginInvoke(new Action(() => EventManagerImplementation_AddressChanged(sender, e)));
            }
            else
            {
                this.textBoxAddress.Text = e.WsEndpoint;
            }
        }

        private void eMbackgroundWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            var writer = new BackgroundWorkerStreamWriter(eMbackgroundWorker);
            // Redirect the out Console stream
            Console.SetOut(writer);
            EventManagerImplementation.Start();
            while (!eMbackgroundWorker.CancellationPending)
            {
                // do nothing, the BackgroundWorkerStreamWriter reports progress 
            }
            EventManagerImplementation.Stop();
        }

        private void buttonStop_Click(object sender, EventArgs e)
        {
            if (EventManagerImplementation.IsRunning)
            {
                buttonStart.Enabled = true;
                buttonStop.Enabled = false;
                EventManagerImplementation.Stop();
            }
            // eMbackgroundWorker.CancelAsync();
        }

        private void eMbackgroundWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            string output = (string)e.UserState;
            this.textBoxEmOutput.AppendText(output); // When character data is written, append it to the text box.
        }

        private void buttonClearDescriptions_Click(object sender, EventArgs e)
        {
            EventManagerImplementation.DeregisterOtherDescriptionsAtNetworkManager();
        }

        private void buttonClearSubscriptions_Click(object sender, EventArgs e)
        {
            EventManagerImplementation.RemovePersistentSubscriptions(DateTime.Today.AddDays(1));
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Regex regex = new Regex(textBoxRegEx.Text);
            string result = regex.Replace(this.textBoxEmOutput.Text, "");
            this.textBoxEmOutput.Text = result;
            
        }

        private void consoleOutcheckBox_CheckedChanged(object sender, EventArgs e)
        {
            if (consoleOutcheckBox.Checked)
            {
                Log.IsConsoleOutEnabled = true;
                var writer = new TextBoxStreamWriter(textBoxEmOutput);
                Console.SetOut(writer);
                Console.Out.WriteLine("Directing log to output window. Disable this during normal operation.");
            }
            else
            {
                Console.Out.WriteLine("Disabling output.");
                Log.IsConsoleOutEnabled = false;
                var stdOut = new StreamWriter(Console.OpenStandardOutput());
                stdOut.AutoFlush = true;
                Console.SetOut(stdOut);
            }
        }

      
    }
}
