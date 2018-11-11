package org.androidpass.pwndpasslib;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

public class BreachedPasswordCheckerTask extends AsyncTask<String, Void, Boolean> {

    private static final String API_URL = "https://api.pwnedpasswords.com/range/";

    Verifier.ValidityResult _result;

    public BreachedPasswordCheckerTask(Verifier.ValidityResult result) {
        _result = result;
    }

    @Override
    protected Boolean doInBackground(String... passwords) {
        String password = passwords[0];

        return hasPasswordBreached(password);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Verifier.Validity validity = result.booleanValue() ? Verifier.Validity.BREACHED : Verifier.Validity.VALID;
        _result.getResult(validity);
    }

    private boolean hasPasswordBreached(String password) {
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
