package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;

import java.time.LocalDate;

public class ClientSupplierTest {

    private static final String    VALID_NAME      = "Dupont";
    private static final String    VALID_FIRSTNAME = "Jean";
    private static final String    VALID_EMAIL     = "jean.dupont@example.com";
    private static final String    VALID_PHONE     = "0123456789";
    private static final String    VALID_VAT       = "FR12345678901";
    private static final LocalDate VALID_DATE      = LocalDate.of(2020, 1, 15);
    private static final Address   VALID_ADDRESS   = new Address("10 Rue de la Paix", "Paris", "France");

    private ClientSupplier buildValid() throws DataValidationException {
        return new ClientSupplier(
            0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE
        );
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        ClientSupplier cs = buildValid();
        assertEquals(0,               cs.getId());
        assertEquals(VALID_NAME,      cs.getName());
        assertEquals(VALID_FIRSTNAME, cs.getFirstname());
        assertEquals(VALID_EMAIL,     cs.getEmail());
        assertEquals(VALID_PHONE,     cs.getPhoneNumber());
        assertEquals(VALID_ADDRESS,   cs.getAddress());
        assertTrue(cs.getIsClient());
        assertTrue(cs.getIsSupplier());
        assertFalse(cs.getIsUs());
        assertEquals(VALID_VAT,  cs.getVATNumber());
        assertEquals(VALID_DATE, cs.getBecameClientDate());
    }

    @Test
    public void basicCreationTestClientOnly() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            1, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            null, true, false, false, null, VALID_DATE
        );
        assertTrue(cs.getIsClient());
        assertFalse(cs.getIsSupplier());
        assertEquals("client", cs.getType());
    }

    @Test
    public void basicCreationTestSupplierOnly() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            2, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, false, true, false, VALID_VAT, null
        );
        assertFalse(cs.getIsClient());
        assertTrue(cs.getIsSupplier());
        assertEquals("supplier", cs.getType());
    }

    @Test
    public void basicCreationTestIsUs() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            3, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, true, false, true, null, VALID_DATE
        );
        assertTrue(cs.getIsUs());
        assertEquals("us", cs.getType());
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        ClientSupplier cs1 = buildValid();
        ClientSupplier cs2 = buildValid();
        assertEquals(cs1, cs2, "Two identical ClientSuppliers should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        ClientSupplier cs1 = buildValid();
        ClientSupplier cs2 = new ClientSupplier(
            0, "Martin", VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE
        );
        assertNotEquals(cs1, cs2, "ClientSuppliers with different names should not be equal");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        ClientSupplier cs = buildValid();
        String result = cs.toString();
        assertTrue(result.contains("id=0"),                          "toString should contain id=0");
        assertTrue(result.contains("name='Dupont'"),                 "toString should contain name='Dupont'");
        assertTrue(result.contains("firstname='Jean'"),              "toString should contain firstname='Jean'");
        assertTrue(result.contains("email='jean.dupont@example.com'"), "toString should contain the email");
        assertTrue(result.contains("phoneNumber='0123456789'"),      "toString should contain the phone number");
        assertTrue(result.contains("isClient=true"),                 "toString should contain isClient=true");
        assertTrue(result.contains("isSupplier=true"),               "toString should contain isSupplier=true");
        assertTrue(result.contains("VATNumber='FR12345678901'"),     "toString should contain the VAT number");
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(-1, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "", VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, null, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void nullFirstnameWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, true, false, false, null, VALID_DATE)
        );
    }

    @Test
    public void emptyFirstnameWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, "", VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, true, false, false, null, VALID_DATE)
        );
    }

    @Test
    public void nullFirstnameWhenNotClientOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, false, true, false, VALID_VAT, null
        );
        assertNull(cs.getFirstname());
    }

    @Test
    public void nullEmailThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, VALID_FIRSTNAME, null, VALID_PHONE,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void invalidEmailThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, VALID_FIRSTNAME, "not-an-email", VALID_PHONE,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void nullPhoneThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, null,
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void nonDigitPhoneThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, "012-345-6789",
                VALID_ADDRESS, true, true, false, VALID_VAT, VALID_DATE)
        );
    }

    @Test
    public void nullAddressWhenSupplierThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
                null, false, true, false, VALID_VAT, null)
        );
    }

    @Test
    public void nullAddressWhenNotSupplierOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            null, true, false, false, null, VALID_DATE
        );
        assertNull(cs.getAddress());
    }

    @Test
    public void nullVATWhenSupplierThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, false, true, false, null, null)
        );
    }

    @Test
    public void invalidVATFormatThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
                VALID_ADDRESS, false, true, false, "12INVALID", null)
        );
    }

    @Test
    public void validVATFormatOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, false, true, false, "BE0123456789", null
        );
        assertEquals("BE0123456789", cs.getVATNumber());
    }

    @Test
    public void nullDateWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
                null, true, false, false, null, null)
        );
    }

    @Test
    public void nullDateWhenNotClientOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, false, true, false, VALID_VAT, null
        );
        assertNull(cs.getBecameClientDate());
    }

    @Test
    public void isUsTrueWithBothFalseThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, VALID_NAME, null, VALID_EMAIL, VALID_PHONE,
                null, false, false, true, null, null)
        );
    }

    @Test
    public void getTypeClientAndSupplier() throws DataValidationException {
        assertEquals("client and supplier", buildValid().getType());
    }

    @Test
    public void getTypeUs() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, VALID_NAME, VALID_FIRSTNAME, VALID_EMAIL, VALID_PHONE,
            VALID_ADDRESS, true, false, true, null, VALID_DATE
        );
        assertEquals("us", cs.getType());
    }
}