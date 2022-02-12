package G13.database;

public class AccountTable {
    String username, password, firstname, lastname, city, adress, email, phonenumber, role;
    int id, zipcode;


    /**
     * A constructor with the following parameters is created when the class is opened.
     * The values of the variables are taken from the parameters.
     * Getter and Setter declaration.
     * @param id
     * @param username
     * @param password
     * @param firstname
     * @param lastname
     * @param zipcode
     * @param city
     * @param adress
     * @param email
     * @param phonenumber
     * @param role
     */
    public AccountTable(int id, String username, String password, String firstname, String lastname,
                        int zipcode, String city, String adress, String email, String phonenumber, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.zipcode = zipcode;
        this.city = city;
        this.adress = adress;
        this.email = email;
        this.phonenumber = phonenumber;
        this.role = role;
    }

    public int getId() {return id;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public String getFirstname() {return firstname;}

    public String getLastname() {return lastname;}

    public int getZipcode() {return zipcode;}

    public String getCity() {return city;}

    public String getAdress() {return adress;}

    public String getEmail() {return email;}

    public String getPhonenumber() {return phonenumber;}

    public String getRole() {return role;}

    public void setId(int id) {this.id = id;}

    public void setUsername(String username) {this.username = username;}

    public void setPassword(String password) {this.password = password;}

    public void setFirstname(String firstname) {this.firstname = firstname;}

    public void setLastname(String lastname) {this.lastname = lastname;}

    public void setZipcode(int zipcode) {this.zipcode = zipcode;}

    public void setCity(String city) {this.city = city;}

    public void setAdress(String adress) {this.adress = adress;}

    public void setEmail(String email) {this.email = email;}

    public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;}

    public void setRole(String role) {this.role = role;}
}
