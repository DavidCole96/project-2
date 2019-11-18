import java.io.*;  
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerThread{

	public static void main(String[] args) throws IOException{
		/*
		if (args.length != 1){
			try{
			throw new IllegalAccessException("Parameter(s): <Port>");
				}catch(IllegalAccessException e){}
								}
		int servePort = Integer.parseInt(args[0]);
	*/
	ServerSocket servSocket = new ServerSocket(6666);

	Logger logger = Logger.getLogger("practical");

	while(true){
		Socket clntSock = servSocket.accept();
		Thread thread = new Thread(new serverProtocol(clntSock,logger));
		thread.start();
		logger.info("Created and started Thread " + thread.getName());
	}


	}

}