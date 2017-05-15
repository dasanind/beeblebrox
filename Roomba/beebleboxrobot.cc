#include <iostream>
#include <libplayerc++/playerc++.h>
#include <fstream>
#include <string>
#include <cstdio>
#include <stdio.h>
#include "args.h"
#include <unistd.h>

using namespace std;

void msleep (int msec) {
  for (int i=0; i<msec; i++)
    usleep(5000); //sleep 5 ms
}


int
main(int argc, char *argv[]) {
  using namespace PlayerCc;
  
  parse_args(argc, argv);
  //PlayerCc::PlayerClient robot("localhost", 6665);
  
  PlayerClient    robot("localhost");
  Position2dProxy pp(&robot,0);
  PlayerCc::PlayerClient client(gHostname, gPort);
  PlayerCc::SpeechProxy sp(&client, gIndex);
  string checkfire;
  string checkmotion;
  string sensor;
  string datatype;
  string datacontent;
  string timecontent;
  string date;
  string time;
  string timeinlongcontent;
  string timeinlong;
  //Input and Output stream for fire files
  ifstream file1;
  ifstream file2;
  ifstream file3;
  ofstream outfile1;
  
  //Input and Output stream for motion files
  ifstream file4;
  ifstream file5;
  ifstream file6;
  ofstream outfile2;
  
  //Fire Files
  char File1[] = "/home/anindita/Dropbox/Public/BeebleBox/checkfire.txt";
  char File2[] = "/home/anindita/Dropbox/Public/BeebleBox/firedata.txt";
  char File3[] = "/home/anindita/Dropbox/Public/BeebleBox/nofiredata.txt";
  char OutFile1[] = "/home/anindita/Dropbox/Public/BeebleBox/firemitigated.txt";
  
  //Motion Files
  char File4[] = "/home/anindita/Dropbox/Public/BeebleBox/checkmotion.txt";
  char File5[] = "/home/anindita/Dropbox/Public/BeebleBox/motiondata.txt";
  char File6[] = "/home/anindita/Dropbox/Public/BeebleBox/nomotiondata.txt";
  char OutFile2[] = "/home/anindita/Dropbox/Public/BeebleBox/motionmitigated.txt";

  double runtime = 1000;
  double runtime1 = 500;
  double runtime2 = 291;

  for(;;) {
    double turnrate, speed, speed1, speed2, speed3;
    int flag = 0;
    int index = 1;
    speed = 0.300;
    speed1 = 0;
    speed2 = 0.165;
    file1.open (File1, ios::in);
    file4.open (File4, ios::in); 
    // read from the proxies
    robot.Read();
   
    if (file1.is_open()) {    
      
      while (!file1.eof()) { 
       //getline from the checkfire file
       getline (file1,checkfire);
       cout << "Check fire: " << checkfire << endl; 
       if(checkfire == "checkfire") {
           file2.open (File2, ios::in);    
           if (file2.is_open()) {   
            	while (!file2.eof()) { 
            	  //getline (file2,sensor);
       		  //cout << "Sensor data " << sensor << endl; 
       		  //pnt = strtok(sensor, delimiter);
       		  //if(strtok.countTokens() > 0) {
       		  while(file2>>datatype>>datacontent>>timecontent>>date>>time>>timeinlongcontent>>timeinlong) {
       		     //datatype = strtok.nextToken();
       		     //datacontent = strtok.nextToken();
       		     cout << "DataType " << datatype << endl; 
       		     cout << "DataContent " << datacontent << endl; 
       		     cout << "TimeContent " << timecontent << endl; 
       		     cout << "Date " << date << endl; 
       		     cout << "Time " << time << endl; 
       		     cout << "TimeinLongContent " << timeinlongcontent << endl; 
       		     cout << "TimeinLong " << timeinlong << endl; 
       		     if ((datatype == "fireData") && (datacontent == "1")) {
       		     	cout<<"I am here inside if" << endl;
       		        turnrate = dtor(0);
        	        //speed = 0.300;
        	        // command the motors
        	        pp.SetSpeed(speed, turnrate);   
       		     }
       		  }
       		  
                }
           }
       }
     }
    std::cout << pp << std::endl;
    msleep(runtime);
    while (index!=flag) {
      pp.SetSpeed(speed1, turnrate); 
      try {
        sp.Say("I am extinguishing fire!\n");	
        msleep(runtime); 
      } catch (PlayerCc::PlayerError e) {
        std::cerr << e << std::endl;
        return -1;
      }
      file3.open (File3, ios::in);
      if (file3.is_open()) {
      cout<< "I m here " << endl;
        while (!file3.eof()) {
          getline (file3,sensor);
          cout << "There is no fire as value = " << sensor << endl;
        }
        index = 0;
      } else {          
        cout << "Keep extinguishing the fire" << endl;
      }
    }

    turnrate = dtor(-184.5);
    //speed2 = 0.185;
    pp.SetSpeed(speed2, turnrate);    
    sp.Say("My work is done\n");
    //runtime = 500;
    msleep(runtime1);
    turnrate = dtor(0);
    pp.SetSpeed(speed1, turnrate);  
    pp.SetSpeed(speed, turnrate);  
    //runtime = 1000;
    msleep(runtime);
    pp.SetSpeed(speed1, turnrate); 
    msleep(runtime);
    turnrate = dtor(188.43);
    speed3 = 0.167;
    pp.SetSpeed(speed3, turnrate);    
    //runtime = 500;
    msleep(runtime1);
    turnrate = dtor(0);
    pp.SetSpeed(speed1, turnrate);  
    sp.Say("\n");
    
    //writing that fire is checked into a file to update the database
    outfile1.open (OutFile1, ios::out);
    if (outfile1.is_open()) {
    	outfile1 << "firemitigated" << endl;
    }
    file3.close();
    file2.close();
    file1.close();
    outfile1.close();
    std::remove("/home/anindita/Dropbox/Public/BeebleBox/checkfire.txt");
    std::remove("/home/anindita/Dropbox/Public/BeebleBox/firedata.txt");
    std::remove("/home/anindita/Dropbox/Public/BeebleBox/nofiredata.txt");
    
   } else if (file4.is_open()) {
     	while (!file4.eof()) {
       	 //getline from the checkmotion file
         getline (file4,checkmotion);
         cout << "Check motion: " << checkmotion << endl; 
       if(checkmotion == "checkmotion") {
           file5.open (File5, ios::in);    
           if (file5.is_open()) {   
            	while (!file5.eof()) { 
       		  while(file5>>datatype>>datacontent>>timecontent>>date>>time>>timeinlongcontent>>timeinlong) {
       		     cout << "DataType " << datatype << endl; 
       		     cout << "DataContent " << datacontent << endl; 
       		     cout << "TimeContent " << timecontent << endl; 
       		     cout << "Date " << date << endl; 
       		     cout << "Time " << time << endl; 
       		     cout << "TimeinLongContent " << timeinlongcontent << endl; 
       		     cout << "TimeinLong " << timeinlong << endl; 
       		     if ((datatype == "motionData") && (datacontent == "1")) {
       		     	//cout<<"I am here inside if" << endl;
       		        turnrate = dtor(-90);
        	        // command the motors
        		speed3 = 0.0848;
        		pp.SetSpeed(speed3, turnrate);   
        		msleep(runtime1);
        		turnrate = dtor(0);
        		pp.SetSpeed(speed1, turnrate);  
        		//speed = 0.300;
        		pp.SetSpeed(speed, turnrate);   
       		     }
       		  }
       		  
                }
           }
       }
     }
    
     std::cout << pp << std::endl;
     msleep(runtime);
     while (index!=flag) {
       pp.SetSpeed(speed1, turnrate); 
       try {
         sp.Say("I am closing the door\n");	
         msleep(runtime); 
       } catch (PlayerCc::PlayerError e) {
         std::cerr << e << std::endl;
         return -1;
       }
       //msleep(runtime); 
       file6.open (File6, ios::in);
       if (file6.is_open()) {
         while (!file6.eof()) {
           getline (file6,sensor);
           cout << "There is no motion as value = " << sensor << endl;
         }
         index = 0;
       } else {          
          cout << "Keep closing the door" << endl;
       }
     }
     //pp.SetSpeed(speed1, turnrate); 
     //msleep(runtime);
     turnrate = dtor(-184);
     //speed = 0.165;
     pp.SetSpeed(speed2, turnrate);    
     sp.Say("My work is done\n");
     //runtime = 500;
     msleep(runtime1);
     turnrate = dtor(0);
     pp.SetSpeed(speed1, turnrate);  
     //speed =0.300;
     pp.SetSpeed(speed, turnrate);  
     //runtime = 1000;
     msleep(runtime);
     pp.SetSpeed(speed1, turnrate); 
     msleep(runtime);
     turnrate = dtor(-90);
     speed3 = 0.08415;
     pp.SetSpeed(speed3, turnrate);    
     //runtime = 500;
     msleep(runtime1);
     turnrate = dtor(0);
     pp.SetSpeed(speed1, turnrate);   
     sp.Say("\n");
     
     //writing that fire is checked into a file to update the database
     outfile2.open (OutFile2, ios::out);
     if (outfile2.is_open()) {
    	 outfile2 << "motionmitigated" << endl;
     }

     file6.close();
     file5.close();
     file4.close();
     outfile2.close();
     std::remove("/home/anindita/Dropbox/Public/BeebleBox/checkmotion.txt");
     std::remove("/home/anindita/Dropbox/Public/BeebleBox/motiondata.txt");
     std::remove("/home/anindita/Dropbox/Public/BeebleBox/nomotiondata.txt");
   } else {
     cout << "File does not exist" << endl;
   }
  }
}
