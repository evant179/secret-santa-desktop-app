using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using IWshRuntimeLibrary;
using Shell32;
using System.IO;

// References:
// http://www.codeproject.com/Articles/146757/Add-Remove-Startup-Folder-Shortcut-to-Your-App
// why we set our "IWshRuntimeLibrary" and "Shell32" references property "Embed Interop Types"
//   to false:
// http://msdn.microsoft.com/en-us/library/dd409610.aspx

namespace FileToFavoritesProject
{
    class FileToFavorites
    {
        //d
        // We were missing this attribute originally
        // For reading: http://stackoverflow.com/questions/1361033/what-does-stathread-do
        [STAThread]
        public static void Main()
        {
            FolderBrowserDialog folderDialog = new FolderBrowserDialog();

            if (folderDialog.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    // get selected folder path
                    string selectedPath = folderDialog.SelectedPath;

                    //creates shortcut in SendTo Folder
                    CreateShortcutInSendTo(selectedPath);

                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }
            }
        }
        public static void CreateShortcutInSendTo(String executablePath)
        {
            // Create a new instance of WshShellClass
            // NOTE: in .Net 4, remove "Class" from WshShellClass to use.
            // http://stackoverflow.com/questions/2483659/interop-type-cannot-be-embedded
            IWshShell lib = new IWshShell_Class();

            // Create the shortcut
            IWshRuntimeLibrary.IWshShortcut MyShortcut;


            // Choose the path for the shortcut
            string deskDir = Environment.GetFolderPath(Environment.SpecialFolder.SendTo);

            //parses file name from path and names the shortcut
            string dirName = Path.GetFileName(executablePath);
            MyShortcut = (IWshRuntimeLibrary.IWshShortcut)lib.CreateShortcut(@deskDir + String.Format("\\{0}.lnk", dirName));


            // Where the shortcut should point to
            //MyShortcut.TargetPath = Application.ExecutablePath;
            MyShortcut.TargetPath = @executablePath;

            // Description for the shortcut
            MyShortcut.Description = "Shortcut to " + dirName;

            // Create the shortcut at the given path
            MyShortcut.Save();
        }
    }
}
