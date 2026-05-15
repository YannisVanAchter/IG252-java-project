package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.DataValidationException;
import model.LocationProduct;
import model.QuantityProduct;

public class QuantityProductTest {
    private LocationProduct locationProduct;
    
    @BeforeEach
    public void setUp() {
        try {
            locationProduct = new LocationProduct("shelf A1","floor 1",true,false);
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicCreationTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            assertEquals(locationProduct, qp.getLocationProduct(), "Assertion creation locationProduct shelf A1 floor 1 has failed, locationProducts are different");
            assertEquals(10, qp.getQuantity(), "Assertion creation quantity 10 has failed, quantities are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicCreationTestWithProduct() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            assertEquals(locationProduct, qp.getLocationProduct(), "Assertion creation locationProduct shelf A1 floor 1 has failed, locationProducts are different");
            assertEquals(10, qp.getQuantity(), "Assertion creation quantity 10 has failed, quantities are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            QuantityProduct qp1 = new QuantityProduct(locationProduct, null, 10);
            QuantityProduct qp2 = new QuantityProduct(locationProduct, null, 10);
            assertEquals(qp1, qp2, "AssertEqual QuantityProduct with same locationProduct and quantity not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            QuantityProduct qp1 = new QuantityProduct(locationProduct, null, 10);
            LocationProduct newLocationProduct = new LocationProduct("shelf B2","floor 2",true,false);
            QuantityProduct qp2 = new QuantityProduct(newLocationProduct, null, 10);
            assertNotEquals(qp1, qp2, "AssertEqual QuantityProduct with different locationProducts not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLabelTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            assertEquals("null at shelf A1 floor 1 : 10", qp.getLabel(), "Assertion getLabel null at shelf A1 floor 1 : 10 has failed, labels are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void toStringTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            assertEquals("QuantityProduct{locationProduct=shelf A1 floor 1, product=null, quantity=10}", qp.toString(), "Assertion toString QuantityProduct{locationProduct=shelf A1 floor 1, product=null, quantity=10} has failed, toStrings are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void quantityNegativeTest() {
        try {
            new QuantityProduct(locationProduct, null, -1);
            fail("Creation of QuantityProduct with quantity -1 should have thrown a DataValidationException");
        } catch (DataValidationException e) {
            assertEquals("Quantity setting error, quantity is lower than 0 when it shouldn't (current value: -1)", e.getMessage(), "Assertion quantity setting error message has failed, messages are different");
        }
    }

    @Test
    public void nullLocationProductTest() {
        try {
            new QuantityProduct(null, null, 10);
            fail("Creation of QuantityProduct with null locationProduct should have thrown a DataValidationException");
        } catch (DataValidationException e) {
            assertEquals("LocationProduct setting error, locationProduct is null when it shouldn't", e.getMessage(), "Assertion locationProduct setting error message has failed, messages are different");
        }
    }

    @Test
    public void nullProductTest() {
        try {
            new QuantityProduct(locationProduct, null, 10);
            fail("Creation of QuantityProduct with null product should have thrown a DataValidationException");
        } catch (DataValidationException e) {
            assertEquals("Product setting error, product is null when it shouldn't", e.getMessage(), "Assertion product setting error message has failed, messages are different");
        }
    }

    @Test
    public void setQuantityTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            qp.setQuantity(20);
            assertEquals(20, qp.getQuantity(), "Assertion setQuantity 20 has failed, quantities are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setQuantityNegativeTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            qp.setQuantity(-1);
            fail("Setting quantity to -1 should have thrown a DataValidationException");
        } catch (DataValidationException e) {
            assertEquals("Quantity setting error, quantity is lower than 0 when it shouldn't (current value: -1)", e.getMessage(), "Assertion quantity setting error message has failed, messages are different");
        }
    }

    @Test
    public void setLocationProductTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            LocationProduct newLocationProduct = new LocationProduct("shelf B2","floor 2",true,false);
            qp.setLocationProduct(newLocationProduct);
            assertEquals(newLocationProduct, qp.getLocationProduct(), "Assertion setLocationProduct shelf B2 floor 2 has failed, locationProducts are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setLocationProductNullTest() {
        try {
            QuantityProduct qp = new QuantityProduct(locationProduct, null, 10);
            qp.setLocationProduct(null);
            fail("Setting locationProduct to null should have thrown a DataValidationException");
        } catch (DataValidationException e) {
            assertEquals("LocationProduct setting error, locationProduct is null when it shouldn't", e.getMessage(), "Assertion locationProduct setting error message has failed, messages are different");
        }
    }
}