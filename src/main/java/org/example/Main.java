package org.example;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();

        properties.load(new FileInputStream("src/main/resources/configuracion.properties"));
        String server = properties.getProperty("server");
        int port = Integer.parseInt(properties.getProperty("port"));
        String user = properties.getProperty("user");
        String pass = properties.getProperty("pass");

        FTPClient ftpClient = new FTPClient();
        Scanner sc = new Scanner(System.in);
        int opc = -1;
        try {

            if (conexionLoginFTP(ftpClient, server, port, user, pass)) return;

            do {

                System.out.println("1. Listar el directorio actual");
                System.out.println("2. Entrar a un directorio");
                System.out.println("3. Subir al directorio padre");
                System.out.println("4. Subir un fichero");
                System.out.println("5. Borrar un fichero");
                System.out.println("6. Salir");
                System.out.print("Introduce una opción: ");
                opc = sc.nextInt();
                sc.nextLine();
                switch (opc) {
                    case 1:
                        FTPFile[] files = ftpClient.listFiles();
                        for (FTPFile file : files) {
                            System.out.println((file.isDirectory() ? "[D] " : "[F] ") + file.getName());
                        }
                        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
                        break;
                    case 2:
                        System.out.println("Introduce el directorio: ");
                        String directorio = sc.nextLine();
                        if (!ftpClient.changeWorkingDirectory(directorio)) System.out.println("El directorio no existe.");
                        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
                        break;
                    case 3:
                        ftpClient.changeToParentDirectory();
                        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
                        break;
                    case 4:
                        System.out.println("Introduce un fichero: ");
                        String fichero = sc.nextLine();
                        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                        break;
                    case 5:

                        break;
                    case 6:
                        System.out.println("Saliendo del programa...");
                        break;
                }
            }while (opc != 6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private static boolean conexionLoginFTP(FTPClient ftpClient, String server, int port, String user, String pass) throws IOException {

        System.out.println("Server: " + server);
        System.out.println("Port: " + port);
        System.out.println("User: " + user);
        System.out.println("Pass: " + pass);


        // Conectar al servidor
        System.out.println("Conectando al servidor...");
        ftpClient.connect(server, port);
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());

        // Entrar en modo pasivo
        System.out.println("Entrando en modo pasivo...");
        ftpClient.enterLocalPassiveMode();
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());

        // Login
        System.out.println("Autenticando en el servidor...");
        if (ftpClient.login(user, pass)) {
            System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
        } else {
            System.out.println("Error en autenticación.");
            return true;
        }


        return false;
    }
}