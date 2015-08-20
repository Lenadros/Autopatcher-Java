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
    private String Name;
    private AP_StateMachine StateMachine;
    private int lol;
    
    public AP_State(AP_StateMachine pStateMachine, String pName)
    {
        StateMachine = pStateMachine;
        Name = pName;
        lol = 0;
        System.out.println("State has been created");
    }
    
    public void Start()
    {
        System.out.println(Name + ": Start");
    }
    
    public boolean Update()
    {
        return true;
    }
    
    public void End()
    {
        System.out.println(Name + ": End");
    }
}
