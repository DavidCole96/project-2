import java.io.*;
import java.io.File;
import java.nio.file.Files;  
import java.net.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class serverProtocol implements Runnable{
    private Socket clntSock;
    private Logger logger;

    public serverProtocol(Socket clntSock, Logger logger){
        this.clntSock = clntSock;
        this.logger = logger;
    }

    public static void handleMsg(Socket clntSock, Logger logger) throws IOException {
      
		ServerSocket ss = new ServerSocket(6666);
        while (true){
            //clntSock = ss.accept();
         DataInputStream in  = new DataInputStream(clntSock.getInputStream());
         DataOutputStream out = new DataOutputStream(clntSock.getOutputStream());

            SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
            FileHandler fh = new FileHandler("/mnt/c/Users/David/Documents/MastersY1/networks/project2/server/log.txt");
            SimpleFormatter sformatter = new SimpleFormatter();
            fh.setFormatter(sformatter);
            logger.addHandler(fh);
            logger.info("Client " + clientAddress);
            System.out.println("Handling client at" + clientAddress);

        //Read the client value into the following String
        String clientString = in.readUTF();
        logger.info("Client Request: " + clientString);
        String[] serverHashes = new String[30];



        if(clientString.equals("list") || clientString.equals("diff") || clientString.equals("pull")){
            
            File songDirectory = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/server/songs");//Users/David/Documents/MastersY1/networks/project2/server/songs
            System.out.println(songDirectory);
            String[] songDir = songDirectory.list();
            int numSongs = 0;
            for (String string2 : songDir){
                System.out.println("Song: " +string2);
                numSongs++;
            }

            /*int numSongs =0;
            for(int i =0; i<5;i++){
                if(songDir[i] != null){
                    numSongs++;
                }
            }*/
            String numSong = Integer.toString(numSongs);
            System.out.println("Sending number of songs: "+numSongs );
            out.writeUTF(numSong);
            
            for(String string1 : songDir){
                out.writeUTF(string1);
            }
            int j =0;
            for(String string : songDir){
                
               File song = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/server/songs/"+string);
               System.out.println("###"+song);
               byte[] bytes =  Files.readAllBytes(new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/server/songs/"+string).toPath());
               
               try{
               MessageDigest md = MessageDigest.getInstance("SHA-256");
               md.update(bytes);
               byte[] finalized = md.digest();
               String toSend = "";

                for(byte b:finalized){
                    toSend += String.format("%02x", b);
                    }
                System.out.println("Hash being sent: "+toSend);
                
                serverHashes[j] = toSend;
                j = j+1;
                out.writeUTF(toSend);
                
            }catch(NoSuchAlgorithmException a){}
            
            }
        
        if (clientString.equals("diff") || clientString.equals("pull")) {
            String[] missingHashes = new String[30];
            System.out.println("Waiting for number of missing hashes");

            //String numberHashes = in.readUTF();
            int numbHashes = in.read();//numberHashes
            int BUFSIZE = 4096;
            System.out.println("Number of hashes received: "+numbHashes);
            for (int k=0;k<numbHashes ;k++ ) {
                missingHashes[k] = in.readUTF();
                System.out.println("Missing Hash Received Locating file:"+missingHashes[k]);
            }
            for(int f=0;f<numbHashes;f++){
                for (int q = 0;q<numSongs;q++ ) {
                    System.out.println("clienthash: "+missingHashes[f]);
                    System.out.println("ServerHash: "+serverHashes[q]);
                if (missingHashes[f].equals(serverHashes[q])) {
                    //SEND FILE HERE songDir[k]

                    File toSend = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/server/songs/"+songDir[q]);
                    byte[] songBytes = Files.readAllBytes(toSend.toPath());
 

                    System.out.println("Sending Message Length: "+ songBytes.length);
                    out.writeInt(songBytes.length);

                    System.out.println("Sending Song bytes: " + songBytes);
                    out.write(songBytes);
                    q=numSongs;
                    clntSock.close();
                    fh.close();
                }
            }}
            }
        }
        fh.close();



        if(clientString.equals("leave")){
            try{
            clntSock.close();
            fh.close();
            } catch(IOException e){}
        }
        /*catch (NoSuchAlgorithmException e){
            System.err.println("some error");
        }*/
    }

}
    public void run(){
        try{ 
            handleMsg(clntSock, logger);
        }catch(IOException e){}
    }
}
