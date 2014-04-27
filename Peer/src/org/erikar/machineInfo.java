package org.erikar;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erik on 4/25/14.
 */
public class machineInfo {

    private InetAddress _ip;
    private int _port;
    private List<String> _fileList = new ArrayList<String>();

    public InetAddress get_ip() { return _ip;}
    public void set_ip (InetAddress i) { this._ip = i;}

    public int get_port() { return _port;}
    public void set_port(int p) { this._port = p;}

    public List<String> get_fileList() { return _fileList;}
    public void set_fileList(List<String> fl) { _fileList = fl;}

    public machineInfo(InetAddress i, int p) {
        this._ip = i;
        this._port = p;
    }

    public machineInfo()
    {
        this._port = Integer.MAX_VALUE;
        this._ip = null;
    }

}