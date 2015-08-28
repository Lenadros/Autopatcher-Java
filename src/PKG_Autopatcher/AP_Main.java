/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import org.micromanager.api.ScriptInterface;
import mmcorej.MMCoreJ;

/**
 *
 * @author Leonard
 */
public class AP_Main implements org.micromanager.api.MMPlugin
{
    public static String menuName = "Autopatcher";
    private ScriptInterface ScriptI;
    private AP_Frame MainFrame;
    private AP_StateMachine StateMachine;
    
    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setApp(ScriptInterface si) 
    {
        ScriptI = si;
    }

    @Override
    public void show() 
    {
        //ScriptI.enableLiveMode(true);
        
        //Open main autopatcher frame        
        MainFrame = new AP_Frame();
        MainFrame.setVisible(true);
        
        //Start state machine thread
        StateMachine = new AP_StateMachine(MainFrame);
        StateMachine.start();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVersion() {
        return "1.0.0";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCopyright() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
