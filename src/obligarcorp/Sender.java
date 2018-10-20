package obligarcorp;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Sender {

    public static void getKeysFromKeystore()throws Exception{

        String ksName = "/Users/johnmichaelobligar/keykeeper.jks";
        char[] pwd = "keykeeper".toCharArray();

            //Opening the keystore
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream ksfis = new FileInputStream(ksName);
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
            ks.load(ksbufin, pwd);

            //Get private key
            PrivateKey privkey = (PrivateKey) ks.getKey("Javadoo", pwd);

            //Get Certificate and public key
            Certificate cert = ks.getCertificate("Javadoo");

            PublicKey pubkey = cert.getPublicKey();

            //signing the file.
            signatureFile(privkey);
            ksfis.close();

        /* save the public key into file */
        byte[] key = pubkey.getEncoded();
        FileOutputStream keyfos = new FileOutputStream("jmpk");
        keyfos.write(key);
        keyfos.close();
    }

    //There is no
    public static void signatureFile(PrivateKey privkey) throws Exception{


            File file = new File("/Users/johnmichaelobligar/Documents/ITE1806-Security/test.txt");

                Signature dsa = Signature.getInstance("SHA1withRSA");
                dsa.initSign(privkey);

            if (file.exists()) {

                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bufin = new BufferedInputStream(fis);
                byte[] buffer = new byte[1024];

                int len;

                while (bufin.available() != 0) {
                    len = bufin.read(buffer);
                    dsa.update(buffer, 0, len);
                }

                bufin.close();

                byte[] realSig = dsa.sign();

                /* save the signature into file */
                FileOutputStream sigfos = new FileOutputStream("Sigfile");
                sigfos.write(realSig);

                sigfos.close();

            }else{
                System.err.println("File does not exist.. Please Check the File Path!");
            }

    }


    public static void writeToZipFile( String signedfile, String document, String zipFile)throws Exception{


         System.out.println("Writing file : '" + signedfile +" and "+ document + "' to zip file");

         String[] srcFiles = {signedfile, document};


        byte[] bytes = new byte[1024];

        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);


             for (int i = 0;i<srcFiles.length; i++) {

                 File srcFile = new File(srcFiles[i]);
                 FileInputStream fis = new FileInputStream(srcFile);

                 /*Writing into file.*/

                 zos.putNextEntry(new ZipEntry(srcFile.getName()));

                 int length;

                 while ((length = fis.read(bytes)) >= 0) {
                     zos.write(bytes, 0, length);
                 }

                 zos.closeEntry();

                 //Close InputStream
                 fis.close();
             }

             //Close ZipOutputStream
             zos.close();


    }

    public static void main(String[] args)throws Exception{

        getKeysFromKeystore();

        String signedfile = "Sigfile";
        String document = "/Users/johnmichaelobligar/Documents/ITE1806-Security/test.txt";

        //Naming the zipfile:
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a zipfile name: ");
        String zipFileName = sc.nextLine()+".zip";

        writeToZipFile(signedfile,document,zipFileName);

    }
}
