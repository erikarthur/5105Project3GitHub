package org.erikar;

public class Main {

    public static String hostname = null;
    public static int hostport = 1234;
    public static int sendport = 5000;
    public static String shareDir = null;

    public static void main(String[] args) {
	// write your code here
        parseCmdLineArgs(args);
        Client c = new Client(hostname, hostport, sendport, shareDir);
        c.run();
    }

    public static void parseCmdLineArgs (String[] args) {
        int argCnt = args.length;

        if (argCnt == 0)
        {
            System.out.println("Arguments needed\nUsage:\n\n\thost:evm1 port:1234 sendPort:5000\n");
        }

        String[] runContext;

        for (String s:args) {
            if (s.startsWith("host"))
            {
                runContext = s.split(":");
                hostname = runContext[1];
            }
            if (s.startsWith("port"))
            {
                runContext = s.split(":");
                hostport = Integer.valueOf(runContext[1]);
            }
            if (s.startsWith("send"))
            {
                runContext = s.split(":");
                sendport = Integer.valueOf(runContext[1]);
            }
            if (s.startsWith("share"))
            {
                runContext = s.split(":");
                shareDir = runContext[1];
            }

        }
        return;
    }
}
