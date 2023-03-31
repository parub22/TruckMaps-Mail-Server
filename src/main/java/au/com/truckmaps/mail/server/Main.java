package au.com.truckmaps.mail.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * This programs accept contact mail request from the frontend and sent email to
 * admin.
 *
 * @author Parbati Budhathoki
 * @Created On Mar 24, 2023 1:43:32 PM
 */
public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    static {
        Configurator.initialize(null, "etc/log4j2.properties");
    }

    public static void main(String[] args) {
        port(6778);               //runs on defined port

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        // Enable CORS for all origins and methods
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        post("/mail", (req, res) -> Mailer.sendEmail(req, res));

    }
}
