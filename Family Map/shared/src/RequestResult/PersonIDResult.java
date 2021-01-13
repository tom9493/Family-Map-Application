package RequestResult;

public class PersonIDResult {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;
    private boolean success;
    private String message;

    public PersonIDResult() { success = false; }

    public PersonIDResult(String personID, String username, String firstName, String lastName, String gender,
                  String fatherID, String motherID, String spouseID, boolean success) {
        this.personID = personID;
        this.associatedUsername = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = success;
    }

    public String getMessage() { return message; }

    public void setMessage(String errorMesage) { this.message = errorMesage; }

    public boolean isSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }

    public String getPersonID() { return personID; }

    public void setPersonID(String PersonID) {
        this.personID = PersonID;
    }

    public String getUsername() {
        return associatedUsername;
    }

    public void setUsername(String username) {
        this.associatedUsername = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) { this.spouseID = spouseID; }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof model.Person) {
            model.Person oPerson = (model.Person) o;
            if (oPerson.getFatherID() == null && getFatherID() != null) { return false; }
            if (oPerson.getFatherID() != null && getFatherID() == null) { return false; }
            if (oPerson.getFatherID() != null && getFatherID() != null) {
                if (!oPerson.getFatherID().equals(getFatherID())) { return false; }
            }
            if (oPerson.getMotherID() == null && getMotherID() != null) { return false; }
            if (oPerson.getMotherID() != null && getMotherID() == null) { return false; }
            if (oPerson.getMotherID() != null && getMotherID() != null) {
                if (!oPerson.getMotherID().equals(getMotherID())) { return false; }
            }
            if (oPerson.getSpouseID() == null && getSpouseID() != null) { return false; }
            if (oPerson.getSpouseID() != null && getSpouseID() == null) { return false; }
            if (oPerson.getSpouseID() != null && getSpouseID() != null) {
                if (!oPerson.getSpouseID().equals(getSpouseID())) { return false; }
            }
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getUsername().equals(getUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender().equals(getGender());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "personID='" + personID + '\'' +
                ", associatedUsername='" + associatedUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", fatherID='" + fatherID + '\'' +
                ", motherID='" + motherID + '\'' +
                ", spouseID='" + spouseID + '\'' +
                '}';
    }
}

