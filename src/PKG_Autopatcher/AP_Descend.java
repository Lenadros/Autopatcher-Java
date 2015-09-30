/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.CharVector;

/**
 *
 * @author ltsai8
 */
public class AP_Descend extends AP_State
{
    boolean bInputFlag;
    int StepCounter;
    String TermCommand, ApproachCommand, SetStepCommand, StepCommand;
    
    public AP_Descend(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        TermCommand = "\r\n";
        bInputFlag = false;
        StepCounter = 0;
        ApproachCommand = "APROACH";
        SetStepCommand = "SETSTEP";
        StepCommand = "STEP";
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("Pipette Descend");
        StateMachine.MainFrame.SetMessage("Please press proceed to descend to cell.");
    }
    
    @Override
    public boolean Update()
    {
        PollInput();
        
        if(bInputFlag)
        {
            switch(StepCounter)
            {
                /*
                case 0:
                    ApproachCommand = StateMachine.MainFrame.ReadCalibrationField();
                    StateMachine.MainFrame.SetDataMessage("Approach Command: " + ApproachCommand);
                    StateMachine.MainFrame.SetMessage("Press proceed to descend pipette.");
                    bInputFlag = false;
                    StepCounter = 1;
                break;*/
                case 0:
                    try
                    {
                        StateMachine.MMCore.setSerialPortCommand("COM14", ApproachCommand + " 1", TermCommand);
                        StepCounter = 1;
                    }
                    catch(Exception e)
                    {
                        bInputFlag = false;
                        Logger.getLogger(AP_Descend.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    //bInputFlag = false;
                break;
                case 1:
                    try
                    {
                        int Distance = (int)(-StateMachine.DescendDist * 10);
                        StateMachine.MMCore.setSerialPortCommand("COM14", SetStepCommand + " " + String.valueOf(Distance), TermCommand);
                        StepCounter = 2;
                    }
                    catch(Exception e)
                    {
                        bInputFlag = false;
                        Logger.getLogger(AP_Descend.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    //bInputFlag = false;
                break;
                case 2:
                    try
                    {
                        StateMachine.MMCore.setSerialPortCommand("COM14", StepCommand, TermCommand);
                        StepCounter = 3;
                        StateMachine.MainFrame.SetMessage("Descent finished.");
                        return false;
                    }
                    catch(Exception e)
                    {
                        bInputFlag = false;
                        Logger.getLogger(AP_Descend.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
            }
        }
        
        return true;
    }
    
    @Override
    public void End()
    {
        try 
        {
            StateMachine.MMCore.setSerialPortCommand("COM14", ApproachCommand + " 0", TermCommand);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(AP_Descend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void PollInput()
    {
        //Only poll for button presses if the event array has events
        if(StateMachine.GetEventSize() > 0)
        {
            //If the correct button is pressed, set the input flag
            if(StateMachine.GetEvent().getActionCommand() == "Proceed" && !bInputFlag)
            {
                bInputFlag = true;
                StateMachine.RemoveEvent();
            }
            else
            {
                //Remove GUI event if not the correct one
                StateMachine.RemoveEvent();
            }
        }
    }
}
