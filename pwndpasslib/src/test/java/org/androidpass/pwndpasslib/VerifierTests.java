package org.androidpass.pwndpasslib;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class VerifierTests {

    @Test
    public void testWithPassword() {
        Verifier v = new Verifier();
        v.verify("password", (validity) -> assertEquals(validity, Verifier.Validity.BREACHED));
    }

    @Test
    public void testAscendingSequence() {
        Verifier v = new Verifier();
        v.verify("qwpeoriuabc=--", (validity) -> assertEquals(validity, Verifier.Validity.SEQUENTIAL_CHARACTERS));
    }

    @Test
    public void testDescendingSequence() {
        Verifier v = new Verifier();
        v.verify("=4321{}-0asdiotn", validity -> assertEquals(validity, Verifier.Validity.SEQUENTIAL_CHARACTERS));
    }

    @Test
    public void testDictionaryWord() {
        Verifier v = new Verifier();
        v.verify("=-apple-=", validity -> { assertEquals(validity, Verifier.Validity.DICTIONARY_WORD); });
    }
}
