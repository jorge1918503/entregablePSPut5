package org.example;

import org.apache.commons.net.ftp.FTPClient;

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

            // Conectar al servidor
            ftpClient.connect(server, port);
            System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());

            // Login
            if (ftpClient.login(user, pass)) {
                System.out.println("Autenticado en el servidor.");
                System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
            } else {
                System.out.println("Error en autenticación.");
                return;
            }

            // Entrar en modo pasivo
            ftpClient.enterLocalPassiveMode();
            System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());

            do {
                System.out.println("Introduce una opción: ");
                opc = sc.nextInt();
                switch (opc) {
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

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
}