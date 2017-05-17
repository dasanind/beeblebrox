package org.sunspotworld.demo;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.util.Utils;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class SensorSampler extends MIDlet {

    private static final int HOST_PORT = 68;
    private static final int SAMPLE_PERIOD = 2 * 1000;  
    
    protected void startApp() throws MIDletStateChangeException {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        String ourAddress = System.getProperty("IEEE_ADDRESS");
        long now = 0L;
        
        double acceleration = 0;
        double threshold = 0.1;
        double accelReading = 0;
        ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
        
        System.out.println("Starting sensor sampler application on " + ourAddress + " ...");
        new com.sun.spot.util.BootloaderListener().start();  
        Spot.getInstance().getSleepManager().disableDeepSleep();
 
        IAccelerometer3D accelerometer = (IAccelerometer3D) Resources.lookup(IAccelerometer3D.class);
        
        try {
            
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + HOST_PORT);
            dg = rCon.newDatagram(50);

        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            System.exit(1);
        }
        
        while (true) {
            try {                
                now = System.currentTimeMillis();
                // wait for motion
                acceleration = accelerometer.getAccel();
                accelReading = Math.abs(acceleration-1.0);
                
                leds[7].setRGB(255, 255, 255);
                leds[7].setOn();                
 
                //Broadcast "moving" signal to remote sunspot
                dg.reset();
                dg.writeLong(now);
                dg.writeDouble(accelReading);
                rCon.send(dg);

                if (accelReading >= threshold) {
                    System.out.println("Door is opened as Accelerometer value = " + accelReading);
                } else {
                    System.out.println("Door is closed as Accelerometer value = " + accelReading);
                }
                leds[7].setOff();                
                
                Utils.sleep(SAMPLE_PERIOD - (System.currentTimeMillis() - now));
            } catch (Exception e) {
                System.err.println("Caught " + e + " while collecting/sending sensor sample.");
            }
        }        
    }
    
    protected void pauseApp() {
        
    }
    
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        
    }
}