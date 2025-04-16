package org.apache.commons.mail;

import static org.junit.Assert.*;
import java.util.*;
import javax.mail.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {

    private MockEmail email;

    @Before
    public void setUp() {
        email = new MockEmail(); // Concrete subclass of abstract Email
    }

    @After
    public void tearDown() {
        email = null;
    }

    // ───────────────────────────────────────────────
    // TESTS FOR addBcc(String... emails)
    // ───────────────────────────────────────────────

    // Valid input: should add 2 BCC addresses
    @Test
    public void testAddBcc_Valid() throws Exception {
        email.addBcc("a@test.com", "b@test.com");
        assertEquals(2, email.getBccAddresses().size());
    }

    // Null input: should throw EmailException (branch test)
    @Test(expected = EmailException.class)
    public void testAddBcc_NullArray() throws Exception {
        email.addBcc((String[]) null);
    }

    // Empty input: should throw EmailException (branch test)
    @Test(expected = EmailException.class)
    public void testAddBcc_EmptyArray() throws Exception {
        email.addBcc();
    }

    // ───────────────────────────────────────────────
    // TESTS FOR addCc(String email)
    // ───────────────────────────────────────────────

    // Valid input: should add one CC address
    @Test
    public void testAddCc_Valid() throws Exception {
        email.addCc("cc@test.com");
        assertEquals(1, email.getCcAddresses().size());
    }

    // Invalid email: should throw EmailException (input validation)
    @Test(expected = EmailException.class)
    public void testAddCc_InvalidEmail() throws Exception {
        email.addCc("bad-email");
    }

    // ───────────────────────────────────────────────
    // TESTS FOR addHeader(String name, String value)
    // ───────────────────────────────────────────────

    // Valid input: header should be added
    @Test
    public void testAddHeader_Valid() {
        email.addHeader("X-Test-Header", "value");
        assertTrue(email.headers.containsKey("X-Test-Header"));
    }

    // Null key: should throw IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeader_NullKey() {
        email.addHeader(null, "value");
    }

    // Empty value: should throw IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeader_EmptyValue() {
        email.addHeader("X-Test", "");
    }

    // ───────────────────────────────────────────────
    // TESTS FOR addReplyTo(String email, String name)
    // ───────────────────────────────────────────────

    // Valid input: reply-to address should be added
    @Test
    public void testAddReplyTo_Valid() throws Exception {
        email.addReplyTo("reply@test.com", "ReplyName");
        assertEquals(1, email.getReplyToAddresses().size());
    }

    // Invalid email: should throw EmailException
    @Test(expected = EmailException.class)
    public void testAddReplyTo_Invalid() throws Exception {
        email.addReplyTo("not-an-email", "Bad");
    }

    // ───────────────────────────────────────────────
    // TESTS FOR buildMimeMessage()
    // ───────────────────────────────────────────────

    // Missing from address: should throw EmailException (required fields not set)
    @Test
    public void testBuildMimeMessage_MissingFrom() throws Exception {
        email.setSubject("Test Subject");
        email.addTo("to@test.com");
        email.setMsg("This is the body.");
        try {
            email.buildMimeMessage();
            fail("Should have thrown EmailException due to missing from");
        } catch (EmailException e) {
            assertTrue(e.getMessage().contains("From address required"));
        }
    }

    // All fields valid: should build MimeMessage successfully
    @Test
    public void testBuildMimeMessage_Success() throws Exception {
        email.setFrom("from@test.com");
        email.setSubject("Hi");
        email.setMsg("Hello world");
        email.addTo("to@test.com");
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage());
    }

    // ───────────────────────────────────────────────
    // TESTS FOR getHostName()
    // ───────────────────────────────────────────────

    // Returns hostName when session is not initialized
    @Test
    public void testGetHostName_FromField() {
        email = new MockEmail(); // ensure fresh instance
        email.setHostName("smtp.test.com");
        assertEquals("smtp.test.com", email.getHostName());
    }

    // ───────────────────────────────────────────────
    // TESTS FOR getMailSession()
    // ───────────────────────────────────────────────

    // Hostname not set: should throw EmailException
    @Test
    public void testGetMailSession_NoHostname() {
        try {
            email.getMailSession();
            fail("Expected EmailException");
        } catch (EmailException e) {
            assertTrue(e.getMessage().contains("Cannot find valid hostname"));
        }
    }

    // Hostname is set: should return Session successfully
    @Test
    public void testGetMailSession_Valid() throws Exception {
        email = new MockEmail(); 
        email.setHostName("smtp.test.com");
        assertNotNull(email.getMailSession());
    }

    // ───────────────────────────────────────────────
    // TESTS FOR getSentDate()
    // ───────────────────────────────────────────────

    // Test both default sent date and custom date
    @Test
    public void testGetSentDate_DefaultAndCustom() {
        Date now = new Date();
        assertTrue(email.getSentDate().getTime() <= System.currentTimeMillis());

        email.setSentDate(now);
        assertEquals(now, email.getSentDate());
    }

    // ───────────────────────────────────────────────
    // TESTS FOR getSocketConnectionTimeout()
    // ───────────────────────────────────────────────

    // Returns default timeout value from constants
    @Test
    public void testGetSocketConnectionTimeout() {
        int timeout = email.getSocketConnectionTimeout();
        assertEquals(60000, timeout); // default from EmailConstants
    }

    // ───────────────────────────────────────────────
    // TESTS FOR setFrom(String email)
    // ───────────────────────────────────────────────

    // Valid input: should set from address
    @Test
    public void testSetFrom_Valid() throws Exception {
        email.setFrom("from@test.com");
        assertEquals("from@test.com", email.getFromAddress().getAddress());
    }

    // Invalid input: should throw EmailException
    @Test
    public void testSetFrom_Invalid() {
        try {
            email.setFrom("invalid@@");
            fail("Expected EmailException");
        } catch (EmailException e) {
            assertTrue(e.getMessage().contains("@"));
        }
    }

}


