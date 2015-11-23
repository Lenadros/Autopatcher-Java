/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltsai8
 */
public class AP_DAQIO extends AP_State
{
    String fileName = "D:\\NIDAQApp\\NIDAQApp\\NIDAQApp\\data.txt";
    FileReader fileReader;
    BufferedReader bufferedReader;
    
    public AP_DAQIO(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        Logger.getLogger(AP_DAQIO.class.getName()).log(Level.WARNING, "DAQ State Created");
        try
        {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
        }
        catch(FileNotFoundException ex)
        {
            Logger.getLogger(AP_DAQIO.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("Data Aquisition");
        Logger.getLogger(AP_DAQIO.class.getName()).log(Level.WARNING, "DAQ State Started");
    }
    
    @Override
    public boolean Update()
    {
        String line = null;
        try
        {
            while((line = bufferedReader.readLine()) != null)
            {
                Logger.getLogger(AP_DAQIO.class.getName()).log(Level.WARNING, line);
            }
        }
        catch(IOException ex)
        {
            Logger.getLogger(AP_DAQIO.class.getName()).log(Level.WARNING, ex.getMessage());
        }
        return true;
    }
    
    @Override
    public void End()
    {
        Logger.getLogger(AP_DAQIO.class.getName()).log(Level.WARNING, "DAQ State Ended");
    }
}
