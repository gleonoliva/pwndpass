package org.androidpass.pwndpasslib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Verifier {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    private static Trie dictionaryTrie;

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
        } else if (containsRepetitiveCharacters(password)) {
            result.getResult(Validity.REPETITIVE_CHARACTERS);
        } else if (containsSequentialCharacters(password)) {
            result.getResult(Validity.SEQUENTIAL_CHARACTERS);
        } else if (isDictionaryWord(password)) {
            result.getResult(Validity.DICTIONARY_WORD);
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

    private Trie buildFromStream(InputStream input) {
        Trie t = new Trie();

        if (input != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                ArrayList<String> allLines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }

                for (String word : allLines) {
                    t.insert(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return t;
    }

    private Trie buildFromZippedStream(InputStream input) {
        if (input != null) {
            try {
                GZIPInputStream zip = new GZIPInputStream(input, 65536);
                Trie loadedDictionary = buildFromStream(zip);
                return loadedDictionary;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Trie();
    }

    private Trie loadDictionary() {
        if (dictionaryTrie == null) {
            // Hack because Android requires a heavy-weight context to access resources
            long startTime = System.nanoTime();
            Class<? extends Trie> aClass = Trie.class;
            InputStream in = aClass.getResourceAsStream("/res/raw/dict.gz");
            dictionaryTrie = buildFromZippedStream(in);
            Log.d("TIME_TO_LOAD_DICTIONARY", Long.toString(System.nanoTime() - startTime));
        }

        return dictionaryTrie;
    }


}
