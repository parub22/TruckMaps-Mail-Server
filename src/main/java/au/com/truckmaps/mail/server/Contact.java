package au.com.truckmaps.mail.server;

/**
 * This is a Truckmaps contact form email model.
 *
 * @author Parbati Budhathoki
 * @Created On Mar 31, 2023 10:49:01 AM
 */
public class Contact {

    private String name;
    private String email;
    private String phone;
    private String message;

    public Contact() {
    }

    public Contact(String name, String email, String phone, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Contact{" + "name=" + name + ", email=" + email + ", phone=" + phone + ", message=" + message + '}';
    }

}
