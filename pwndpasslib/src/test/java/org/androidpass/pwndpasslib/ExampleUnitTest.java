package org.androidpass.pwndpasslib;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testWithPassword() {
        Verifier v = new Verifier();
        assertEquals(v.verify("password"), Verifier.Validity.BREACHED);
    }

    @Test
    public void testAscendingSequence() {
        Verifier v = new Verifier();
        assertTrue(v.verify("qwpeoriuabc=--") == Verifier.Validity.SEQUENTIAL_CHARACTERS);
    }

    @Test
    public void testDescendingSequence() {
        Verifier v = new Verifier();
        assertEquals(v.verify("=4321{}-0asdiotn"), Verifier.Validity.SEQUENTIAL_CHARACTERS);
    }
}