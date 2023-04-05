/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.serversocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author magi
 */
public class SeSocket {

    public static void main(String[] args) throws IOException {
        ServerSocket ss=new ServerSocket(1234);
        System.out.println("Server is listening on port 1234 ");
        while (!ss.isClosed()) {         
            System.out.println("Server is Waiting for client...  ");
            Socket s=ss.accept();
            System.out.println("new clinet client request recived "+s);
            new Thread(new ClientHandler(s)).start();
        }
    }
}
class  ClientHandler implements Runnable{
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    String Name;
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        while (true) {            
                    try {
                        Name=br.readLine();
                        if(validation()){
                            bw.write("valid");
                            bw.newLine();
                            bw.flush();
                            sendMessage("server : "+Name+" has join the chat", "all");
                            for(ClientHandler clientHandler:clientHandlers){
                                System.out.println(clientHandler.Name);
                                if(!clientHandler.Name.equals(Name)){
                                    bw.write(clientHandler.Name);
                                    bw.newLine();
                                }
                            }
                            bw.write("end");
                            bw.newLine();
                            bw.flush();
                            break;
                        }else{
                            bw.write("invalid");
                            bw.newLine();
                            bw.flush();
                            
                        }
                    } catch (Exception e) {
                    }
                }
    }
    boolean validation(){
        if(Name.toLowerCase().equals("server")||Name==null||Name.toLowerCase().equals("end")){
            System.out.println("server or null");
            return false;
        }else{
            for(ClientHandler clientHandler:clientHandlers){
                if(Name.toLowerCase().equals(clientHandler.Name.toLowerCase())){
                    System.out.println("similar name");
                    return false;
                }
            }
            clientHandlers.add(this);
            return true;
        }
    }
    @Override
    public void run() {
        while(socket.isConnected()) {
    	   try {
    		   String message,rname;
    		   message=br.readLine();
                   System.out.println("message");
                   System.out.println(message);
                   rname=br.readLine();
                   
                   System.out.println("rName");
                   System.out.println(rname);
    		   sendMessage(Name+": "+message,rname);
			} catch (IOException e) {
				// TODO: handle exception
			}
       }
    }
    
    void sendMessage(String message,String rName) throws IOException{
        if(rName.toLowerCase().equals("all")){
            for(ClientHandler clientHandler:clientHandlers){
                if(!clientHandler.Name.toLowerCase().equals(Name)){
                    System.out.println(clientHandler.Name+" "+this.Name);
                    clientHandler.bw.write("(G) "+message);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush();
                }
            }
        }else{
            for(ClientHandler clientHandler:clientHandlers){
                System.out.println("singl send");
                if(clientHandler.Name.toLowerCase().equals(rName.toLowerCase())){
                    clientHandler.bw.write("(p) "+message);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush();
                }
            }
                
        }
    }
    
}
