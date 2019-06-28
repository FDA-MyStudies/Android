package com.harvard.securityModule;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Rajeesh on 3/30/2017.
 */

public class KeystoreEncryption extends Application {
    private static KeyStore mKeyStore;
    private static final String TAG = "FDAKeystoreApp";
    private static String mKeystoreValue = null;

    public static void keystoreInitilize(Context context, String mPasswordString) {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        refreshKeys();
        if (mKeystoreValue == null) {
            String alias = getRandomString();
            createNewKeys(context, alias);
            encryptString(context, alias, mPasswordString);
        }
    }

    // create random string
    private static String getRandomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(MAX_LENGTH);
        // sometime random val coming null so legth 7 hard-coded
        int randomLength = 7;
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    // get the stored keystore values
    private static void refreshKeys() {
        try {
            mKeystoreValue = mKeyStore.aliases().nextElement();
        } catch (Exception e) {
            mKeystoreValue = null;
        }
    }

    // first time create new keystore value
    private static void createNewKeys(Context context, String alias) {
        try {
            // Create new key if needed
            if (!mKeyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    // delete the keystore value
    public static void deleteKey() {
        try {
            refreshKeys();
            mKeyStore.deleteEntry(mKeystoreValue);
            refreshKeys();
        } catch (KeyStoreException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    private static void encryptString(Context context, String alias, String mPasswordString) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();


            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(mPasswordString.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            Log.e("rajeesh", "Encrypted text....." + Base64.encodeToString(vals, Base64.DEFAULT));
            SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS_NAME", context.MODE_PRIVATE).edit();
            editor.putString("userName", "" + Base64.encodeToString(vals, Base64.DEFAULT));
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void decryptString(Context context) {
        try {
            refreshKeys();
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(mKeystoreValue, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            SharedPreferences prefs = context.getSharedPreferences("MY_PREFS_NAME", context.MODE_PRIVATE);
            String cipherText = prefs.getString("userName", "");//"No name defined" is the default value.
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            Log.e("rajeesh", "Decrypted text....." + finalText);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}