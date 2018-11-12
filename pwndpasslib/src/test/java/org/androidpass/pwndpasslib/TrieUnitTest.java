package org.androidpass.pwndpasslib;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static junit.framework.TestCase.assertTrue;

public class TrieUnitTest {


    private Trie buildFromFile(String filePath) {
        Trie t = new Trie();

        try {
            Files.lines(Paths.get(filePath), Charset.forName("UTF-8"))
                    .filter(line -> line.length() > 3 && !line.contains("'"))
                    .map(line -> line.trim().toLowerCase())
                    .forEach(word -> {
                        t.insert(word);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return t;
    }

    @Test
    public void trieInsertContainsWords() {
        Trie t = new Trie();

        t.insert("hola");
        t.insert("como");
        t.insert("estas");
        t.insert("estado");

        assertTrue(t.contains("hola"));
        assertTrue(t.contains("como"));
        assertTrue(t.contains("estas"));
        assertTrue(t.contains("estado"));
    }

    @Test
    public void trieInsertSerializeDeserializeContainsWords() throws IOException, ClassNotFoundException {
        Trie t = new Trie();

        t.insert("hola");
        t.insert("como");
        t.insert("estas");
        t.insert("estado");

        String fileName = "trie.dat";

        FileOutputStream fsout = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fsout);

        out.writeObject(t);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        Trie t2 = (Trie) in.readObject();

        assertTrue(t2.contains("hola"));
        assertTrue(t2.contains("como"));
        assertTrue(t2.contains("estas"));
        assertTrue(t2.contains("estado"));
    }

    @Test
    public void createDicitionaryTrie() throws IOException {
        String inputFile = "sampledata/dict_allcat_95.txt";
        String fileName = "compressed_dict.dat";

        Trie t = buildFromFile(inputFile);

        FileOutputStream fsout = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fsout);

        out.writeObject(t);
        out.close();
    }

    @Test
    public void createCompressedDictionaryTrie() throws IOException, ClassNotFoundException {
        String inputFile = "sampledata/dict_allcat_95.txt";
        String fileName = "compressed_dict.zip";

        Trie t = buildFromFile(inputFile);

        FileOutputStream fsout = new FileOutputStream(fileName);
        GZIPOutputStream zipout = new GZIPOutputStream(fsout);
        ObjectOutputStream out = new ObjectOutputStream(zipout);

        out.writeObject(t);
        out.close();

        FileInputStream inputStream = new FileInputStream(fileName);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ObjectInputStream in = new ObjectInputStream(gzipInputStream);

        Trie t2 = (Trie) in.readObject();

        assertTrue(t2.contains("Abbado".toLowerCase()));
        assertTrue(t2.contains("apple"));
        assertTrue(t2.contains("fossil"));
        assertTrue(t2.contains("gruesome"));
    }

    @Test
    public void createCompressedDictionary70Trie() throws IOException, ClassNotFoundException {
        String inputFile = "sampledata/dict_allcat_70.txt";
        String fileName = "compressed_dict70.zip";

        Trie t = buildFromFile(inputFile);

        FileOutputStream fsout = new FileOutputStream(fileName);
        GZIPOutputStream zipout = new GZIPOutputStream(fsout);
        ObjectOutputStream out = new ObjectOutputStream(zipout);

        out.writeObject(t);
        out.close();

        FileInputStream inputStream = new FileInputStream(fileName);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ObjectInputStream in = new ObjectInputStream(gzipInputStream);

        Trie t2 = (Trie) in.readObject();

        assertTrue(t2.contains("Addison".toLowerCase()));
        assertTrue(t2.contains("apple"));
        assertTrue(t2.contains("fossil"));
        assertTrue(t2.contains("gruesome"));
    }

    @Test
    public void createCompressedDictionaryTrie50() throws IOException, ClassNotFoundException {
        String inputFile = "sampledata/dict_allcat_50.txt";
        String fileName = "compressed_dict50.zip";

        Trie t = buildFromFile(inputFile);

        FileOutputStream fsout = new FileOutputStream(fileName);
        GZIPOutputStream zipout = new GZIPOutputStream(fsout);
        ObjectOutputStream out = new ObjectOutputStream(zipout);

        out.writeObject(t);
        out.close();

        FileInputStream inputStream = new FileInputStream(fileName);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ObjectInputStream in = new ObjectInputStream(gzipInputStream);

        Trie t2 = (Trie) in.readObject();

        assertTrue(t2.contains("Adela".toLowerCase()));
        assertTrue(t2.contains("apple"));
        assertTrue(t2.contains("fossil"));
        assertTrue(t2.contains("gruesome"));
    }

}
