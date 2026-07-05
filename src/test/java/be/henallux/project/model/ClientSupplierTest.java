package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import be.henallux.project.model.Locality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.Address;
import be.henallux.project.model.ClientSupplier;

import java.time.LocalDate;

public class ClientSupplierTest {
    private ClientSupplier clientSupplier;
    
    @BeforeEach
    public void setup() {
        try {
            String streetName = "Rue de la Loi";
            int streetNumber = 16;
            Locality locality = new Locality("marlon",7500);
            Address address = new Address(123, streetName, streetNumber, locality);
            clientSupplier = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
            );
        } catch (DataValidationException e) {
            fail("Failed to initialize test data");
        }
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
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
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            1, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, false, false, null, LocalDate.of(2020, 1, 15)
        );
        assertTrue(cs.getIsClient());
        assertFalse(cs.getIsSupplier());
        assertEquals("client", cs.getType());
    }

    @Test
    public void basicCreationTestSupplierOnly() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            2, "Dupont", null, "jean.dupont@example.com", "0123456789",
            address, false, true, false, "FR12345678901", null
        );
        assertFalse(cs.getIsClient());
        assertTrue(cs.getIsSupplier());
        assertEquals("supplier", cs.getType());
    }

    @Test
    public void basicCreationTestIsUs() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            3, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, false, true, null, LocalDate.of(2020, 1, 15)
        );
        assertTrue(cs.getIsUs());
        assertEquals("us", cs.getType());
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs1 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        ClientSupplier cs2 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertEquals(cs1, cs2, "Two identical ClientSuppliers should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs1 = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        ClientSupplier cs2 = new ClientSupplier(
            1, "Martin", "Jean", "jean.martin@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );
        assertNotEquals(cs1, cs2, "ClientSuppliers with different names should not be equal");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
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
    public void negativeIdThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(-1, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void emptyNameThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullNameThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, null, "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullFirstnameWhenClientThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", null, "jean.dupont@example.com", "0123456789",
                address, true, false, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void emptyFirstnameWhenClientThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "", "jean.dupont@example.com", "0123456789",
                address, true, false, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullFirstnameWhenNotClientOk() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", null, "jean.dupont@example.com", "0123456789",
            address, false, true, false, "FR12345678901", null
        );
        assertNull(cs.getFirstname());
    }

    @Test
    public void nullEmailThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", null, "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void invalidEmailThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "not-an-email", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullPhoneThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", null,
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nonDigitPhoneThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "012-345-6789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void nullAddressWhenSupplierThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
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
    public void nullVATWhenSupplierThrows() throws  DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, false, true, false, null, LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void invalidVATFormatThrows() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, false, true, false, "12INVALID", LocalDate.of(2020, 1, 15))
        );
    }

    @Test
    public void validVATFormatOk() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, false, true, false, "BE0123456789", LocalDate.of(2020, 1, 15)
        );
        assertEquals("BE0123456789", cs.getVATNumber());
    }

    @Test
    public void nullDateWhenClientThrows() throws  DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        assertThrows(DataValidationException.class, () ->
            new ClientSupplier(0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                null, true, false, false, null, null)
        );
    }

    @Test
    public void nullDateWhenNotClientOk() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, false, true, false, "FR12345678901", null
        );
        assertNull(cs.getBecameClientDate());
    }

    @Test
    public void getTypeClientAndSupplier() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "BE12345678901", LocalDate.of(2020, 1, 15)
        );
        assertEquals("client and supplier", cs.getType());
    }

    @Test
    public void getTypeUs() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        ClientSupplier cs = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, false, true, null, LocalDate.of(2020, 1, 15)
        );
        assertEquals("us", cs.getType());
    }
}