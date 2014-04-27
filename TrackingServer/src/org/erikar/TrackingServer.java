package org.erikar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * Created by erik on 4/23/14.
 */

public class TrackingServer {

    HashMap<String, List<machineInfo>> fileStorage = new HashMap<String, List<machineInfo>>();
    public static final int DEFAULT_PORT = 1234;
    Type stringList = new TypeToken<List<String>>() {}.getType();

    public void run() {


   Thread netThread = new Thread(new NetworkCommunications());
        netThread.start();
//
//        Thread heartBeat = new Thread(new HeartBeat());
//        heartBeat.start();

    }

    public class NetworkCommunications implements Runnable
    {

        private DatagramSocket socket;
        private DatagramPacket packet;
        Type machinelistType = new TypeToken<List<machineInfo>>() {}.getType();
        Type fileListType = new TypeToken<List<fileList>>() {}.getType();
        Gson gson = new Gson();

        public void run() {
            try {

                List<machineInfo> f = new ArrayList<machineInfo>();
                //f.add(new fileInfo(InetAddress.getLocalHost(), 4500));

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try
            {
                socket = new DatagramSocket(DEFAULT_PORT);

                packet = new DatagramPacket(new byte[8000], 8000);

                while (true) {

                    socket.receive(packet);

                    // bb = ByteBuffer.allocate(packet.getLength());
                    ByteBuffer bb = ByteBuffer.wrap(packet.getData());
                    bb.position(0);

                    int datagramSize = bb.getInt();

                    if (datagramSize != 0) {   //bugbug add the size of an int.

                        int msgType = bb.getInt();
                        String payLoadStr = null;

                        switch (msgType) {
                            case 0:

                                int  hLen, p, jLen;
                                String hStr, jStr;

                                hLen = bb.getInt();
                                byte[] hBytes = new byte[hLen];
                                bb.get(hBytes, 0, hLen);
                                hStr = new String(hBytes, Charset.forName("UTF-8"));

                                p = bb.getInt();

                                jLen = bb.getInt();
                                byte[] jBytes = new byte[jLen];
                                bb.get(jBytes, 0, jLen);
                                jStr = new String(jBytes, Charset.forName("UTF-8"));
                                List<fileList> fileInfoList = gson.fromJson(jStr, fileListType);

                                Runnable r = new updateFileTimeStamps(fileInfoList, hStr, p );
                                new Thread(r).start();
                                int ii = 9;
                                break;

                            case 1:
                                String fileToSend = payLoadStr;   //just the filename to send
                                List<machineInfo> machineList = fileStorage.get(fileToSend);
                                String machineListStr = gson.toJson(machineList, machinelistType);

                                bb.clear();
                                bb.position(0);

                                byte[] outBuffer = machineListStr.getBytes(Charset.forName("UTF-8"));

                                bb.putInt(outBuffer.length);
                                bb.put(outBuffer,bb.arrayOffset(),outBuffer.length);

                                packet.setData(bb.array());
                                packet.setLength(bb.array().length);
                                socket.send(packet);

                                break;
                        }
                    }
                }

            }
            catch (IOException ie) {
                    ie.printStackTrace();
            }
        }

//        public void updateFileTimeStamps(List<String> fileList, InetAddress ip, int p) {
//            System.out.println("in the filelist");
//        }
    }
    public class sendFileList implements Runnable {
        private String fileName;
        private InetAddress ip;
        private int port;

        Type machinelistType = new TypeToken<List<machineInfo>>() {}.getType();
        Gson gson = new Gson();

        public sendFileList(String f, InetAddress i, int p) {
            this.fileName = f;
            this.ip = i;
            this.port = p;
        }

        public void run() {
            if (fileStorage.containsKey(fileName))
            {

                //
            }
        }


    }
    public class updateFileTimeStamps implements Runnable {
        public List<fileList> fileListList;
        public InetAddress ip;
        public int port;

        //constructor
        public updateFileTimeStamps(List<fileList> fl, String hostString, int p)
        {
            try {
                this.fileListList = fl;
                this.ip = InetAddress.getByName(hostString);
                this.port = p;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            int i = 99;
            machineInfo foundMachineInfo = new machineInfo();

            for (fileList fileName: fileListList)
            {
                int iiii = 999;
                if (fileStorage.containsKey(fileName.get_filename()))
                {
                    List<machineInfo> machineList = fileStorage.get(fileName.get_filename());
                    boolean foundMachine = false;
                    for (machineInfo machine: machineList)
                    {
                        if ((machine.get_ip() == this.ip) && (machine.get_port() == this.port)) {
                            foundMachine = true;
                            foundMachineInfo = machine;
                            break;
                        }
                    }

                    if (foundMachine) {
                        foundMachineInfo.set_ts(new Date());
                    }
                    else {
                        machineInfo newMachineFile = new machineInfo(ip, port);
                        newMachineFile.set_ts(new Date());
                        machineList.add(newMachineFile);
                    }
                }
                else
                {
                    //new file
                    machineInfo newMachineFile = new machineInfo(ip, port);
                    newMachineFile.set_ts(new Date());

                    List<machineInfo> mList = new ArrayList<machineInfo>();
                    mList.add(newMachineFile);

                    fileStorage.put(fileName.get_filename(), mList);
                }
            }
        }
    }
//    public class HeartBeat implements Runnable {
//
//
//        public void run() {
//            Timer timer = new Timer("Printer");
//            MyTask t = new MyTask();
//            timer.schedule(t, 0, 2000);
//        }
//
//    }
//    class MyTask extends TimerTask {
//        //times member represent calling times.
//        private int times = 0;
//
//
//        public void run() {
//              times++;
//              System.out.println("I'm alive...");
//            }
//
//    }

}