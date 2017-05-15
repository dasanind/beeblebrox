package org.sunspotworld.demo;

import com.sun.spot.io.j2me.radiogram.*;

import com.sun.spot.peripheral.ota.OTACommandServer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.microedition.io.*;

import java.io.*;


public class SendAccelerometerDataHostApplication {
    
    private static final int HOST_PORT = 68;
    public static String Sensor = "0014.4F01.0000.80CD";
    
    public static double threshold = 0.1;
        
    private void run() throws Exception {
        RadiogramConnection rCon;
        Datagram dg;
        
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        int flag1 = 0;
        int count1 = 0;
        try {
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
             System.err.println("setUp caught " + e.getMessage());
             throw e;
        }

        while (true) {
            try {
                rCon.receive(dg);
                String addr = dg.getAddress();  
                long time = dg.readLong();      
                double val = dg.readDouble();     
                
                if (addr.equals(Sensor)) {                  
                    if ((flag1 == 0) && (val >= threshold) ) {                       
                        File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                        + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"motiondata.txt");
                        // if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			} else {
                            boolean deleted = file.delete();
                            if(deleted) {
                                System.out.println(file.getName() + " is deleted!");
                                file.createNewFile();
                            }
                        }
                        FileWriter fstream = new FileWriter(file.getAbsoluteFile());
                        
                        BufferedWriter motiondata = new BufferedWriter(fstream);
                        String motionContent = "motionData 1";
                        Date date = new Date(); 
                        String motionTime = "timeOfMotion " + dateFormat.format(date);
                        String timeInLong = "timeInLong " + System.currentTimeMillis();
                        
                        motiondata.write(motionContent + " " + motionTime + " " + timeInLong);
                        // Close the output stream
                        motiondata.close();
                        
                        System.out.println(motionTime + "  Motion data from Sensor with: "
                            + addr + "   value = " + val + " timeInLong = " + timeInLong);
                        flag1 = 1;
                        
                    }
                    else if (flag1 == 1) {

                        if (val < threshold)  {

                            count1 = count1 + 1;
                            System.out.println("Waiting for no motion data : "+count1+ " count");
                            if (count1 == 4) {
                                File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"nomotiondata.txt");
                                // if file doesnt exists, then create it
                                if (!file.exists()) {
                                    file.createNewFile();
                                } else {
                                    boolean deleted = file.delete();
                                    if(deleted) {
                                        System.out.println(file.getName() + " is deleted!");
                                        file.createNewFile();
                                    }   
                                }
                                FileWriter fstream = new FileWriter(file.getAbsoluteFile());
                                
                                BufferedWriter nomotiondata = new BufferedWriter(fstream);
                                String noFireContent = "noMotionData 0";
                                Date date = new Date(); 
                                String noMotionTime = "timeOfNoMotion " + dateFormat.format(date);
                                String timeInLong = "timeInLong " + System.currentTimeMillis();
                                
                                nomotiondata.write(noFireContent + " " + noMotionTime + " " + timeInLong);
                                // Close the output stream
                                nomotiondata.close();
                                
                                System.out.println(noMotionTime + "  Motion data from Sensor with: "
                                + addr + "   value = " + val + " timeInLong = " + timeInLong);
                                flag1 = 0;
                                count1 = 0;
                            }                       
                        }
                        else if (val >= threshold) {
                            count1 = 0;
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                throw e;
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        OTACommandServer.start("SendDataDemo");

        SendAccelerometerDataHostApplication app = new SendAccelerometerDataHostApplication();
        app.run();
    }
}
