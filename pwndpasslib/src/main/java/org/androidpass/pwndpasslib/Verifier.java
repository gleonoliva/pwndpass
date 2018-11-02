package org.androidpass.pwndpasslib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

public class Verifier {

    private static final String API_URL = "https://api.pwnedpasswords.com/range/";


    public boolean verify(String password) {

        try {
            String passwordHash = computeSha1(password);
            String hashPrefix = passwordHash.substring(0, 5);
            URL url = new URL(API_URL + hashPrefix);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "PwndPass-For-Android");

            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String response = readResponse(in);

            boolean breached = response.contains(passwordHash.substring(5));
            return breached;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String computeSha1(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readResponse(InputStream in) throws IOException {
        byte[] buffer = new byte[1 << 10];
        int bytesRead = 0;
        StringBuilder sb = new StringBuilder();
        while((bytesRead = in.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, bytesRead));
        }

        return sb.toString();
    }
}
