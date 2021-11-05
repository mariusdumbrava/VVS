package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.StringTokenizer;

import java.io.File;
public class Server implements Runnable {
    static ServerSocket serverSocket;
    static int serverPort = 0;
    public int conectionClient;
    static final File WEB_ROOT = new File("html_files");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    static final String FILE_MAINTENANCE = "maintenance.html";
    static int statusServerInt = 0;
    static int MIN_PORT_NUMBER = 0;
    static int MAX_PORT_NUMBER = 9999;

    private Socket clientSocket;

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public  boolean setPort(int portNr) {
        try (var ignored = new ServerSocket(portNr)) {
            serverPort=portNr;
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean acceptServerPort() {
        try{

            if(serverPort <= 1024 || serverPort >= 9999) {
                throw new Exception();
            }else {
                serverSocket=new ServerSocket(serverPort);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void setStateServer(int state) {
        statusServerInt=state;
    }

    public void setConnectionOK(int x) {
        this.conectionClient = x;
    }


    public void listenForClients(){
        while(true)
        {
            Server connection;
            try {
                connection = new Server();
                connection.setClientSocket(serverSocket.accept());
                setConnectionOK(1);
                Thread thread = new Thread(connection);
                thread.start();
            }catch (IOException e) {
                setConnectionOK(0);
            }
        }
    }


    public byte[] readFileData(File file,int fileLength) throws IOException
    {
        FileInputStream fileIn=null;
        byte[] fileData= new byte[fileLength];
        try{
            fileIn=new FileInputStream(file);
            fileIn.read(fileData);
        }finally{
            if (fileIn!=null)
                fileIn.close();
        }
        return fileData;
    }


    public void writeFileData(String nameOfFileRequested,PrintWriter hearderOut,OutputStream bodyOut,String headerText)
    {
        File file= new File(WEB_ROOT,nameOfFileRequested);
        int fileLength=(int)file.length();
        String content=getExtFile(file);

        String inputString = "No file";
        Charset charset = Charset.forName("ASCII");
        byte[] fileData =  inputString.getBytes(charset);
        try {
            fileData = readFileData(file,fileLength);
        } catch (IOException e) {
            file= new File(WEB_ROOT,FILE_NOT_FOUND);
            fileLength=(int)file.length();
            content=getExtFile(file);
            try {
                fileData = readFileData(file,fileLength);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("-------Cannot read this file : "+file);
        }
        //send Header
        if (hearderOut!=null) {
            hearderOut.println(headerText);
            hearderOut.println("main.Server : Java HTTP server");
            hearderOut.println("Date: " + new Date());
            hearderOut.println("Content-type: " + content);
            hearderOut.println("Content-length: " + fileLength);
            hearderOut.println();//blamk line between head and content very important
            hearderOut.flush();
        }
        //send data
        try {
            bodyOut.write(fileData,0,fileLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bodyOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileNotFound(PrintWriter out,OutputStream dataOut,String fileRequested) throws IOException
    {
        writeFileData(FILE_NOT_FOUND,out,dataOut,"HTTP/1.1 404 Not found");
    }


    @Override
    public void run() {


        BufferedReader in = null;
        PrintWriter out=null;
        BufferedOutputStream dataOut=null;
        String fileRequested=null;


        try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out=new PrintWriter(clientSocket.getOutputStream());
            dataOut=new BufferedOutputStream(clientSocket.getOutputStream());
            //get first line of request
            String input = in.readLine();
            StringTokenizer parse= new StringTokenizer(input);
            String method= parse.nextToken().toUpperCase();
            //get file request
            fileRequested=URLDecoder.decode(parse.nextToken().toLowerCase(), "UTF-8");

            System.out.println("+ <<--REQUEST :  "+input);

            if(!method.equals("GET") && !method.equals("HEAD"))
            {
                System.out.println("501 Not implemented: "+method);
                writeFileData(METHOD_NOT_SUPPORTED,out,dataOut,"HTTP/1.1 501 Not Implemented");

            }else{
                if(method.equals("GET"))
                {	if(statusServerInt==1){
                    if(fileRequested.endsWith("/"))
                    {
                        fileRequested+=DEFAULT_FILE;
                    }
                    //return content
                    writeFileData(fileRequested,out,dataOut,"HTTP/1.1 200 OK");
                }else if(statusServerInt==2) {
                    fileRequested=FILE_MAINTENANCE;
                    writeFileData(fileRequested,out,dataOut,"HTTP/1.1 200 OK");
                }else if(statusServerInt==3){
                    // close  server
                    in.close();
                    out.close();
                    dataOut.close();
                    clientSocket.close();
                }
                }
            }
        }catch(FileNotFoundException fnfe)
        {
            try{
                fileNotFound(out,dataOut,fileRequested);
                System.out.println("-------NOT FOUND: "+fileRequested+" not found! -----------------------");
            }catch(IOException ioe){
                System.err.println("+ File : 404.html not found  "+ioe.getMessage());
            }
        }catch(IOException ioe){
            System.err.println("+ Server error : "+ioe);
        }finally{
            try{
                in.close();
                out.close();
                dataOut.close();
                clientSocket.close();
            }catch(Exception e)
            {
                System.err.println("+ Error closing stream:"+e.getMessage());
            }
        }
    }

    public String getExtFile(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "none"; // empty extension
        }
        String extString=name.substring(lastIndexOf);
        switch(extString)
        {
            case ".html":
                return "text/html";
            default:
                return "text/html";
        }
    }

    public int getStateServer() {
        return statusServerInt;
    }

    public Socket getClientSocket() {
        return this.clientSocket;
    }
}
