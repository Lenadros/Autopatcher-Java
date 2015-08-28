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
public class AP_State 
{
    protected String Name;
    protected AP_StateMachine StateMachine;
    
    public AP_State(AP_StateMachine pStateMachine, String pName)
    {
        StateMachine = pStateMachine;
        Name = pName;
        System.out.println("State has been created");
    }
    
    public void Start()
    {
        System.out.println(Name + ": Start");
    }
    
    public boolean Update()
    {
        //Update function
        //Return true if Update is to continue
        //Return false if Update is to stop
        return true;
    }
    
    public void End()
    {
        System.out.println(Name + ": End");
    }
}
