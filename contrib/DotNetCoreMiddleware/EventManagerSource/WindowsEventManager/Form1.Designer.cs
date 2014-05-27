namespace WindowsEventManager
{
    partial class EventManagerForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(EventManagerForm));
            this.eMbackgroundWorker = new System.ComponentModel.BackgroundWorker();
            this.buttonStart = new System.Windows.Forms.Button();
            this.buttonStop = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.textBoxEmOutput = new System.Windows.Forms.TextBox();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.textBoxAddress = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.textBoxHID = new System.Windows.Forms.TextBox();
            this.textBoxDescription = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.buttonClearDescriptions = new System.Windows.Forms.Button();
            this.buttonClearSubscriptions = new System.Windows.Forms.Button();
            this.buttonCleanEvents = new System.Windows.Forms.Button();
            this.textBoxRegEx = new System.Windows.Forms.TextBox();
            this.consoleOutcheckBox = new System.Windows.Forms.CheckBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // eMbackgroundWorker
            // 
            this.eMbackgroundWorker.WorkerReportsProgress = true;
            this.eMbackgroundWorker.WorkerSupportsCancellation = true;
            this.eMbackgroundWorker.DoWork += new System.ComponentModel.DoWorkEventHandler(this.eMbackgroundWorker_DoWork);
            this.eMbackgroundWorker.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.eMbackgroundWorker_ProgressChanged);
            // 
            // buttonStart
            // 
            this.buttonStart.Location = new System.Drawing.Point(559, 13);
            this.buttonStart.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
            this.buttonStart.Name = "buttonStart";
            this.buttonStart.Size = new System.Drawing.Size(201, 22);
            this.buttonStart.TabIndex = 0;
            this.buttonStart.Text = "Start";
            this.buttonStart.UseVisualStyleBackColor = true;
            this.buttonStart.Click += new System.EventHandler(this.buttonStart_Click);
            // 
            // buttonStop
            // 
            this.buttonStop.Location = new System.Drawing.Point(559, 41);
            this.buttonStop.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
            this.buttonStop.Name = "buttonStop";
            this.buttonStop.Size = new System.Drawing.Size(201, 22);
            this.buttonStop.TabIndex = 1;
            this.buttonStop.Text = "Stop";
            this.buttonStop.UseVisualStyleBackColor = true;
            this.buttonStop.Click += new System.EventHandler(this.buttonStop_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(565, 44);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(0, 13);
            this.label1.TabIndex = 2;
            // 
            // textBoxEmOutput
            // 
            this.textBoxEmOutput.BackColor = System.Drawing.Color.Black;
            this.textBoxEmOutput.Font = new System.Drawing.Font("Lucida Console", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.textBoxEmOutput.ForeColor = System.Drawing.Color.White;
            this.textBoxEmOutput.Location = new System.Drawing.Point(13, 10);
            this.textBoxEmOutput.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
            this.textBoxEmOutput.MaxLength = 65535;
            this.textBoxEmOutput.Multiline = true;
            this.textBoxEmOutput.Name = "textBoxEmOutput";
            this.textBoxEmOutput.ReadOnly = true;
            this.textBoxEmOutput.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.textBoxEmOutput.Size = new System.Drawing.Size(540, 360);
            this.textBoxEmOutput.TabIndex = 4;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.InitialImage = ((System.Drawing.Image)(resources.GetObject("pictureBox1.InitialImage")));
            this.pictureBox1.Location = new System.Drawing.Point(721, 374);
            this.pictureBox1.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(51, 50);
            this.pictureBox1.TabIndex = 5;
            this.pictureBox1.TabStop = false;
            // 
            // textBoxAddress
            // 
            this.textBoxAddress.Location = new System.Drawing.Point(559, 199);
            this.textBoxAddress.Multiline = true;
            this.textBoxAddress.Name = "textBoxAddress";
            this.textBoxAddress.ReadOnly = true;
            this.textBoxAddress.Size = new System.Drawing.Size(195, 82);
            this.textBoxAddress.TabIndex = 6;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(556, 183);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(45, 13);
            this.label2.TabIndex = 7;
            this.label2.Text = "Address";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(556, 70);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(60, 13);
            this.label3.TabIndex = 8;
            this.label3.Text = "Description";
            // 
            // textBoxHID
            // 
            this.textBoxHID.Location = new System.Drawing.Point(559, 160);
            this.textBoxHID.Name = "textBoxHID";
            this.textBoxHID.ReadOnly = true;
            this.textBoxHID.Size = new System.Drawing.Size(195, 20);
            this.textBoxHID.TabIndex = 9;
            // 
            // textBoxDescription
            // 
            this.textBoxDescription.Location = new System.Drawing.Point(559, 86);
            this.textBoxDescription.Multiline = true;
            this.textBoxDescription.Name = "textBoxDescription";
            this.textBoxDescription.ReadOnly = true;
            this.textBoxDescription.Size = new System.Drawing.Size(195, 51);
            this.textBoxDescription.TabIndex = 10;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(556, 144);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(26, 13);
            this.label4.TabIndex = 11;
            this.label4.Text = "HID";
            // 
            // buttonClearDescriptions
            // 
            this.buttonClearDescriptions.Location = new System.Drawing.Point(559, 288);
            this.buttonClearDescriptions.Name = "buttonClearDescriptions";
            this.buttonClearDescriptions.Size = new System.Drawing.Size(195, 39);
            this.buttonClearDescriptions.TabIndex = 12;
            this.buttonClearDescriptions.Text = "Remove other HIDs with this description from Network Manager";
            this.buttonClearDescriptions.UseVisualStyleBackColor = true;
            this.buttonClearDescriptions.Click += new System.EventHandler(this.buttonClearDescriptions_Click);
            // 
            // buttonClearSubscriptions
            // 
            this.buttonClearSubscriptions.AccessibleDescription = "";
            this.buttonClearSubscriptions.Location = new System.Drawing.Point(559, 333);
            this.buttonClearSubscriptions.Name = "buttonClearSubscriptions";
            this.buttonClearSubscriptions.Size = new System.Drawing.Size(195, 37);
            this.buttonClearSubscriptions.TabIndex = 14;
            this.buttonClearSubscriptions.Text = "Clear All Persistent Subscriptions";
            this.buttonClearSubscriptions.UseVisualStyleBackColor = true;
            this.buttonClearSubscriptions.Click += new System.EventHandler(this.buttonClearSubscriptions_Click);
            // 
            // buttonCleanEvents
            // 
            this.buttonCleanEvents.Location = new System.Drawing.Point(12, 400);
            this.buttonCleanEvents.Name = "buttonCleanEvents";
            this.buttonCleanEvents.Size = new System.Drawing.Size(173, 24);
            this.buttonCleanEvents.TabIndex = 16;
            this.buttonCleanEvents.Text = "Remove regex from console text";
            this.buttonCleanEvents.UseVisualStyleBackColor = true;
            this.buttonCleanEvents.Click += new System.EventHandler(this.button1_Click);
            // 
            // textBoxRegEx
            // 
            this.textBoxRegEx.Location = new System.Drawing.Point(13, 374);
            this.textBoxRegEx.Name = "textBoxRegEx";
            this.textBoxRegEx.Size = new System.Drawing.Size(172, 20);
            this.textBoxRegEx.TabIndex = 17;
            this.textBoxRegEx.Text = "###.*###[\\r|\\n]";
            // 
            // consoleOutcheckBox
            // 
            this.consoleOutcheckBox.AutoSize = true;
            this.consoleOutcheckBox.Location = new System.Drawing.Point(205, 374);
            this.consoleOutcheckBox.Name = "consoleOutcheckBox";
            this.consoleOutcheckBox.Size = new System.Drawing.Size(135, 17);
            this.consoleOutcheckBox.TabIndex = 18;
            this.consoleOutcheckBox.Tag = "Keep this disabled during operation";
            this.consoleOutcheckBox.Text = "Enable Console Output";
            this.consoleOutcheckBox.UseVisualStyleBackColor = true;
            this.consoleOutcheckBox.CheckedChanged += new System.EventHandler(this.consoleOutcheckBox_CheckedChanged);
            // 
            // EventManagerForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(775, 434);
            this.Controls.Add(this.consoleOutcheckBox);
            this.Controls.Add(this.textBoxRegEx);
            this.Controls.Add(this.buttonCleanEvents);
            this.Controls.Add(this.buttonClearSubscriptions);
            this.Controls.Add(this.buttonClearDescriptions);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.textBoxDescription);
            this.Controls.Add(this.textBoxHID);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.textBoxAddress);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.textBoxEmOutput);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.buttonStop);
            this.Controls.Add(this.buttonStart);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
            this.Name = "EventManagerForm";
            this.Text = "EventManager";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.EventManagerForm_FormClosing);
            this.Load += new System.EventHandler(this.EventManagerForm_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.ComponentModel.BackgroundWorker eMbackgroundWorker;
        private System.Windows.Forms.Button buttonStart;
        private System.Windows.Forms.Button buttonStop;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox textBoxEmOutput;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.TextBox textBoxAddress;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox textBoxHID;
        private System.Windows.Forms.TextBox textBoxDescription;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Button buttonClearDescriptions;
        private System.Windows.Forms.Button buttonClearSubscriptions;
        private System.Windows.Forms.Button buttonCleanEvents;
        private System.Windows.Forms.TextBox textBoxRegEx;
        private System.Windows.Forms.CheckBox consoleOutcheckBox;
    }
}

