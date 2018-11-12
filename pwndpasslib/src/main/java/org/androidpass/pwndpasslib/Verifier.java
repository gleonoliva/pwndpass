package org.androidpass.pwndpasslib;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Verifier {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    public enum Validity {
        VALID,
        TOO_SHORT,
        DICTIONARY_WORD,
        REPETITIVE_CHARACTERS,
        SEQUENTIAL_CHARACTERS,
        BREACHED
    }

    public interface ValidityResult {

        void getResult(Validity validity);

    }


    public void verify(String password, ValidityResult result) {
        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            result.getResult(Validity.TOO_SHORT);
        } else if (isDictionaryWord(password)) {
            result.getResult(Validity.DICTIONARY_WORD);
        } else if (containsRepetitiveCharacters(password)) {
            result.getResult(Validity.REPETITIVE_CHARACTERS);
        } else if (containsSequentialCharacters(password)) {
            result.getResult(Validity.SEQUENTIAL_CHARACTERS);
        } else {
            BreachedPasswordCheckerTask breachedTask = new BreachedPasswordCheckerTask(result);
            breachedTask.execute(password);
        }
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

        for(int i = 0; i <= chars.length - limit; i++) {
            char c = chars[i];
            boolean found = false;
            for (int j = 1; j < limit && i + j < chars.length; j++) {
                char d = (char) (ascending ? c + j : c - j);
                if (chars[i + j] != d || !Character.isLetterOrDigit(c)) {
                    found = false;
                    break;
                } else {
                    found = true;
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
        Pattern p = Pattern.compile("(\\w)+");
        Matcher m = p.matcher(password);

        Trie t = loadDictionary();

        while(m.find()) {
            String substring = m.group();

            if (t.contains(substring.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private Trie loadDictionary() {

        // Hack because Android requires a heavy-weight context to access resources
        Class<? extends Trie> aClass = Trie.class;
        InputStream in = aClass.getResourceAsStream("/res/raw/compressed_dict.zip");

        try {
            GZIPInputStream zip = new GZIPInputStream(in);
            ObjectInputStream objectInputStream = new ObjectInputStream(zip);

            Trie loadedDictionary = (Trie) objectInputStream.readObject();

            return loadedDictionary;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
