package obligarcorp;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Sender {

    public static void javaKeyStore(){

        String ksName = "/Users/johnmichaelobligar/keykeeper.jks";
        char[] pwd = "keykeeper".toCharArray();

        try{
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
            signatureFile(privkey, pubkey);

            //System.out.println(pubkey);

            ksfis.close();

        }catch (KeyStoreException ex){
            System.err.println("Key Store: "+ex);

        }  catch ( FileNotFoundException ex){
            System.err.println("File Stream: "+ex);

        }catch (NoSuchAlgorithmException ex){
            System.err.println("Load: "+ex);

        }catch (CertificateException ex){
            System.err.println("Load 1: "+ex);

        }catch (IOException e){
            System.err.println("Load 2: "+e);

        }catch (UnrecoverableKeyException e){
            System.err.println("Private Key: "+e);
        }
    }

    public static void signatureFile(PrivateKey priv, PublicKey pub){

        try {

            File file = new File("/Users/johnmichaelobligar/Documents/ITE1806-Security/test.txt");

                Signature dsa = Signature.getInstance("SHA1withRSA");
                dsa.initSign(priv);

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

                /* save the public key into file */
                byte[] key = pub.getEncoded();
                FileOutputStream keyfos = new FileOutputStream("jmpk");
                keyfos.write(key);
                keyfos.close();

                //Method that writes the file with signature into a file that is ready to send.
                //writeToFile(realSig);

            }else{
                System.err.println("File does not exist.. Please Check the File Path!");
            }

            }catch(NoSuchAlgorithmException e){
                System.err.println("Error in line: Signature dsa = Signature.getInstance(\"SHA1withthRSA\"): " + e);

            }catch(InvalidKeyException e){
                System.err.println("initSign error: " + e);
            }catch(FileNotFoundException e){
                System.err.println("FileInputStream error: " + e);
            }catch(IOException e){
                System.err.println("While loop bufin.available() error: " + e);
            }catch(SignatureException e){
                System.err.println("dsa.update() error: " + e);
            }
    }


    public static void writeToZipFile( String path, ZipOutputStream zipStream)throws Exception{


         System.out.println("Writing file : '" + path + "' to zip file");

             File aFile = new File(path);

             FileInputStream fis = new FileInputStream(aFile);

             ZipEntry zipSigned = new ZipEntry(path);
             zipStream.putNextEntry(zipSigned);

             byte[] bytes = new byte[1024];
             int length;

             while ((length = fis.read(bytes)) >= 0) {

                 zipStream.write(bytes, 0, length);
             }
             zipStream.closeEntry();

             fis.close();

    }

    public static void main(String[] args){

        javaKeyStore();

        File


    }
}
