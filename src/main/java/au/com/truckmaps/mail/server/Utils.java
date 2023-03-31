package au.com.truckmaps.mail.server;

/**
 * This class will consist of helping methods required for TruckMaps.
 *
 * @author Parbati Budhathoki
 * @Created On Mar 31, 2023 11:54:02 AM
 */
public class Utils {

    /**
     * This method will check whether a string is null or empty
     *
     * @param str String value
     * @return true if string is null or empty otherwise returns false
     */
    static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }
}
