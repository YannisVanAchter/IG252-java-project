package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;

import java.time.LocalDate;

public class ClientSupplierTest {
    private ClientSupplier clientSupplier;
    
    @BeforeEach
    public void setup() {
        try {
            clientSupplier = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
            );
        } catch (DataValidationException e) {
            fail("Failed to initialize test data");
        }
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertEquals(0,               cs.getId());
        assertEquals("Dupont",      cs.getName());
        assertEquals("Jean",      cs.getFirstname());
        assertEquals("jean.dupont@example.com",     cs.getEmail());
        assertEquals("0123456789",     cs.getPhoneNumber());
        assertEquals(clientSupplier.getAddress(),   cs.getAddress());
        assertTrue(cs.getIsClient());
        assertTrue(cs.getIsSupplier());
        assertFalse(cs.getIsUs());
        assertEquals(clientSupplier.getVATNumber(),  cs.getVATNumber());
        assertEquals(clientSupplier.getBecameClientDate(), cs.getBecameClientDate());
    }

    @Test
    public void basicCreationTestClientOnly() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            1, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, false, false, null, LocalDate.of(2020, 1, 15)
        );
        assertTrue(cs.getIsClient());
        assertFalse(cs.getIsSupplier());
        assertEquals("client", cs.getType());
    }

    @Test
    public void basicCreationTestSupplierOnly() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            2, "Dupont", null, "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, "FR12345678901", null
        );
        assertFalse(cs.getIsClient());
        assertTrue(cs.getIsSupplier());
        assertEquals("supplier", cs.getType());
    }

    @Test
    public void basicCreationTestIsUs() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            3, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, false, true, null, LocalDate.of(2020, 1, 15)
        );
        assertTrue(cs.getIsUs());
        assertEquals("us", cs.getType());
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        ClientSupplier cs1 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        ClientSupplier cs2 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertEquals(cs1, cs2, "Two identical ClientSuppliers should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        ClientSupplier cs1 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        ClientSupplier cs2 = new ClientSupplier(
            0, "Martin", "Jean", "jean.martin@example.com", "0123456789",
            new Address("20 Avenue des Champs-Élysées", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertNotEquals(cs1, cs2, "ClientSuppliers with different names should not be equal");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
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
            new ClientSupplier(-1, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, null, "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullFirstnameWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", null, "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, false, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void emptyFirstnameWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, false, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullFirstnameWhenNotClientOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", null, "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, "FR12345678901", null
        );
        assertNull(cs.getFirstname());
    }

    @Test
    public void nullEmailThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", null, "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void invalidEmailThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "not-an-email", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullPhoneThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", null,
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nonDigitPhoneThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "012-345-6789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullAddressWhenSupplierThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                null, false, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullAddressWhenNotSupplierOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            null, true, false, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertNull(cs.getAddress());
    }

    @Test
    public void nullVATWhenSupplierThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, null, LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void invalidVATFormatThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, "12INVALID", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void validVATFormatOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, "BE0123456789", LocalDate.of(2020, 1, 15)
        );
        assertEquals("BE0123456789", cs.getVATNumber());
    }

    @Test
    public void nullDateWhenClientThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                null, true, false, false, null, null)
        );
    }

    @Test
    public void nullDateWhenNotClientOk() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), false, true, false, "FR12345678901", null
        );
        assertNull(cs.getBecameClientDate());
    }

    @Test
    public void isUsTrueWithBothFalseThrows() {
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), false, false, true, null, null)
        );
    }

    @Test
    public void getTypeClientAndSupplier() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, null, null
        );
        assertEquals("client and supplier", cs.getType());
    }

    @Test
    public void getTypeUs() throws DataValidationException {
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            new Address("10 Rue de la Paix", "Paris", "France"), true, false, true, null, LocalDate.of(2020, 1, 15)
        );
        assertEquals("us", cs.getType());
    }
}