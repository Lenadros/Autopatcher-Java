/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.micromanager.api.ScriptInterface;
import org.micromanager.api.MMPlugin;
import mmcorej.MMCoreJ;
import org.micromanager.api.MMListenerInterface;

/**
 *
 * @author Leonard
 */
public class AP_Main implements org.micromanager.api.MMPlugin
{
    public static final String menuName = "Autopatcher";
    public static final String tooltipDescription = "Autopatching software";
    private ScriptInterface MMScript;
    private AP_Frame MainFrame;
    
    @Override
    public void dispose() {

    }

    @Override
    public void setApp(ScriptInterface si) 
    {
        MMScript = si;
        if(MainFrame == null)
        {
            try {
                MainFrame = new AP_Frame(MMScript);
            } catch (Exception ex) {
                Logger.getLogger(AP_Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            MainFrame.setBackground(MMScript.getBackgroundColor());
            MMScript.addMMBackgroundListener(MainFrame);
            //si.addMMListener((MMListenerInterface) MainFrame);
        }
        MainFrame.setVisible(true);
    }

    @Override
    public void show() 
    {
        String ig = "Autopatcher";
    }

    @Override
    public String getDescription() {
        return "Autopatching software";
    }

    @Override
    public String getInfo() {
        return "Autopatching software";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getCopyright() {
        return "Precision Biosystem Laboratory, 2015";
    }
    
}
