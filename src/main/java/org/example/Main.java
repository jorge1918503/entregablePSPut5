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
            // Conexion y login con el FTPServer
            if (conexionLoginFTP(ftpClient, server, port, user, pass)) return;
            do {

                // Opciones del menú
                opc = opcionesMenu(sc);

                switch (opc) {
                    case 1:
                        mostrarDirectorio(ftpClient);
                        break;
                    case 2:
                        entrarDirectorio(sc, ftpClient);
                        break;
                    case 3:
                        subirDirectorioPadre(ftpClient);
                        break;
                    case 4:
                        subirFichero(sc, ftpClient);
                        break;
                    case 5:
                        borrarFichero(sc, ftpClient);
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

    private static void borrarFichero(Scanner sc, FTPClient ftpClient) throws IOException {
        // Borrar fichero
        System.out.println("Introduce un fichero: ");
        String ficheroBorrar = sc.nextLine();
        ftpClient.deleteFile(ficheroBorrar);
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
    }

    private static void subirFichero(Scanner sc, FTPClient ftpClient) throws IOException {
        // Subir fichero
        System.out.println("Introduce un fichero: ");
        String fichero = sc.nextLine();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        FileInputStream fis = new FileInputStream(fichero);

        ftpClient.storeFile(fichero, fis);
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
    }

    private static void subirDirectorioPadre(FTPClient ftpClient) throws IOException {
        // Subimos al directorio padre
        ftpClient.changeToParentDirectory();
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
    }

    private static void entrarDirectorio(Scanner sc, FTPClient ftpClient) throws IOException {
        System.out.println("Introduce el directorio: ");
        String directorio = sc.nextLine();
        // Si no consigue cambiar al directorio lo informa
        if (!ftpClient.changeWorkingDirectory(directorio)) System.out.println("El directorio no existe.");
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
    }

    private static void mostrarDirectorio(FTPClient ftpClient) throws IOException {
        // Listamos el directorio en un array de FTPFiles y los imprimimos
        FTPFile[] files = ftpClient.listFiles();
        for (FTPFile file : files) {
            System.out.println(file);
        }
        System.out.println("Respuesta del servidor: " + ftpClient.getReplyString());
    }

    private static int opcionesMenu(Scanner sc) {
        int opc;
        System.out.println("1. Listar el directorio actual");
        System.out.println("2. Entrar a un directorio");
        System.out.println("3. Subir al directorio padre");
        System.out.println("4. Subir un fichero");
        System.out.println("5. Borrar un fichero");
        System.out.println("6. Salir");
        System.out.print("Introduce una opción: ");
        opc = sc.nextInt();
        sc.nextLine();
        return opc;
    }

    private static boolean conexionLoginFTP(FTPClient ftpClient, String server, int port, String user, String pass) throws IOException {

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