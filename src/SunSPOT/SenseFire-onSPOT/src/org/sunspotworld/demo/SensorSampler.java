package org.sunspotworld.demo;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.io.IScalarInput;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.util.Utils;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class SensorSampler extends MIDlet {

    private static final int HOST_PORT = 67;
    private static final int SAMPLE_PERIOD = 2 * 1000;  
    
    protected void startApp() throws MIDletStateChangeException {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        String ourAddress = System.getProperty("IEEE_ADDRESS");
        long now = 0L;
        IScalarInput lightSensor =  EDemoBoard.getInstance().getLightSensor();
        int reading = 0;
        int threshold = 500;
        ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
        
        System.out.println("Starting sensor sampler application on " + ourAddress + " ...");
        new com.sun.spot.util.BootloaderListener().start();       
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
                reading = lightSensor.getValue();
                
                leds[7].setRGB(255, 255, 255);
                leds[7].setOn();
                   
                dg.reset();
                dg.writeLong(now);
                dg.writeInt(reading);
                rCon.send(dg);

                if (reading >= threshold) {

                    System.out.println("There is fire as Light value = " + reading);

                }
                else {

                    System.out.println("There is no fire as Light value = " + reading);
                    
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
