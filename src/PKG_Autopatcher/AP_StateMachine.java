/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import mmcorej.CMMCore;
import mmcorej.StrVector;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;

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
    
    public CMMCore MMCore;
    public AP_Frame MainFrame;
    
    public double CalibAngle;
    
    private SMState CurrentState;
    private ArrayList<AP_State> StateList;
    private volatile ArrayList<ActionEvent> EventList;
    private int StateCounter;
    private StrVector DeviceLabels;
    
    public AP_StateMachine(AP_Frame pFrame, CMMCore pMMCore)
    {
        MainFrame = pFrame;
        CurrentState = SMState.START;
        StateCounter = 0;
        
        MMCore = pMMCore;
        //LoadDevices();
        
        StateList = new ArrayList<>();
        //StateList.add(new AP_Calibration(this, "Test"));
        StateList.add(new AP_PipetSetup(this, "SelectCell"));
        EventList = new ArrayList<>();
        
        MainFrame.SetStateTitle("Welcome to Autopatcher");
        System.out.println("State Machine Initialized");
        
        /*
        try
        {
            MMCore.loadDevice("StageXY", "Scientifica", "XYStage");
            MMCore.initializeDevice("StageXY");
            //MMCore.setExposure(70);
            
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
        /*
            try
            {
                MMCore.snapImage();
                byte[] image = (byte[])MMCore.getImage();
                //Mat MatImage = new Mat((int)MMCore.getImageHeight(), (int)MMCore.getImageWidth(),CvType.CV_8U);// CvType.CV_8UC1);
                //Mat FinalMat = new Mat(MatImage.rows(), MatImage.cols(), MatImage.type());
                //MatImage.put(0, 0, image);
                //Imgproc.equalizeHist(MatImage, FinalMat);
                //displayImage(Mat2BufferedImage(MatImage));
                long width = MMCore.getImageWidth();
                long height = MMCore.getImageHeight();
                System.out.println(width + ":" + height);
            }
            catch(Exception e)
            {
                System.out.println("Exception: " + e.getMessage());
            }*/
        
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            //System.out.println(MainFrame.EventList.size());
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
            if(StateList.get(StateCounter).Update() == false)
            {
                CurrentState = SMState.END;
            }
        }
        else if(CurrentState == SMState.END)
        {
            StateList.get(StateCounter).End();
            StateCounter++;
            CurrentState = SMState.START;
        }
        
        //if(!MainFrame.EventQueue.isEmpty())
            //MainFrame.EventQueue.clear();
        
        return true;
    }
    /*
    public BufferedImage Mat2BufferedImage(Mat m)
    {
    // source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
    // Fastest code
    // The output can be assigned either to a BufferedImage or to an Image

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 )
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;
    }
    
    public void displayImage(Image img2)
    {   
        System.out.println(img2.getWidth(null));
        System.out.println(img2.getHeight(null));
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon=new ImageIcon(img2);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());        
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }*/
    
    public void AddEvent(ActionEvent pEvent)
    {
        EventList.add(pEvent);
        System.out.println(EventList.size());
    }
    
    public int GetEventSize()
    {
        return EventList.size();
    }
    
    public ActionEvent GetEvent()
    {
        return EventList.get(0);
    }
    
    public void RemoveEvent()
    {
        EventList.remove(0);
    }
}
