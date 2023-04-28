// IN2011 Computer Networks
// Coursework 2022/2023
//
// This is an example of how the src.DSTNode object can be tested.
// This file should work with your submission without any changes.
// You do not need to submit this file; it is just to help you test.

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DSTNodeCmdLine {

    public static void main(String[] args) {
	if (args.length != 4) {
	    System.err.println("Usage error!");
	    System.err.println("src.DSTStoreCmdLine ipAddress portNumber identifier startingNodeName");
	    return;
	} else {
	    InetAddress ipAddress;
	    try {
		ipAddress = InetAddress.getByName(args[0]);
	    } catch (UnknownHostException e) {
		System.err.println("Exception thrown while parsing IP address");
		System.err.println(e);
		e.printStackTrace(System.err);
		return;
	    }

	    int portNumber = Integer.parseInt(args[1]);
	    String identifier = args[2];
	    String startingNodeName = args[3];
	    
	    DSTNode node = new DSTNode(ipAddress, portNumber, identifier);
	    node.handleIncomingConnections(startingNodeName);
	    
	    return;
	}
    }
}
