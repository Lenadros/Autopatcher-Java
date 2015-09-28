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
 * @author Leonard
 */
public class AP_PipetteSetup extends AP_State
{
    double CellX, CellY, CellZ;
    boolean bInputFlag;
    double CalibAngle, MagRatio;
    double ScaledX, ScaledY;
    double PipetX, PipetY, PipetZ, ZSlice, Z_CP;
    double Gamma;
    double DistToDescend;
    int StepCounter;
    String TermCommand, ApproachCommand;
    
    public AP_PipetteSetup(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        bInputFlag = false;
        MagRatio = 1;
        Gamma = 0.57595;
        StepCounter = 0;
        TermCommand = "\r\n";
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("Cell Select");
        StateMachine.MainFrame.SetMessage("Please center the pipette on the screen and press proceed.");
        CalibAngle = StateMachine.CalibAngle;
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
                    try
                    {
                        StateMachine.MMCore.setOriginXY("XY-Pipette");
                        StateMachine.MMCore.setOrigin("Z-Pipette");
                        StateMachine.MMCore.setOriginXY("XY-Stage");
                        StateMachine.MMCore.setOrigin("Z-Stage");
                        StateMachine.MainFrame.SetMessage("Please select a target cell and then press proceed.");
                        StepCounter = 1;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    bInputFlag = false;
                break;
                case 1:
                    try
                    {
                        //Get cell position
                        CellX = StateMachine.MMCore.getXPosition("XY-Stage");
                        CellY = StateMachine.MMCore.getYPosition("XY-Stage");
                        CellZ = StateMachine.MMCore.getPosition("Z-Stage");
                        StateMachine.MainFrame.SetMessage("Please move the stage to the surface of the slice.");
                        StateMachine.MainFrame.SetDataMessage("Cell Position: (" + CellX + "," + CellY + "," + CellZ + ")");
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, "Cell Position: (" + CellX + "," + CellY + "," + CellZ + ")");
                        StepCounter = 2;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    bInputFlag = false;
                break;
                case 2:
                    try
                    {
                        ZSlice = StateMachine.MMCore.getPosition("Z-Stage");
                        StateMachine.MainFrame.SetDataMessage("Top of Slice: Z = " + ZSlice);
                        StateMachine.MainFrame.SetMessage("Please enter command for device approach.");
                        StepCounter = 3;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    bInputFlag = false;
                break;
                case 3:
                    try
                    {
                        ApproachCommand = StateMachine.MainFrame.ReadCalibrationField();
                        StateMachine.MainFrame.SetDataMessage("Approach Command: " + ApproachCommand);
                        StateMachine.MainFrame.SetMessage("Press Proceed to Park Pipette.");
                        StepCounter = 4;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        return true;
                    }
                    bInputFlag = false;
                break;
                case 4:
                    try
                    {
                        //Rotation transform from stage coordinates to pipet coordinates
                        double Theta = Math.atan2(CellY, CellX);
                        Theta = Theta - CalibAngle;
                        double MoveMag = Math.sqrt(Math.pow(CellX, 2) + Math.pow(CellY, 2)) * MagRatio;

                        ScaledX = -(Math.cos(Theta) * (MoveMag));
                        ScaledY = Math.sin(Theta) * (MoveMag);
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, "Cell Pipette: (" + ScaledX + "," + ScaledY + ")");

                        //Park pippet on top of slice
                        PipetX = (ZSlice -  CellZ)/Math.tan(Gamma) + ScaledX;
                        PipetY = ScaledY;
                        PipetZ = ZSlice;
                        Z_CP = CellZ;

                        //Calculate distance to descend
                        DistToDescend = Math.sqrt(Math.pow((ScaledX - PipetX),2) + Math.pow((ScaledY - PipetY), 2) + Math.pow(Z_CP - PipetZ, 2));
                        
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, "Pipette Position: (" + PipetX + "," + PipetY + "," + PipetZ + ")");
                        StepCounter = 5;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        bInputFlag = false;
                        return true;
                    }
                case 5:
                    try
                    {
                        StateMachine.MMCore.setRelativeXYPosition("XY-Pipette", PipetX, PipetY);
                        StepCounter = 6;
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        bInputFlag = false;
                        return true;
                    }
                break;
                case 6:
                    try
                    {
                        if(!StateMachine.MMCore.deviceBusy("XY-Pipette"))
                        {
                            StateMachine.MMCore.setRelativePosition("Z-Pipette", PipetZ);
                            return false;
                        }
                    }
                    catch(Exception e)
                    {
                        Logger.getLogger(AP_PipetteSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                        bInputFlag = false;
                        return true;
                    }
                break;
            }
        }
        return true;
    }
    
    @Override
    public void End()
    {
        
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
