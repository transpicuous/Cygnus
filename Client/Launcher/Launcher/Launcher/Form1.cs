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
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            String username = textBox1.Text;
            String password = textBox2.Text;
            String url = String.Format(Program.APIHost + "name={0}&password={1}", username, password);

            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            try
            {
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, Encoding.UTF8);
                    String result = reader.ReadToEnd();
                    account user = JsonConvert.DeserializeObject<account>(result);
                    if (user.token == "null")
                    {
                        MessageBox.Show(user.name + "\r\n" + url, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                    else
                    {
                        string path = Directory.GetCurrentDirectory();
                        if (!File.Exists(path + "\\MapleStory.exe"))
                        {
                            MessageBox.Show("Unable to locate MapleStory.exe.\r\n\r\nPlease move this client into your maplestory folder.");
                        }
                        else
                        {
                            ProcessStartInfo startInfo = new ProcessStartInfo(string.Concat(path, "\\", "MapleStory.exe"));
                            startInfo.Arguments = "WEBSTART " + user.token;
                            startInfo.UseShellExecute = false;
                            Process.Start(startInfo);
                        }
                    }
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show("Unable to request account details!", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }
    }

    class account
    {
        public int response;
        public string name;
        public string token;
    }
}
