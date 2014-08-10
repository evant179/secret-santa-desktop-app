using IWshRuntimeLibrary;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace FileToFavorites
{
    class Program
    {
        static void Main(string[] args)
        {
            string path = @"C:\Users\Owner\Documents\JavaPrograms";
           createShortcutOnDesktop(path);
        }

        public static void createShortcutOnDesktop(String executablePath)
        {
            // Create a new instance of WshShellClass

            IWshShell lib = new IWshShell_Class();
            // Create the shortcut

            IWshRuntimeLibrary.IWshShortcut MyShortcut;


            // Choose the path for the shortcut
            string deskDir = Environment.GetFolderPath(Environment.SpecialFolder.SendTo);
            MyShortcut = (IWshRuntimeLibrary.IWshShortcut)lib.CreateShortcut(@deskDir + "\\AZ.lnk");


            // Where the shortcut should point to

            //MyShortcut.TargetPath = Application.ExecutablePath;
            MyShortcut.TargetPath = @executablePath;

            // Description for the shortcut

            MyShortcut.Description = "Launch AZ Client";

            // Create the shortcut at the given path

            MyShortcut.Save();

        }
    }
}
