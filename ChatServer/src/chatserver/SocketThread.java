package chatserver;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JOptionPane;

class SocketThread implements Runnable{
    private Socket s = null;
    private Scanner in = null;
    private PrintWriter out = null;
    private String inMessage = null;
    private String outMessage = null;
    private boolean exit = true;
    private ArrayList<Socket> listSocket = null;
    private final ChatServer serverApp;
    
    SocketThread(Socket s, ChatServer serverApp) {
       this.s = s;
       this.serverApp = serverApp;
    }
    
    @Override
    public void run(){
        try{
            String socketname = s.getInetAddress().toString();
            serverApp.setOutputText(" < + Client " + socketname + " was connected > ");
            ListSocket.addSocketToList(s);
            in = new Scanner(s.getInputStream());
            
            serverApp.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}

                @Override
                public void windowClosing(WindowEvent e) {
                    try{
                        listSocket = ListSocket.getListSocket();
                        for(Socket socket : listSocket){
                            if(!socket.equals(s)){
                                out = new PrintWriter(socket.getOutputStream());
                                out.println("=== Server was shutdown ===");
                                out.flush();
                            }
                        }
                    }
                    catch(Exception ex){
                        System.out.println(ex);
                    }
                }
                
                @Override
                public void windowClosed(WindowEvent e) {}

                @Override
                public void windowIconified(WindowEvent e) {}

                @Override
                public void windowDeiconified(WindowEvent e) {}

                @Override
                public void windowActivated(WindowEvent e) {}

                @Override
                public void windowDeactivated(WindowEvent e) {}
            });
            
            while (exit){
                inMessage = in.nextLine();
                listSocket = ListSocket.getListSocket();
                for(Socket socket : listSocket){
                    if(!socket.equals(s)){
                        out = new PrintWriter(socket.getOutputStream());
                        out.println(inMessage);
                        out.flush();
                    }
                }
                if(inMessage.contains("was disconnected")){
                    exit = false;
                }
            }
            ListSocket.removeSocketWithList(s);
            serverApp.setOutputText(" < - Client " + socketname + " was disconnected > ");
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    
    }
}
