/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
﻿using System;
using System.IO;
using System.Data;
using System.Text;
using System.Drawing;
using System.Diagnostics;
using System.Windows.Forms;
using System.ComponentModel;
using System.Collections.Generic;
using Microsoft.Win32;

namespace OpenSource.UPnP
{
    public partial class AutoUpdate : Form
    {
        public static int Version = 30;
        public static string VersionString = GetVersionStr(Version);
        public static Guid ProductCode = new Guid("{D7A4D78E-2554-4EFD-9310-26C7CB4135C6}");
        private static string ShortProductName = "Tools for UPnP technology";
        private static string RegistryKey = "UPnPUpdateCheck";
        private static string TempFolder = "devtools";
        public static void ShowMainSite() { try { System.Diagnostics.Process.Start("http://opentools.homeip.net/dev-tools-for-upnp"); } catch (System.ComponentModel.Win32Exception) { } }
        private static HttpRequestor requestor = null;
        private static AutoUpdate updateform = null;
        private static Form parentform = null;
        private static string GetVersionStr(int ver) { return string.Format("{0}.{1}.{2}", (ver >> 16) & 0xFF, (ver >> 8) & 0xFF, ver & 0xFF); }

        public AutoUpdate(int newversion)
        {
            InitializeComponent();
            currentVersionLabel.Text = "v" + VersionString;
            newVersionLabel.Text = "v" + GetVersionStr(newversion);
        }

        private void MeshToolsUpdate_Load(object sender, EventArgs e)
        {
            updateCheckBox.Checked = GetAutoUpdateCheck();
        }

        public static void UpdateCheck(Form parent)
        {
            if (requestor != null) return;
            if (File.Exists(Application.StartupPath + "\\AutoUpdateTool.exe") == false) return;

            parentform = parent;
            requestor = new HttpRequestor();
            requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
            requestor.LaunchProxyRequest("http://sites.google.com/site/opensoftwareprojects/mesh/security-1/autoupdate", null, 1);
        }

        private static RegistryKey GetRegKey()
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey("Software\\Open Source", true);
            if (key == null)
            {
                RegistryKey skey = Registry.CurrentUser.OpenSubKey("Software", true);
                if (skey == null) return null;
                key = skey.CreateSubKey("Open Source");
            }
            return key;
        }

        public static void SetAutoUpdateCheck(bool autoupdate)
        {
            RegistryKey key = GetRegKey();
            if (key == null) return;
            if (autoupdate)
            {
                key.SetValue(RegistryKey, DateTime.MinValue.ToString());
            }
            else
            {
                key.SetValue(RegistryKey, DateTime.MaxValue.ToString());
            }
            key.Close();
        }

        public static void SetAutoUpdateCheckNow(bool autoupdate)
        {
            RegistryKey key = GetRegKey();
            if (key == null) return;
            if (autoupdate)
            {
                key.SetValue(RegistryKey, DateTime.Now.ToString());
            }
            else
            {
                key.SetValue(RegistryKey, DateTime.MaxValue.ToString());
            }
            key.Close();
        }

        public static bool GetAutoUpdateCheck()
        {
            RegistryKey key = GetRegKey();
            if (key == null) return false;
            string updatecheck = (string)key.GetValue(RegistryKey, null);
            DateTime lastcheck = DateTime.MinValue;
            if (updatecheck != null) lastcheck = DateTime.Parse(updatecheck);
            key.Close();
            return (lastcheck.Year < 9000);
        }

        public static void AutoUpdateCheck(Form parent)
        {
            // Delete the temp folder if present
            String tpath = Path.GetTempPath();
            try
            {
                if (Directory.Exists(tpath + TempFolder)) Directory.Delete(tpath + TempFolder, true);
            }
            catch (Exception) { } // In some cases, we try to delete too quickly and this will fail. It's ok, not critical.

            // See if we need to perform a version check
            RegistryKey key = GetRegKey();
            if (key == null) return;
            string updatecheck = (string)key.GetValue(RegistryKey, null);
            DateTime lastcheck = DateTime.MinValue;
            if (updatecheck != null) lastcheck = DateTime.Parse(updatecheck);
            if (lastcheck.Year < 9000)
            {
                if (lastcheck.AddDays(1).CompareTo(DateTime.Now) < 0)
                {
                    key.SetValue(RegistryKey, DateTime.Now.ToString());
                    UpdateCheck(parent);
                }
            }
            key.Close();
        }

        private static void ShowUpdateForm()
        {
            updateform.ShowDialog(parentform);
        }

        private static void requestor_OnRequestCompleted(HttpRequestor sender, bool success, object tag, string url, byte[] data)
        {
            if (success == true)
            {
                // Fetch version information
                int newversion = 0;
                string updatelink = null;
                try
                {
                    string page = UTF8Encoding.UTF8.GetString(data);
                    string x = "##" + ProductCode.ToString() + "##";
                    x = x.ToUpper();
                    int i = page.IndexOf(x);
                    if (i == -1)
                    {
                        requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
                        requestor = null;
                        return;
                    }
                    page = page.Substring(i + x.Length);
                    i = page.IndexOf("##");
                    if (i == -1)
                    {
                        requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
                        requestor = null;
                        return;
                    }
                    string versionstr = page.Substring(0, i);
                    newversion = int.Parse(versionstr);
                    page = page.Substring(i + 2);
                    i = page.IndexOf("##");
                    if (i == -1)
                    {
                        requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
                        requestor = null;
                        return;
                    }
                    updatelink = page.Substring(0, i);
                }
                catch (Exception)
                {
                    requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
                    requestor = null;
                    return;
                }

                // Compare to existing version
                if (Version < newversion)
                {
                    updateform = new AutoUpdate(newversion);
                    parentform.Invoke(new System.Threading.ThreadStart(ShowUpdateForm));
                }
            }
            requestor.OnRequestCompleted += new HttpRequestor.RequestCompletedHandler(requestor_OnRequestCompleted);
            requestor = null;
        }

        private void cancelButton_Click(object sender, EventArgs e)
        {
            // Cancel
            Close();
        }

        private void updateButton_Click(object sender, EventArgs e)
        {
            String tpath = Path.GetTempPath();
            Directory.CreateDirectory(tpath + TempFolder);
            tpath += (TempFolder + "\\");
            File.Copy(Application.StartupPath + "\\AutoUpdateTool.exe", tpath + "AutoUpdateTool.exe", true);
            File.Copy(Application.StartupPath + "\\Interop.WindowsInstaller.dll", tpath + "Interop.WindowsInstaller.dll", true);

            // Update
            try
            {
                string arg = string.Format("-g:{0} -t:\"{1}\" -r:\"{2}\"", ProductCode.ToString(), ShortProductName, Application.ExecutablePath);
                ProcessStartInfo startInfo = new ProcessStartInfo(tpath + "AutoUpdateTool.exe", arg);
                Process process = Process.Start(startInfo);
            }
            catch (Exception)
            {
                return;
            }
            Application.Exit();
        }

        private void updateCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            SetAutoUpdateCheckNow(updateCheckBox.Checked);
        }
    }
}
