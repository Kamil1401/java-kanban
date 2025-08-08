package tracker.exceptions;

import java.lang.RuntimeException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }
}
