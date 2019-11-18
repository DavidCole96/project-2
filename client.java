import java.io.*;  
import java.net.*;  
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;  
import java.security.*;

public class client {
    public static void main(String[] args) throws IOException {

        
        //Create the client socket
        int servPort = 6666;
        Socket socket = new Socket("127.0.0.1", servPort); //matrix.csc.villanova.edu
        System.out.println("connecting to server...");

        //Create stream processing IO objects

		DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        
        //Send the command line arg to the server via the socket output stream
        Scanner scan = new Scanner(System.in);

        System.out.println("Hello welcome to your myMusic User Interface");
        System.out.println("your available commands are \"list\" \"diff\" \"pull\" and \"leave\"");
        System.out.println("please enter your command now");
        System.out.println("begin to read input stream");
        String cmd = scan.next();
        scan.close();
        String[] cmds = {"list","diff","pull","leave"};
        List<String> list = Arrays.asList(cmds);
        if(!list.contains(cmd)){
            System.out.println("Invalid command");
                                }
        else{
        out.writeUTF(cmd);
            }
        //Read the result from the socket input stream

        if(cmd.equals("leave")){socket.close();}
        if(cmd.equals("list") || cmd.equals("diff") || cmd.equals("pull")){
            String numSongs = in.readUTF();
            System.out.println("Number of songs:" + numSongs);
            String[] songHashes = new String[30];
            String[] songNames = new String[30];
            int numSong = Integer.parseInt(numSongs);
                for (int i = 0;i<numSong;i++) {

                    songNames[i] = in.readUTF();
                    System.out.println("Song names from server: "+songNames[i]);
                                             }
                for (int i = 0;i<numSong;i++) {

                    songHashes[i] = in.readUTF();
                    System.out.println("song hashes from server: "+songHashes[i]);
                                                 }
            if(cmd.equals("list")){
                for (int z = 0;z<numSong ;z++ ) {
                    System.out.println("Song #"+(z+1)+" "+songNames[z]);
                                                 }
                                     }
            else{//DIFF BEGINS HERE
                //DOUBLE FOR LOOP MAY GAWD  
            File clientSong = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/client/songs");
            System.out.println("Client songs: "+clientSong);
            String[] clientSongDir = clientSong.list();
            int numClientSongs =0;
            for (String string2 : clientSongDir){
                System.out.println("Song: " +string2);
                numClientSongs++;
            }
            String[] clientSongHash = new String[5];
            int m = 0;
             for(String string : clientSongDir){
                
                File song = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/client/songs/"+string);
                try{

                byte[] bytes =  Files.readAllBytes(new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/client/songs/"+string).toPath());
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(bytes);
                byte[] finalized = md.digest();
                String toSend = "";
                
                for(byte b:finalized){
                    toSend += String.format("%02x", b);
                    }
                
                clientSongHash[m] = toSend;
                 
                 m = m+1;
                }catch(NoSuchAlgorithmException a){}
            }
                //HASHES GATHERED 
                String[] diffSongHash = new String[30];
                boolean found = false;
                int missing = 0;
                
                ArrayList<String> alSongHashes = new ArrayList<String>(Arrays.asList(songHashes));
                ArrayList<String> alClientSongHashes = new ArrayList<String>(Arrays.asList(clientSongHash));
                for(String string5 : alClientSongHashes){
                    System.out.println("client song hashes: " + string5);
                }
                for(String string4 : alSongHashes){
                    if(!alClientSongHashes.contains(string4)){
                        
                        diffSongHash[missing] = string4;
                        missing = missing+1;
                        System.out.println("A missing hash: "+ string4);
                    }
                }


                System.out.println("sending number of hash files that will be sent:" + missing);
                String send = Integer.toString(missing);
          

                  out.write(missing);
                for(int s = 0; s < missing;s++){
                    out.writeUTF(diffSongHash[s]);
                    System.out.println("Missing Hash Codes sent: "+ diffSongHash[s]);
                }
                if(cmd.equals("diff")){socket.close();}
                else{

                //out.write(-1);
                System.out.println("now we are waiting for the server to send us the byte code for our files");
                int len = in.readInt();
                System.out.println("here is the byte length sent by the server: "+len);
                 if(len > 0){
                    byte[]songBytes = new byte[len];
                    in.readFully(songBytes,0,songBytes.length);
                
                System.out.println("Writing bytes to a file");
                File newFile = new File("/mnt/c/Users/David/Documents/MastersY1/networks/project2/client/songs/newSong.MP3");
                OutputStream os = new FileOutputStream(newFile);
                os.write(songBytes);
                System.out.println(newFile);
                socket.close();
            }}
                                                            }//here is diff end 
                                                        }//here is diff and list end
                                                    }

        
        //Close IO streams and socket
        //socket.close();
	}
      