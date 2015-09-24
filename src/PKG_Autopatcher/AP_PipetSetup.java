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
public class AP_PipetSetup extends AP_State
{
    double CellX, CellY, CellZ;
    boolean bInputFlag;
    double CalibAngle, MagRatio;
    double ScaledX, ScaledY;
    double PipetX, PipetY, PipetZ, Z_CP;
    double Gamma;
    double DistToDescend;
    
    public AP_PipetSetup(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
        bInputFlag = false;
        MagRatio = 1;
        Gamma = 1;
    }
    
    @Override
    public void Start()
    {
        StateMachine.MainFrame.SetStateTitle("Cell Select");
        StateMachine.MainFrame.SetMessage("Please select a target cell and then press proceed.");
        CalibAngle = StateMachine.CalibAngle;
    }
    
    @Override
    public boolean Update()
    {
        PollInput();
        
        if(bInputFlag)
        {
            try
            {
                //Get cell position
                CellX = StateMachine.MMCore.getXPosition("XY-Stage");
                CellY = StateMachine.MMCore.getYPosition("XY-Stage");
                CellZ = StateMachine.MMCore.getPosition("Z-Stage");
                
                //Rotation transform from stage coordinates to pipet coordinates
                double Theta = Math.atan2(CellY, CellX);
                Theta = Theta - CalibAngle;
                double MoveMag = Math.sqrt(Math.pow(CellX, 2) + Math.pow(CellY, 2));
                
                ScaledX = -(Math.cos(Theta) * (MoveMag * MagRatio));
                ScaledY = Math.sin(Theta) * (MoveMag * MagRatio);
                
                //Park pippet on top of slice
                double ZStageTop = StateMachine.MMCore.getPosition("Z-Stage");
                PipetX = (ZStageTop -  CellZ)/Math.tan(Gamma) + ScaledX;
                PipetY = CellY;
                PipetZ = ZStageTop;
                Z_CP = CellZ;
                
                //Calculate distance to descend
                DistToDescend = Math.sqrt(Math.pow((ScaledX - PipetZ),2) + Math.pow((ScaledY - PipetY), 2) + Math.pow(Z_CP - PipetZ, 2));
                
                StateMachine.MMCore.setXYPosition("XY-Pipet", PipetX, PipetY);
                StateMachine.MMCore.setPosition("Z-Pipet", PipetX);
                
            }
            catch(Exception e)
            {
                Logger.getLogger(AP_PipetSetup.class.getName()).log(Level.SEVERE, e.getMessage());
                return false;
            }
            return true;
        }
        return false;
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
