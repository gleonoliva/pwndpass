package org.androidpass.pwndpasslib;

import java.io.Serializable;
import java.util.HashMap;

public class Trie implements Serializable {

    private class TrieNode  implements Serializable{
        char character;
        boolean isFinal;
        HashMap<Character, TrieNode> children;

        public TrieNode(char character) {
            this.character = (char) character;
            this.isFinal = false;
            this.children = new HashMap<>();
        }
    }

    TrieNode root;

    public Trie() {
        root = new TrieNode((char) 0);
    }

    public void insert(String word) {
        char[] wordChars = word.toCharArray();
        TrieNode ptr = root;
        for (int i = 0; i < wordChars.length; i++) {
            char theChar = wordChars[i];
            TrieNode child = ptr.children.get(theChar);
            if (child == null) {
                child = new TrieNode(theChar);
                ptr.children.put(theChar, child);
            }
            ptr = child;

            if (i == wordChars.length - 1) {
                // Last character in word
                ptr.isFinal = true;
            }
        }
    }

    public boolean contains(String word) {
        char[] wordChars = word.toCharArray();
        TrieNode ptr = root;
        for (int i = 0; i < wordChars.length; i++) {
            char theChar = wordChars[i];
            TrieNode child = ptr.children.get(theChar);
            if (child == null) {
                break;
            }
            ptr = child;

            if (i == wordChars.length - 1) {
                return ptr.isFinal;
            }
        }

        return false;
    }
}
