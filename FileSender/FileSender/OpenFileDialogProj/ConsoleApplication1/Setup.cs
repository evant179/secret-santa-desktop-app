using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Permissions;
using System.Text;
using System.Threading.Tasks;
using System;
using System.Security.Permissions;
using Microsoft.Win32;
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;
namespace Setup
{
    class Setup
    {
        static void Main(string[] args)
        {

            //Paths where the new keys will be stored in the registry
            string MenuName = @"*\shell\Add New Favorite";
            string Command = @"*\shell\Add New Favorite\Command";

            //This gets the absolute path to the FileToFavorites.exe file.
            //Since I put Setup.exe in the same directory as FileToFavorites.exe,
            //I just got the absolute path of this file and replaced "Setup" with
            //"FileToFavorites"
            string currentProgramPath = Path.GetFullPath(Application.ExecutablePath);
            string newPathToProgram = currentProgramPath.Replace("Setup", "FileToFavorites");


            RegistryKey regmenu = null;
            RegistryKey regcmd = null;
            try
            {
                //creates the actual keys in the registry.  Basically this adds the 
                //"Add New Favorite" to the explorer context menu.
                regmenu = Registry.ClassesRoot.CreateSubKey(MenuName);
                regcmd = Registry.ClassesRoot.CreateSubKey(Command);

                //this sets the command subkey to have a value that will open up filetofavorites.exe
                //on click
                regcmd.SetValue("", newPathToProgram);

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            finally
            {
                if (regmenu != null)
                    regmenu.Close();
                if (regcmd != null)
                    regcmd.Close();
            }


        }
    }
}
