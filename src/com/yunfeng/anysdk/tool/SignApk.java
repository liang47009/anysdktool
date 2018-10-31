package com.yunfeng.anysdk.tool;

import com.sun.xml.internal.ws.api.ResourceLoader;
import jdk.internal.util.xml.impl.Input;
import sun.misc.BASE64Encoder;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.jar.*;
import java.util.regex.Pattern;

public class SignApk {
    private static final String CERT_SF_NAME = "META-INF/CERT.SF";
    private static final String CERT_RSA_NAME = "META-INF/CERT.RSA";
    private static Pattern stripPattern = Pattern.compile("^META-INF/(.*)[.](SF|RSA|DSA)$");

    public static void main(String[] args) {
        String keyStorePassword;
        if (args.length != 6) {
            System.err.println("Usage: signapk file.{keystore} keystore_password key_entry key_password\ninput.jar \noutput.jar");
            StringBuilder b = new StringBuilder();
            String[] var5 = args;
            int var4 = args.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                keyStorePassword = var5[var3];
                b.append(keyStorePassword);
                b.append('\n');
            }

            System.err.println(b.toString());
            System.exit(2);
        }

        String pathToKeyStore = args[0];
        keyStorePassword = args[1];
        String keyEntry = args[2];
        String keyPassword = args[3];
        String pathToInputApk = args[4];
        String pathToOutputApk = args[5];
        JarFile inputJar = null;
        JarOutputStream outputJar = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            File keyFile = new File(pathToKeyStore);
            if (keyFile.exists()) {
                FileInputStream fis = new FileInputStream(keyFile);
                keyStore.load(fis, keyStorePassword.toCharArray());
                fis.close();
            } else {
                InputStream is = SignApk.class.getClassLoader().getResourceAsStream(pathToKeyStore);
                keyStore.load(is, keyStorePassword.toCharArray());
                is.close();
            }

            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyEntry, new KeyStore.PasswordProtection(keyPassword.toCharArray()));
            PrivateKey mPrivateKey = null;
            X509Certificate mCertificate = null;
            if (entry == null) {
                throw new Exception("Null Entry Exception");
            }

            mPrivateKey = entry.getPrivateKey();
            mCertificate = (X509Certificate) entry.getCertificate();
            long timestamp = mCertificate.getNotBefore().getTime() + 3600000L;
            inputJar = new JarFile(new File(pathToInputApk), false);
            outputJar = new JarOutputStream(new FileOutputStream(pathToOutputApk));
            outputJar.setLevel(9);
            Manifest manifest = addDigestsToManifest(inputJar);
            JarEntry je = new JarEntry("META-INF/MANIFEST.MF");
            je.setTime(timestamp);
            outputJar.putNextEntry(je);
            manifest.write(outputJar);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(mPrivateKey);
            je = new JarEntry("META-INF/CERT.SF");
            je.setTime(timestamp);
            outputJar.putNextEntry(je);
            writeSignatureFile(manifest, new SignApk.SignatureOutputStream(outputJar, signature));
            je = new JarEntry("META-INF/CERT.RSA");
            je.setTime(timestamp);
            outputJar.putNextEntry(je);
            writeSignatureBlock(signature, mCertificate, outputJar);
            copyFiles(manifest, inputJar, outputJar, timestamp);
        } catch (Exception var27) {
            var27.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (inputJar != null) {
                    inputJar.close();
                }

                if (outputJar != null) {
                    outputJar.close();
                }
            } catch (IOException var26) {
                var26.printStackTrace();
                System.exit(1);
            }

        }

    }

    private static String readPassword(File keyFile) {
        System.out.print("Enter password for " + keyFile + " (password will not be hidden): ");
        System.out.flush();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        try {
            return stdin.readLine();
        } catch (IOException var3) {
            return null;
        }
    }

    private static KeySpec decryptPrivateKey(byte[] encryptedPrivateKey, File keyFile) throws GeneralSecurityException {
        EncryptedPrivateKeyInfo epkInfo;
        try {
            epkInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKey);
        } catch (IOException var9) {
            return null;
        }

        char[] password = readPassword(keyFile).toCharArray();
        SecretKeyFactory skFactory = SecretKeyFactory.getInstance(epkInfo.getAlgName());
        Key key = skFactory.generateSecret(new PBEKeySpec(password));
        Cipher cipher = Cipher.getInstance(epkInfo.getAlgName());
        cipher.init(2, key, epkInfo.getAlgParameters());

        try {
            return epkInfo.getKeySpec(cipher);
        } catch (InvalidKeySpecException var8) {
            System.err.println("signapk: Password for " + keyFile + " may be bad.");
            throw var8;
        }
    }

    private static Manifest addDigestsToManifest(JarFile jar) throws IOException, GeneralSecurityException {
        Manifest input = jar.getManifest();
        Manifest output = new Manifest();
        Attributes main = output.getMainAttributes();
        if (input != null) {
            main.putAll(input.getMainAttributes());
        } else {
            main.putValue("Manifest-Version", "1.0");
            main.putValue("Created-By", "1.0 (Android SignApk)");
        }

        BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] buffer = new byte[4096];
        TreeMap<String, JarEntry> byName = new TreeMap();
        Enumeration e = jar.entries();

        while (e.hasMoreElements()) {
            JarEntry entry = (JarEntry) e.nextElement();
            byName.put(entry.getName(), entry);
        }

        Iterator var15 = byName.values().iterator();

        while (true) {
            String name;
            JarEntry entry;
            do {
                do {
                    do {
                        do {
                            do {
                                if (!var15.hasNext()) {
                                    return output;
                                }

                                entry = (JarEntry) var15.next();
                                name = entry.getName();
                            } while (entry.isDirectory());
                        } while (name.equals("META-INF/MANIFEST.MF"));
                    } while (name.equals("META-INF/CERT.SF"));
                } while (name.equals("META-INF/CERT.RSA"));
            } while (stripPattern != null && stripPattern.matcher(name).matches());

            InputStream data = jar.getInputStream(entry);

            int num;
            while ((num = data.read(buffer)) > 0) {
                md.update(buffer, 0, num);
            }

            Attributes attr = null;
            if (input != null) {
                attr = input.getAttributes(name);
            }

            attr = attr != null ? new Attributes(attr) : new Attributes();
            attr.putValue("SHA1-Digest", base64.encode(md.digest()));
            output.getEntries().put(name, attr);
        }
    }

    private static void writeSignatureFile(Manifest manifest, OutputStream out) throws IOException, GeneralSecurityException {
        Manifest sf = new Manifest();
        Attributes main = sf.getMainAttributes();
        main.putValue("Signature-Version", "1.0");
        main.putValue("Created-By", "1.0 (Android SignApk)");
        BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        PrintStream print = new PrintStream(new DigestOutputStream(new ByteArrayOutputStream(), md), true, "UTF-8");
        manifest.write(print);
        print.flush();
        main.putValue("SHA1-Digest-Manifest", base64.encode(md.digest()));
        Map<String, Attributes> entries = manifest.getEntries();
        Iterator var9 = entries.entrySet().iterator();

        while (var9.hasNext()) {
            Map.Entry<String, Attributes> entry = (Map.Entry) var9.next();
            print.print("Name: " + (String) entry.getKey() + "\r\n");
            Iterator var11 = ((Attributes) entry.getValue()).entrySet().iterator();

            while (var11.hasNext()) {
                Map.Entry<Object, Object> att = (Map.Entry) var11.next();
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }

            print.print("\r\n");
            print.flush();
            Attributes sfAttr = new Attributes();
            sfAttr.putValue("SHA1-Digest", base64.encode(md.digest()));
            sf.getEntries().put((String) entry.getKey(), sfAttr);
        }

        sf.write(out);
    }

    private static void writeSignatureBlock(Signature signature, X509Certificate publicKey, OutputStream out) throws IOException, GeneralSecurityException {
        SignerInfo signerInfo = new SignerInfo(new X500Name(publicKey.getIssuerX500Principal().getName()), publicKey.getSerialNumber(), AlgorithmId.get("SHA1"), AlgorithmId.get("RSA"), signature.sign());
        PKCS7 pkcs7 = new PKCS7(new AlgorithmId[]{AlgorithmId.get("SHA1")}, new ContentInfo(ContentInfo.DATA_OID, (DerValue) null), new X509Certificate[]{publicKey}, new SignerInfo[]{signerInfo});
        pkcs7.encodeSignedData(out);
    }

    private static void copyFiles(Manifest manifest, JarFile in, JarOutputStream out, long timestamp) throws IOException {
        byte[] buffer = new byte[4096];
        Map<String, Attributes> entries = manifest.getEntries();
        List<String> names = new ArrayList(entries.keySet());
        Collections.sort(names);
        Iterator var10 = names.iterator();

        while (var10.hasNext()) {
            String name = (String) var10.next();
            JarEntry inEntry = in.getJarEntry(name);
            JarEntry outEntry = null;
            if (inEntry.getMethod() == 0) {
                outEntry = new JarEntry(inEntry);
            } else {
                outEntry = new JarEntry(name);
            }

            outEntry.setTime(timestamp);
            out.putNextEntry(outEntry);
            InputStream data = in.getInputStream(inEntry);

            int num;
            while ((num = data.read(buffer)) > 0) {
                out.write(buffer, 0, num);
            }

            out.flush();
        }

    }

    private static class SignatureOutputStream extends FilterOutputStream {
        private Signature mSignature;

        public SignatureOutputStream(OutputStream out, Signature sig) {
            super(out);
            this.mSignature = sig;
        }

        public void write(int b) throws IOException {
            try {
                this.mSignature.update((byte) b);
            } catch (SignatureException var3) {
                throw new IOException("SignatureException: " + var3);
            }

            super.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            try {
                this.mSignature.update(b, off, len);
            } catch (SignatureException var5) {
                throw new IOException("SignatureException: " + var5);
            }
            super.write(b, off, len);
        }
    }
}

