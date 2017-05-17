package org.sunspotworld.demo;

import com.sun.spot.io.j2me.radiogram.*;

import com.sun.spot.peripheral.ota.OTACommandServer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.microedition.io.*;

import java.io.*;


public class SendDataDemoHostApplication {
    
    private static final int HOST_PORT = 67;
    public static String Sensor = "0014.4F01.0000.81DE";
    public static int threshold = 500;
        
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
                int val = dg.readInt();         

                if (addr.equals(Sensor)) {

                    if ((flag1 == 0) && (val >= threshold) ) {
                        
                        File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                        + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"firedata.txt");
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
                        BufferedWriter firedata = new BufferedWriter(fstream);
                        String fireContent = "fireData 1";
                        Date date = new Date(); 
                        String fireTime = "timeOfFire " + dateFormat.format(date);
                        String timeInLong = "timeInLong " + System.currentTimeMillis();
                        
                        firedata.write(fireContent + " " + fireTime + " " + timeInLong);
                        // Close the output stream
                        firedata.close();
                        
                        System.out.println(fireTime + "  Fire data from Sensor with: "
                            + addr + "   value = " + val + " timeInLong = " + timeInLong);
                        flag1 = 1;
                        
                    } else if (flag1 == 1) {

                        if (val < threshold)  {

                            count1 = count1 + 1;
                            System.out.println("Waiting for no fire data : "+count1+ " count");
                            if (count1 == 4) {
                                File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"nofiredata.txt");
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
                                
                                BufferedWriter nofiredata = new BufferedWriter(fstream);
                                String noFireContent = "noFireData 0";
                                Date date = new Date(); 
                                String noFireTime = "timeOfNoFire " + dateFormat.format(date);
                                String timeInLong = "timeInLong " + System.currentTimeMillis();
                                
                                nofiredata.write(noFireContent + " " + noFireTime + " " + timeInLong);
                                // Close the output stream
                                nofiredata.close();
                                
                                System.out.println(noFireTime + "  Fire data from Sensor with: "
                                + addr + "   value = " + val + " timeInLong = " + timeInLong);
                                flag1 = 0;
                                count1 = 0;
                            }
                        
                        } else if (val >= threshold) {
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

        SendDataDemoHostApplication app = new SendDataDemoHostApplication();
        app.run();
    }
}
