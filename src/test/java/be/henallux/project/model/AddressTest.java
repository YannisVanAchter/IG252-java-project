package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.Locality;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;

public class AddressTest {
    private Locality locality;

    @BeforeEach
    public void setUp() {
        try {
            locality = new Locality("Namur", 5000);
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void basicCreationTest() {
        try {
            String streetName = "Rue de la Loi";
            int streetNumber = 16;
            Locality locality = new Locality("marlon",7500);
            Address a = new Address(123, streetName, streetNumber, locality);
            assertEquals(streetName, a.getStreetName(), "Assertion creation Rue de la Loi has failed, street names are different");
            assertEquals(streetNumber, a.getStreetNumber(), "Assertion creation 16 has failed, street numbers are different");
            assertEquals(locality, a.getLocality(), "Assertion creation locality Namur 5000 has failed, localities are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicCreationTestWithLocalityInfo() {
        try {
            String streetName = "Rue de la Loi";
            int streetNumber = 16;
            Locality locality = new Locality("marlon",7500);
            String localityName = locality.getCity();
            int localityPostalCode = locality.getPostalCode();
            Address a = new Address(streetName, streetNumber, localityName, localityPostalCode);
            assertEquals(streetName, a.getStreetName(), "Assertion creation Rue de la Loi has failed, street names are different");
            assertEquals(streetNumber, a.getStreetNumber(), "Assertion creation 16 has failed, street numbers are different");
            assertEquals(new Locality(localityName, localityPostalCode), a.getLocality(), "Assertion creation locality Namur 5000 has failed, localities are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        try {
            Locality locality = new Locality("marlon",7500);
            Address address = new Address(123, "Rue de la Loi", 16, locality);
            assertEquals(address, address, "AssertEqual Rue de la Loi 16 Namur 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            Locality locality = new Locality("marlon",7500);
            Address address = new Address(123, "Rue de la Loi", 16, locality);
            assertNotEquals(address, address, "AssertEqual Rue de la Loi 16 VS 17 Namur 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongStreetNumber() {
        try{
            Locality locality = new Locality("marlon",7500);
            assertThrows(DataValidationException.class, () -> { new Address(123, "Rue de la Loi", -16, locality); });
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void nullLocality() {
        assertThrows(DataValidationException.class, () -> { new Address(123, "Rue de la Loi", 16, (Locality) null); });
    }
}
