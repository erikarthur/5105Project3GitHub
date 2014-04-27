package org.erikar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

/**
 * Created by erik on 4/23/14.
 */
public class Client
{
    private String hostname= "evm1";
    private String shareDir = null;
    private int port=1234;
    private int sendPort = 5000;
    private InetAddress host;
    private DatagramSocket socket;
    DatagramPacket packet;
    Type stringList = new TypeToken<List<String>>() {}.getType();
    Type machinelistType = new TypeToken<List<machineInfo>>() {}.getType();
    Type fileListType = new TypeToken<List<fileList>>() {}.getType();


    public Client(String h, int p, int sp, String sd)
    {
        this.hostname = h;
        this.port = p;
        this.sendPort = sp;
        this.shareDir = sd;
    }

    public Client()
    {
        this.hostname = "localhost";
        this.port = 1234;
        this.sendPort = 5000;
    }

    //sha1 function from http://www.javacreed.com/how-to-generate-sha1-hash-value-of-file/
    //directly copied for my SHA1 implementation.
    public static String sha1(String file) throws NoSuchAlgorithmException, IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            final byte[] buffer = new byte[1024];
            for (int read = 0; (read = is.read(buffer)) != -1;) {
                messageDigest.update(buffer, 0, read);
            }
        }

        // Convert the byte to hex format
        try (Formatter formatter = new Formatter()) {
            for (final byte b : messageDigest.digest()) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

    public List<fileList> processFiles(String directory) {

        List<fileList> fileInfoList = new ArrayList<fileList>();
        String fileName, fileNameSHA1;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileName = path.getFileName().toString();
                fileNameSHA1 = sha1(fileName);
                fileInfoList.add(new fileList(fileName, fileNameSHA1));
            }
        } catch (IOException | NoSuchAlgorithmException ex) { ex.printStackTrace();}
        return fileInfoList;
    }

    public void sendFileList()
    {
        try
        {
            List<fileList> f = processFiles(shareDir);  //bugbug - expand to add SHA1 hashes as well.

            Gson gson = new Gson();
            String fileListStr = gson.toJson(f, fileListType);

            InetAddress host = InetAddress.getByName(hostname);
            DatagramSocket socket1 = new DatagramSocket(sendPort);

            ByteBuffer bb = ByteBuffer.allocate(8196);

            int msgType = 0;
            int overAllLength = 0;

            bb.putInt(overAllLength);    //just adding as a placeholder.  Real length added at end.
            bb.putInt(msgType);

            bb.putInt(hostname.getBytes(Charset.forName("UTF-8")).length);
            bb.put(hostname.getBytes(Charset.forName("UTF-8")));

            bb.putInt(sendPort);
            bb.putInt(fileListStr.length());
            bb.put(fileListStr.getBytes(Charset.forName("UTF-8")));

            int bbLength = bb.position();
            bb.putInt(0,bbLength);

            bb.position(0);
            byte[] buffer = new byte[bb.remaining()];
            bb.get(buffer);

//            int oLen, hLen, p, jLen, mType;
//            String hStr, jStr;
//
//            bb.position(0);
//            oLen = bb.getInt();
//            mType = bb.getInt();
//
//            hLen = bb.getInt();
//            byte[] hBytes = new byte[hLen];
//            bb.get(hBytes, 0, hLen);
//            hStr = new String(hBytes, Charset.forName("UTF-8"));
//
//            p = bb.getInt();
//
//            jLen = bb.getInt();
//            byte[] jBytes = new byte[jLen];
//            bb.get(jBytes, 0, jLen);
//            jStr = new String(jBytes, Charset.forName("UTF-8"));
//            List<fileList> f1 = gson.fromJson(jStr, fileListType);

            DatagramPacket packet =new DatagramPacket (buffer, buffer.length, host, port);
            socket1.send (packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run()
    {
        try
        {
            sendFileList();
//            List<String> f = fileList("/home/erik/IdeaProjects/5105project3/fsDir");
//
//            Gson gson = new Gson();
//            String jsonStr = gson.toJson(f, stringList);
//
//            host = InetAddress.getByName(hostname);
//            socket = new DatagramSocket(null);
//
//            ByteBuffer bb = ByteBuffer.allocate(8196);
//
//            bb.putInt(jsonStr.length()).putInt(0).put(jsonStr.getBytes(Charset.forName("UTF-8")));
//            //int CmdLine = 1;
//            //bb.putInt(1);
//            bb.position(0);
//            byte[] buffer = new byte[bb.remaining()];
//            bb.get(buffer);
//
//            packet=new DatagramPacket (buffer, buffer.length, host, port);
//            socket.send (packet);
//
//            packet.setLength(100);
//
//            socket.receive (packet);
//            socket.close ();
//
//            byte[] data = packet.getData ();
//            //ByteBuffer byteBuffer = ByteBuffer.allocate(packet.length);
//            String v = new String( data, Charset.forName("UTF-8") );
//
//            //String time=new String(data);  // convert byte array data into string
//
//            System.out.println(v);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}