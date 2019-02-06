package yay.linda.genericbackend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String md5Hash(Object object) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(OBJECT_MAPPER.writeValueAsBytes(object));
            return new String(digest.digest());
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            LOGGER.error("Error calculating md5Hash... {}", e.getMessage());
            return "";
        }
    }
}
