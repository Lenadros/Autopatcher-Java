/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

/**
 *
 * @author Leonard
 */
public class AP_Calibration extends AP_State
{

    public AP_Calibration(AP_StateMachine pStateMachine, String pName) 
    {
        super(pStateMachine, pName);
    }
    
    @Override
    public void Start()
    {
        try
        {
            //Zero pippet and stage coordinate system
            StateMachine.MMCore.setOrigin("Stage");
            StateMachine.MMCore.setOrigin("Pipet");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public boolean Update()
    {
        return true;
    }
     
    @Override
    public void End()
    {
    }
}
