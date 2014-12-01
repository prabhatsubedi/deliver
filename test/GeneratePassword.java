import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/25/14
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneratePassword {

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("password");
        System.out.println(hashedPassword);
    }

}
