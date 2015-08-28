/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PKG_Autopatcher;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import mmcorej.CMMCore;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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
    
    private SMState CurrentState;
    private ArrayList<AP_State> StateList;
    private int StateCounter;
    
    public AP_StateMachine(AP_Frame pFrame)
    {
        MainFrame = pFrame;
        CurrentState = SMState.START;
        StateCounter = 0;
        
        MMCore = new CMMCore();
        LoadDevices();
        
        StateList = new ArrayList<>();
        StateList.add(new AP_State(this, "Test"));
        
        MainFrame.SetStateTitle("Welcome to Autopatcher");
        System.out.println("State Machine Initialized");
        /*
        try
        {
            MMCore.loadDevice("Camera", "DemoCamera", "DCam");
            MMCore.initializeAllDevices();
            MMCore.setExposure(70);
            
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
        
            try
            {
            MMCore.snapImage();
            byte[] image = (byte[])MMCore.getImage();
            Mat MatImage = new Mat((int)MMCore.getImageHeight(), (int)MMCore.getImageWidth(),CvType.CV_8U);// CvType.CV_8UC1);
            //Mat FinalMat = new Mat(MatImage.rows(), MatImage.cols(), MatImage.type());
            MatImage.put(0, 0, image);
            //Imgproc.equalizeHist(MatImage, FinalMat);
            displayImage(Mat2BufferedImage(MatImage));
            }
            catch(Exception e)
            {
                System.out.println("Exception: " + e.getMessage());
            }*/
        
    }
    
    public void LoadDevices()
    {
        try
        {
            //Initialize stage and manipulator/pipet devices
            MMCore.loadDevice("StageXY", "Scientifica", "XYStage");
            MMCore.loadDevice("StageZ", "Scientifica", "ZStage");
            MMCore.loadDevice("PipetXY", "Scientifica", "XYStage");
            MMCore.loadDevice("PipetZ", "Scientifica", "ZStage");
            MMCore.setProperty("StageXY", "Port", "COM3");
            MMCore.setProperty("StageZ", "Port", "COM3");
            MMCore.setProperty("PipetXY", "Port", "COM4");
            MMCore.setProperty("PipetZ", "Port", "COM4");
            MMCore.initializeAllDevices();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
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
        
        MainFrame.EventQueue.clear();
        return true;
    }
    
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
    }
}
