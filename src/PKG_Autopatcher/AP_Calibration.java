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
public class AP_Calibration extends AP_State
{
    private int StepCounter;
    private int XCalibDist;
    private int YCalibDist;
    private boolean bInputFlag, bZeroInFlag;
    private boolean bXDistSet;
    private double XP1, YP1, XP2, YP2, XPipetStart, YPipetStart;
    private double Theta1, Theta2, CalibTheta;
    private String XYStage, XYPipet, ZStage, ZPipet;
    
    public AP_Calibration(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        StepCounter = 0;
        XCalibDist = 0;
        YCalibDist = 0;
        XPipetStart = 0;
        YPipetStart = 0;
        bInputFlag = false;
        bZeroInFlag = false;
        bXDistSet = false;
        XYStage = "XY-Stage";
        XYPipet = "XY-Pipet";
        ZStage = "Z-Stage";
        ZPipet = "Z-Pipet";
    }
    
    
    @Override
    public void Start()
    {
        //Initial set up of state variables and messages
        StateMachine.MainFrame.SetStateTitle("Calibration");
        StateMachine.MainFrame.SetMessage("Please enter the X calibration distance and then press proceed.");
        StateMachine.MainFrame.SetXDist("X: Not Set");
        StateMachine.MainFrame.SetYDist("Y: Not Set");
        StateMachine.MainFrame.SetProgressBar(0);
    }
    
    @Override
    public boolean Update()
    {
        PollInput();
        
        switch(StepCounter)
        {
            case 0:
                if(bInputFlag)
                {
                    String InputString = StateMachine.MainFrame.ReadCalibrationField();
                    try
                    {
                        int CalibDist = Integer.parseInt(InputString);
                        if(!bXDistSet)
                        {
                            XCalibDist = CalibDist;
                            StateMachine.MainFrame.SetXDist("X: " + XCalibDist);
                            StateMachine.MainFrame.SetMessage("Please enter the Y calibration distance and then press proceed.");
                            bXDistSet = true;
                        }
                        else
                        {
                            YCalibDist = CalibDist;
                            StateMachine.MainFrame.SetYDist("Y: " + YCalibDist);
                            StepCounter = 1;
                            StateMachine.MainFrame.SetMessage("Center the pipet on the screen and the press proceed.");
                            StateMachine.MainFrame.SetProgressBar(25);
                        }
                    }
                    catch(Exception e)
                    {
                       StateMachine.MainFrame.SetMessage("Error: Please enter a numeric value!");
                       System.out.println(e.getMessage());
                    }
                    
                    bInputFlag = false;
                }
                break;
            case 1:
                if(bInputFlag)
                {
                    try
                    {
                        //Zero pippet and stage coordinate system
                        //StateMachine.MMCore.setOrigin("StageZ");
                        //StateMachine.MMCore.setOrigin("PipetZ");
                        //StateMachine.MMCore.setOriginXY(XYStage);
                        XPipetStart = StateMachine.MMCore.getXPosition(XYPipet);
                        YPipetStart = StateMachine.MMCore.getYPosition(XYPipet);
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "XPipetStart: " + XPipetStart + " YPipetStart: " + YPipetStart);
                        //StateMachine.MMCore.setOriginXY(XYPipet);
                        
                        //Move the stage to user defined X position
                        StateMachine.MMCore.setRelativeXYPosition(XYStage, XCalibDist, 0);
                        
                        //Display message for next step
                        StateMachine.MainFrame.SetMessage("Center the pipet on the screen again and then press proceed.");
                        StepCounter = 2;
                        StateMachine.MainFrame.SetProgressBar(50);
                    }
                    catch(Exception e)
                    {
                        StateMachine.MainFrame.SetMessage("Error: Unable to set origin or move stage!");
                        System.out.println(e.getMessage());
                    }
                    bInputFlag = false;
                }
                break;
            case 2:
                if(bInputFlag)
                {
                    try
                    {
                        //StateMachine.MMCore.getXYPosition(PipetLabel, XP1, XP1);
                        XP1 = StateMachine.MMCore.getXPosition(XYPipet);
                        YP1 = StateMachine.MMCore.getYPosition(XYPipet);
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "XP1: " + XP1 + " YP1: " + YP1);
                        XP1 = XP1 - XPipetStart;
                        YP1 = YP1 - YPipetStart;
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "XP1: " + XP1 + " YP1: " + YP1);
                        Theta1 = Math.atan2(YP1, XP1);
                        Theta1 = Theta1 + Math.PI;
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "Theta1: " + Theta1);
                        //StateMachine.MainFrame.SetMessage("XP1: " + XP1 + " YP1: " + YP1);
                        
                        StateMachine.MMCore.setRelativeXYPosition(XYStage, 0, YCalibDist);
                        StepCounter = 3;
                        StateMachine.MainFrame.SetProgressBar(75);
                    }
                    catch(Exception e)
                    {
                        StateMachine.MainFrame.SetMessage("Error: Unable to get position or move stage!");
                        System.out.println(e.getMessage());      
                    }
                    bInputFlag = false;
                }
                //StateMachine.MainFrame.SetMessage("Center the pipet on the screen again and then press proceed.");
                break;
            case 3:
                if(bInputFlag)
                {
                    try
                    {
                        XP2 = StateMachine.MMCore.getXPosition(XYPipet);
                        YP2 = StateMachine.MMCore.getYPosition(XYPipet);
                        XP2 = XP2 - XPipetStart;
                        YP2 = YP2 - YPipetStart;
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "XP2: " + XP2 + " YP2: " + YP2);
                        Theta2 = Math.atan2(YP2-YP1, XP2-XP1);
                        Theta2 = Theta2 - Math.PI/2;
                        Logger.getLogger(AP_Calibration.class.getName()).log(Level.SEVERE, "Theta2: " + Theta2);
                        
                        //System.out.println(Theta1);
                        //System.out.println(Theta2);
                        CalibTheta = (Theta1 + Theta2)/2;
                        bInputFlag = false;
                        StateMachine.MainFrame.SetProgressBar(100);
                        return false;
                    }
                    catch(Exception e)
                    {
                        StateMachine.MainFrame.SetMessage("Error: Unable to get position!");
                        System.out.println(e.getMessage()); 
                        bInputFlag = false;
                    }
                }
                break;
        }
        
        return true;
    }
     
    @Override
    public void End()
    {
        //StateMachine.MainFrame.SetMessage("Calibration Done!");
        StateMachine.MainFrame.SetMessage("Theta1: " + Theta1 + " Theta2: " + Theta2 + " Angle: " + CalibTheta);
        //System.out.println("Calibration angle was measured as: " + CalibTheta + " Degree(s)");
        StateMachine.CalibAngle = CalibTheta;
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
