using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

using System.Windows.Forms;
using System.ComponentModel;

namespace WindowsEventManager
{

    public class BackgroundWorkerStreamWriter : TextWriter
    {
        BackgroundWorker _output = null;

        public BackgroundWorkerStreamWriter(BackgroundWorker output)
        {
            _output = output;
        }

        public override void Write(char value)
        {
            base.Write(value);
            _output.ReportProgress(0,value.ToString()); // When character data is written, report it as progress.
        }

        public override Encoding Encoding
        {
            get { return System.Text.Encoding.UTF8; }
        }

    }

}
