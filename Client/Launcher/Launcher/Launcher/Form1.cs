using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Launcher
{
    public partial class Form1 : Form
    {
        private static account pAccount;

        public Form1()
        {
            InitializeComponent();
        }

        internal static account PAccount { get => pAccount; set => pAccount = value; }

        private void button1_Click(object sender, EventArgs e)
        {
            String username = textBox1.Text;
            String password = textBox2.Text;

            if (pAccount == null)
            {
                String url = String.Format(Program.APIHost + "login?sName={0}&sPassword={1}", username, password);
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                try
                {
                    WebResponse response = request.GetResponse();
                    using (Stream responseStream = response.GetResponseStream())
                    {
                        StreamReader reader = new StreamReader(responseStream, Encoding.UTF8);
                        String result = reader.ReadToEnd();
                        account user = JsonConvert.DeserializeObject<account>(result);
                        if (user.sToken == "null")
                        {
                            MessageBox.Show(user.sName + "\r\n" + url, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                        else
                        {
                            pAccount = user;
                            textBox3.Text = pAccount.sToken;
                        }
                    }
                }
                catch (WebException ex)
                {
                    MessageBox.Show("Unable to request account details!", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }

            string path = Directory.GetCurrentDirectory();
            if (!File.Exists(path + "\\MapleStory.exe"))
            {
                MessageBox.Show("Unable to locate MapleStory.exe.\r\n\r\nPlease move this client into your maplestory folder.");
            }
            else
            {
                ProcessStartInfo startInfo = new ProcessStartInfo(string.Concat(path, "\\", "MapleStory.exe"));
                startInfo.Arguments = "WEBSTART " + pAccount.sToken;
                startInfo.UseShellExecute = false;
                Process.Start(startInfo);
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (pAccount == null)
            {
                MessageBox.Show("Can't logout without logging in and obtaining a token first.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            String url = String.Format(Program.APIHost + "logout?sToken={0}", pAccount.sToken);

            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            try
            {
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, Encoding.UTF8);
                    String result = reader.ReadToEnd();
                    if (result == "true")
                    {
                        pAccount = null;
                        textBox3.Text = "";
                    }
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show("Unable to reach API service.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }
    }

    class account
    {
        public int nResponse;
        public string sName;
        public string sToken;
    }
}
