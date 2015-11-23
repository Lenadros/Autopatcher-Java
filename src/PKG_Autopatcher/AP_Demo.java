/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltsai8
 */
public class AP_Demo extends AP_State
{
    private boolean DoLoop, StartFlag, EndFlag;
    private int LeftM, RightM, Stage;
    private String TermCommand;
    
    public AP_Demo(AP_StateMachine pStateMachine, String pName)
    {
        super(pStateMachine, pName);
        DoLoop = StartFlag = EndFlag = false;
        LeftM = RightM = Stage = 0;
        TermCommand = "\r\n";
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("DEMO");
        StateMachine.MainFrame.SetMessage("Press the buttons to start or end demo.");
        try {
            StateMachine.MMCore.setSerialPortCommand("COM14", "TOP 30000", TermCommand);
            StateMachine.MMCore.setSerialPortCommand("COM13", "TOP 30000", TermCommand);
            StateMachine.MMCore.setSerialPortCommand("COM3", "TOP 30000", TermCommand);
        } catch (Exception ex) {
            Logger.getLogger(AP_Demo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean Update()
    {
        PollInput();
        
        if(DoLoop)
        {
            try 
            {
                if(!StateMachine.MMCore.deviceBusy("XY-Pipette"))
                {
                    switch(LeftM)
                    {
                        case 0:
                            StateMachine.MMCore.setXYPosition("XY-Pipette", 0, 0);
                            LeftM++;
                        break;
                        case 1:
                            StateMachine.MMCore.setXYPosition("XY-Pipette", 5000, 0);
                            LeftM++;
                        break;
                        case 2:
                            StateMachine.MMCore.setXYPosition("XY-Pipette", 5000, 5000);
                            LeftM++;
                        break;
                        case 3:
                            StateMachine.MMCore.setXYPosition("XY-Pipette", 0, 5000);
                            LeftM = 0;
                        break;
                    }
                }
                if(!StateMachine.MMCore.deviceBusy("XY-Stage"))
                {
                    switch(Stage)
                    {
                        case 0:
                            StateMachine.MMCore.setXYPosition("XY-Stage", 0, 0);
                            Stage++;
                        break;
                        case 1:
                            StateMachine.MMCore.setXYPosition("XY-Stage", 5000, 0);
                            Stage++;
                        break;
                        case 2:
                            StateMachine.MMCore.setXYPosition("XY-Stage", 5000, 5000);
                            Stage++;
                        break;
                        case 3:
                            StateMachine.MMCore.setXYPosition("XY-Stage", 0, 5000);
                            Stage = 0;
                        break;
                    }
                }
                if(!StateMachine.MMCore.deviceBusy("Pipette2"))
                {
                    switch(RightM)
                    {
                        case 0:
                            StateMachine.MMCore.setXYPosition("Pipette2", 0, 0);
                            RightM++;
                        break;
                        case 1:
                            StateMachine.MMCore.setXYPosition("Pipette2", 5000, 0);
                            RightM++;
                        break;
                        case 2:
                            StateMachine.MMCore.setXYPosition("Pipette2", 5000, 5000);
                            RightM++;
                        break;
                        case 3:
                            StateMachine.MMCore.setXYPosition("Pipette2", 0, 5000);
                            RightM = 0;
                        break;
                    }
                }
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(AP_Demo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    @Override
    public void End()
    {
        try {
            StateMachine.MMCore.setSerialPortCommand("COM14", "TOP 50000", TermCommand);
            StateMachine.MMCore.setSerialPortCommand("COM13", "TOP 50000", TermCommand);
            StateMachine.MMCore.setSerialPortCommand("COM3", "TOP 50000", TermCommand);
        } catch (Exception ex) {
            Logger.getLogger(AP_Demo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void PollInput()
    {
        //Only poll for button presses if the event array has events
        if(StateMachine.GetEventSize() > 0)
        {
            //If the correct button is pressed, set the input flag
            if(StateMachine.GetEvent().getActionCommand() == "Start Demo" && !DoLoop)
            {
                DoLoop = true;
                StateMachine.RemoveEvent();
                StateMachine.MainFrame.SetMessage("Demo Started!");
            }
            else if(StateMachine.GetEvent().getActionCommand() == "End Demo" && DoLoop)
            {
                DoLoop = false;
                StateMachine.RemoveEvent();
                StateMachine.MainFrame.SetMessage("Demo Ended.");
            }
            else
            {
                //Remove GUI event if not the correct one
                StateMachine.RemoveEvent();
            }
        }
    }
}
