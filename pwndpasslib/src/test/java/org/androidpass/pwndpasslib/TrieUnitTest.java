package org.androidpass.pwndpasslib;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static junit.framework.TestCase.assertTrue;

public class TrieUnitTest {


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
        Trie t = new Trie();

        String inputFile = "sampledata/dict_allcat_95.txt";
        String fileName = "compressed_dict.dat";

        Scanner reader = new Scanner(new File(inputFile));

        while(reader.hasNext()) {
            String word = reader.next();
            if (word.length() > 3 && !word.contains("'")) {
                t.insert(word);
            }
        }

        FileOutputStream fsout = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fsout);

        out.writeObject(t);
        out.close();
    }

    @Test
    public void createCompressedDictionaryTrie() throws IOException, ClassNotFoundException {
        Trie t = new Trie();

        String inputFile = "sampledata/dict_allcat_95.txt";
        String fileName = "compressed_dict.zip";

        Scanner reader = new Scanner(new File(inputFile));

        while(reader.hasNext()) {
            String word = reader.next();
            if (word.length() > 3 && !word.contains("'")) {
                t.insert(word);
            }
        }


        FileOutputStream fsout = new FileOutputStream(fileName);
        GZIPOutputStream zipout = new GZIPOutputStream(fsout);
        ObjectOutputStream out = new ObjectOutputStream(zipout);

        out.writeObject(t);
        out.close();

        FileInputStream inputStream = new FileInputStream(fileName);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ObjectInputStream in = new ObjectInputStream(gzipInputStream);

        Trie t2 = (Trie) in.readObject();

        assertTrue(t2.contains("Abbado"));
    }

}
