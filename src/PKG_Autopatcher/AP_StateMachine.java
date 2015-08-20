/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.util.ArrayList;

/**
 *
 * @author Leonard
 */
public class AP_StateMachine extends Thread
{
    public enum SMState
    {
        START,
        UDPATE,
        END
    }   
    
    private AP_Frame MainFrame;
    private SMState CurrentState;
    private ArrayList<AP_State> StateList;
    private int StateCounter;
    
    public AP_StateMachine(AP_Frame pFrame)
    {
        MainFrame = pFrame;
        CurrentState = SMState.START;
        StateCounter = 0;
        
        StateList = new ArrayList<>();
        StateList.add(new AP_State(this, "Test"));
        
        MainFrame.SetStateTitle("Welcome to Autopatcher");
        System.out.println("State Machine Initialized");
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if(Update() == false)
                break;
        }
    }
    
    public boolean Update()
    {
        if(CurrentState == SMState.START)
        {
            if(StateList.size() == StateCounter)
            {
                return false;
            }
            else
            {
                StateList.get(StateCounter).Start();
                CurrentState = SMState.UDPATE;
            }
        }
        else if (CurrentState == SMState.UDPATE)
        {
            while(StateList.get(StateCounter).Update() == true)
            {
                //Do Something Here If Necessary
            }
            CurrentState = SMState.END;
        }
        else if(CurrentState == SMState.END)
        {
            StateList.get(StateCounter).End();
            StateCounter++;
            CurrentState = SMState.START;
        }
        return true;
    }
}
