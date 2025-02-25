package org.example;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.DataSource;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;


public class ej2 {
    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("src/main/resources/configuracion.properties"));
            //servidor SMTP
            props.put(props.getProperty("mailHost"), props.getProperty("smtp"));

            //identificación requerida
            props.put(props.getProperty("mailAuth"), "true");

            //Para transmisión segura a través de TLS
            props.put(props.getProperty("mailStarttls"), "true"); //Para conectar de manera segura al servidor SMTP
            props.put(props.getProperty("mailPort"), props.getProperty("smtpPort")); //El puerto SMTP seguro de Google


            String cuentaUsuario = props.getProperty("cuentaUsuario");
            String password = props.getProperty("password");

            //abre una nueva sesión contra el servidor basada en:
            //el usuario, la contraseña y las propiedades especificadas
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(cuentaUsuario, password);
                        }
                    });

            Scanner sc = new Scanner(System.in);
            int opc = -1;
            do {
                try {
                    System.out.println("1. Lista emails no leidos");
                    System.out.println("2. Enviar email con copia oculta");
                    System.out.println("3. Salir");
                    System.out.print("Introduce una opción: ");
                    opc = sc.nextInt();
                    sc.nextLine(); // Limpiar el buffer

                } catch (Exception e) {
                    System.out.println("Entrada no válida. Debes introducir un número.");
                    sc.nextLine(); // Limpiar buffer para evitar bucle infinito
                }

                switch (opc) {
                    case 1:
                        mostrarCorreoNoLeido(session, props, cuentaUsuario, password);
                        break;
                    case 2:
                        enviarCorreoConBCC(sc, session, cuentaUsuario);

                        break;
                    case 3:
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                        break;
                }
            }while (opc != 3);

        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }


    }

    private static void enviarCorreoConBCC(Scanner sc, Session session, String cuentaUsuario) throws MessagingException {
        System.out.println("Introduce el Gmail destinatario: ");
        String mailDestinatario = sc.nextLine();
        System.out.println("Introduce el asunto del correo: ");
        String asunto = sc.nextLine();
        System.out.println("Introduce el cuerpo del mensaje del correo: ");
        String cuerpo = sc.nextLine();
        System.out.println("Introduce el archivo adjunto: ");
        String archivo = sc.nextLine();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(cuentaUsuario));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestinatario));
        // copia oculta a nosotros mismos
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(cuentaUsuario));
        message.setSubject(asunto);
        // Multipart para mensaje con varias partes
        Multipart multipart = new MimeMultipart();

        // Bodypart para el archivo adjunto
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(archivo);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(archivo);
        // segundo Bodypart para cuerpo del mensaje porque el primero lo machaca
        BodyPart messageBodyPart2 = new MimeBodyPart();
        messageBodyPart2.setText(cuerpo);

        // Añadimos los bodyparts al multipart
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(messageBodyPart2);

        // Añadimos el multipart al mensaje y lo mandamos
        message.setContent(multipart);
        Transport.send(message);
    }

    private static void mostrarCorreoNoLeido(Session session, Properties props, String cuentaUsuario, String password) throws MessagingException {
        Store store = session.getStore("imaps");
        store.connect(props.getProperty("imap"), cuentaUsuario, password);
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);
        Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        for (Message message : messages) {
            System.out.println("--------------------------------------------");
            System.out.println("De: " + Arrays.toString(message.getFrom()));
            System.out.println("Asunto: " + message.getSubject());
            System.out.println("Fecha de envío: " + message.getSentDate());
        }
        System.out.println("--------------------------------------------");
    }
}
