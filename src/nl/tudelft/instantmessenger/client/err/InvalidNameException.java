package nl.tudelft.instantmessenger.client.err;

/**
 * Created by Jasper on 10/16/2015.
 */
public class InvalidNameException extends Exception {

    public InvalidNameException() {
        super();
    }

    public InvalidNameException(final String message) {
        super(message);
    }
}
