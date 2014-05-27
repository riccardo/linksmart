using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

using System.Windows.Forms;

namespace WindowsEventManager
{

    public class TextBoxStreamWriter : TextWriter
    {
        TextBox _output = null;

        public TextBoxStreamWriter(TextBox output)
        {
            _output = output;
        }

        public override void Write(char value)
        {
            if (_output.InvokeRequired) {
            _output.BeginInvoke(new Action(() => Write(value)));
            } else {
            base.Write(value);
            _output.AppendText(value.ToString()); // When character data is written, append it to the text box.
            }
        }

        public override Encoding Encoding
        {
            get { return System.Text.Encoding.UTF8; }
        }

    }

}
