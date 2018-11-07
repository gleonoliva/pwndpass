package org.androidpass.pwndpasslib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Verifier {

    private static final String API_URL = "https://api.pwnedpasswords.com/range/";

    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    public enum Validity {
        VALID,
        TOO_SHORT,
        DICTIONARY_WORD,
        REPETITIVE_CHARACTERS,
        SEQUENTIAL_CHARACTERS,
        BREACHED
    }


    public Validity verify(String password) {
        if (password.length() <= MINIMUM_PASSWORD_LENGTH) {
            return Validity.TOO_SHORT;
        }

        if (isDictionaryWord(password)) {
            return Validity.DICTIONARY_WORD;
        }

        if (containsRepetitiveCharacters(password)) {
            return Validity.REPETITIVE_CHARACTERS;
        }

        if (containsSequentialCharacters(password)) {
            return Validity.SEQUENTIAL_CHARACTERS;
        }

        if (hasPasswordBreached(password)) {
            return Validity.BREACHED;
        }

        return Validity.VALID;
    }

    private boolean containsSequentialCharacters(String password) {
        Pattern p = Pattern.compile("(\\w)+");
        Matcher m = p.matcher(password);

        while(m.find()) {
            String substring = m.group();
            if (isSequential(substring, true) || isSequential(substring, false)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSequential(String substring, boolean ascending) {
        char[] chars = substring.toCharArray();
        int limit = 3;

        for(int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean found = true;
            for (int j = 1; j < limit && i + j < chars.length; j++) {
                char d = (char) (ascending ? c + j : c - j);
                if (chars[i + j] != d || !Character.isLetterOrDigit(c)) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return true;
            }
        }

        return false;
    }

    private boolean containsRepetitiveCharacters(String password) {
        Pattern p = Pattern.compile("(.)\\1\\1");
        Matcher m = p.matcher(password);

        if (m.find()) {
            return true;
        }
        return false;
    }

    private boolean isDictionaryWord(String password) {

        return false;
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
