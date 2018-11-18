package org.androidpass.pwndpasslib;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class VerifierTests {

    @Test
    public void testDictionaryWord() {
        Verifier v = new Verifier();
        v.verify("=-apple-=", validity -> { assertEquals(validity, Verifier.Validity.DICTIONARY_WORD); });
    }

    @Test
    public void testWithPassword() {
        Verifier v = new Verifier();
        v.verify("password", (validity) -> assertEquals(validity, Verifier.Validity.BREACHED));
    }

}
