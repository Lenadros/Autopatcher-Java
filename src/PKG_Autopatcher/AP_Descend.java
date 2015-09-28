/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

/**
 *
 * @author ltsai8
 */
public class AP_Descend extends AP_State
{
    boolean bInputFlag;
    int StepCounter;
    String TermCommand, ApproachCommand;
    
    public AP_Descend(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        TermCommand = "\r\n";
        bInputFlag = false;
        StepCounter = 0;
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("Pipette Descend");
        StateMachine.MainFrame.SetMessage("Please enter in approach command for your manipulator device.");
    }
    
    @Override
    public boolean Update()
    {
        PollInput();
        
        if(bInputFlag)
        {
            switch(StepCounter)
            {
                case 0:
                    ApproachCommand = StateMachine.MainFrame.ReadCalibrationField();
                    StateMachine.MainFrame.SetDataMessage("Approach Command: " + ApproachCommand);
                    StateMachine.MainFrame.SetMessage("Press proceed to descend pipette.");
                    bInputFlag = false;
                    StepCounter = 1;
                break;
                case 1:
                    try
                    {
                        StateMachine.MMCore.setSerialPortCommand("PipettePort", ApproachCommand + " 1", TermCommand);
                        StepCounter = 2;
                    }
                    catch(Exception e)
                    {
                        return true;
                    }
                break;
            }
        }
        
        return true;
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
