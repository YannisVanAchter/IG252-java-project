package main.java.be.henallux.project.model;

import java.time.LocalDate;

import main.java.be.henallux.project.model.exception.DataValidationException;

/**
 * This class represents a client or supplier, which can be either a client, a supplier, or both.
 * It contains various fields such as id, name, firstname, email, phone number, address, VAT number, and the date they became a client.
 * The class includes several validations for these fields such as regex, not null, not empty, and logical consistency between the boolean fields (isClient, isSupplier, isUs).
 * The class provides a constructor that takes all the fields as parameters and performs the necessary validations.
 */
public class ClientSupplier implements Model {
    private int id;
    private String name;
    private String firstname;
    private String email;
    private String phoneNumber;
    private Address address;
    private boolean isClient;
    private boolean isSupplier;
    private boolean isUs;
    private String VATNumber;
    private LocalDate becameClientDate;
    private FidelityCard fidelityCard;

    public ClientSupplier(int id, String name, String firstname, String email, String phoneNumber, Address address, boolean isClient, boolean isSupplier, boolean isUs, String VATNumber, LocalDate becameClientDate, FidelityCard fidelityCard) throws DataValidationException {
        setId(id);
        setIsClient(isClient);
        setIsSupplier(isSupplier);
        setIsUs(isUs);
        setName(name);
        setFirstname(firstname);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setAddress(address);
        setVATNumber(VATNumber);
        setBecameClientDate(becameClientDate);
        setFidelityCard(fidelityCard);
    }

    public ClientSupplier(int id, String name, String firstname, String email, String phoneNumber, Address address, boolean isClient, boolean isSupplier, boolean isUs, String VATNumber, LocalDate becameClientDate) throws DataValidationException {
        this(id, name, firstname, email, phoneNumber, address, isClient, isSupplier, isUs, VATNumber, becameClientDate, null);
    }

    public ClientSupplier(String name, String firstname, String email, String phoneNumber, Address address, boolean isClient, boolean isSupplier, boolean isUs, String VATNumber, LocalDate becameClientDate, FidelityCard fidelityCard) throws DataValidationException {
        this(0, name, firstname, email, phoneNumber, address, isClient, isSupplier, isUs, VATNumber, becameClientDate, fidelityCard);
    }

    public int getId() { return id; }

    public void setId(int id) throws DataValidationException {
        if (id < 0) {
            String message = "ID setting error, ID is lower or equal to 0 (zero) when it shouldn't (current value: " + id + ")";
            throw new DataValidationException(message);
        }
        this.id = id;
    }

    public boolean getIsClient() { return isClient; }

    private void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }

    public boolean getIsSupplier() { return isSupplier; }

    private void setIsSupplier(boolean isSupplier) {
        this.isSupplier = isSupplier;
    }

    public boolean getIsUs() { return isUs; }

    private void setIsUs(boolean isUs) throws DataValidationException {
        if (isUs && !isClient && !isSupplier) {
            String message = "IsUs setting error, isUs is true while isClient and isSupplier are both false when it shouldn't (current values: isUs=" + isUs + ", isClient=" + getIsClient() + ", isSupplier=" + getIsSupplier() + ")";
            throw new DataValidationException(message);
        }
        this.isUs = isUs;
    }

    /**
     * Returns a string representation of the type of the client/supplier based on the boolean fields isClient, isSupplier, and isUs.
     * @return A string representing the type of the client/supplier, which can be "us", "client", "supplier", or "client and supplier" depending on the values of the boolean fields.
     */
    public String getType() {
        if (getIsUs()) 
            return "us";
        StringBuilder type = new StringBuilder();
        if (getIsClient()) 
            type.append("client");
        if (getIsSupplier()) {
            if (type.length() > 0)
                type.append(" and ");
            type.append("supplier");
        }
        return type.toString();
    }

    public String getName() { return name; }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.isEmpty()) {
            String message = "Name setting error, name is null or empty when it shouldn't (current value: " + name + ")";
            throw new DataValidationException(message);
        }
        this.name = name;
    }

    public String getFirstname() { return firstname; }

    private void setFirstname(String firstname) throws DataValidationException {
        if (getIsClient() && (firstname == null || firstname.isEmpty())) {
            String message = "Firstname setting error, firstname is null or empty when it shouldn't (current value: " + firstname + ")";
            throw new DataValidationException(message);
        }
        this.firstname = firstname;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) throws DataValidationException {
        if (email == null || email.isEmpty()) {
            String message = "Email setting error, email is null or empty when it shouldn't (current value: " + email + ")";
            throw new DataValidationException(message);
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            String message = "Email setting error, email does not contain '@' character when it should (current value: " + email + ")";
            throw new DataValidationException(message);
        }
        this.email = email;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) throws DataValidationException {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            String message = "Phone number setting error, phone number is null or empty when it shouldn't (current value: " + phoneNumber + ")";
            throw new DataValidationException(message);
        }
        if (!phoneNumber.matches("^[0-9]+$")) {
            String message = "Phone number setting error, phone number contains non-digit characters when it shouldn't (current value: " + phoneNumber + ")";
            throw new DataValidationException(message);
        }
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() { return address; }

    public void setAddress(Address address) throws DataValidationException {
        if (getIsSupplier() && address == null) {
            String message = "Address setting error, address is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.address = address;
    }

    public String getVATNumber() { return VATNumber; }

    private void setVATNumber(String VATNumber) throws DataValidationException {
        if (getIsSupplier() && (VATNumber == null || VATNumber.isEmpty())) {
            String message = "VAT number setting error, VAT number is null or empty when it shouldn't (current value: " + VATNumber + ")";
            throw new DataValidationException(message);
        }
        if (getIsSupplier() && !VATNumber.matches("^[A-Z]{2}[0-9A-Z]+$")) {
            String message = "VAT number setting error, VAT number does not match the required format (current value: " + VATNumber + ")";
            throw new DataValidationException(message);
        }
        this.VATNumber = VATNumber;
    }

    public LocalDate getBecameClientDate() { return becameClientDate; }

    private void setBecameClientDate(LocalDate becameClientDate) throws DataValidationException {
        if (getIsClient() && becameClientDate == null) {
            String message = "Became client date setting error, became client date is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.becameClientDate = becameClientDate;
    }

    public FidelityCard getFidelityCard() { return this.fidelityCard; }

    public void setFidelityCard(FidelityCard fidelityCard) throws DataValidationException {
        if (!getIsClient() && fidelityCard != null) {
            throw new DataValidationException("One must be a client to posses a fidelity card");
        }
        this.fidelityCard = fidelityCard;
    }

    public String getLabel() {
        return String.format("(%s%s%s) %s %s", 
            (getIsClient() ? "C": ""),
            (getIsSupplier() ? "S": ""),
            (getIsUs() ? "Us":""),
            getName(),
            (getIsClient() ? getFirstname(): "")
        );
    }

    @Override
    public String toString() {
        return  "ClientSupplier{id=" + id + 
                                ", name='" + name + 
                                ", firstname='" + firstname + 
                                "'', email='" + email + 
                                "'', phoneNumber='" + phoneNumber + 
                                "', address=" + (address != null ? address.toString() : "null") + 
                                ", isClient=" + isClient + 
                                ", isSupplier=" + isSupplier + 
                                ", isUs=" + isUs + 
                                ", VATNumber='" + VATNumber + 
                                "', becameClientDate=" + becameClientDate + 
                                ", fidelityCard=" + ( (fidelityCard != null) ? fidelityCard.toString(): "null") +
                                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ClientSupplier other = (ClientSupplier) obj;
        return  id == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
