package tn.esprit.eventsphere;


public class User {

    // Fields for the User class
    private int id;
    private String email;
    private String password;  // Hashed password
    private String salt;      // Salt used for hashing the password
    private String firstName;
    private String lastName;
    private String role;
    private String hashedPassword;
    private byte[] profileImage;



    public byte[] getProfileImage() {
        return this.profileImage;
    }


    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }


    // Constructor (optional)
    public User() {
        // You can initialize the fields if needed
    }

    // Getter and Setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for Salt
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    // Getter and Setter for First Name
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter and Setter for Last Name
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getter and Setter for Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}


