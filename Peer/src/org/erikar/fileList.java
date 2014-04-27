package org.erikar;

/**
 * Created by erik on 4/26/14.
 */
public class fileList {
    public String _filename;
    public String _fileSHA1;

    public String get_filename() { return _filename; }
    public void set_filename (String f) { _filename = f;}

    public String get_fileSHA1() { return _fileSHA1;}
    public void set_fileSHA1 (String s) { _fileSHA1 = s; }

    public fileList(String f, String s) {
        this._filename = f;
        this._fileSHA1 = s;
    }

    public fileList() {
        this._filename = null;
        this._fileSHA1 = null;
    }
}
