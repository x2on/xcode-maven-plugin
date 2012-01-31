package de.felixschulze.maven.plugins.xcode;

/**
 *
 */
public class ExecutionException extends Exception {

    static final long serialVersionUID = -7843278034782074384L;

    /**
     * Constructs an <code>ExecutionException</code>  with no exception message.
     */
    public ExecutionException() {
        super();
    }

    /**
     * Constructs an <code>ExecutionException</code> with the specified exception message.
     *
     * @param message the exception message
     */
    public ExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs an <code>ExecutionException</code> with the specified exception message and cause of the exception.
     *
     * @param message the exception message
     * @param cause   the cause of the exception
     */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an <code>ExecutionException</code> with the cause of the exception.
     *
     * @param cause the cause of the exception
     */
    public ExecutionException(Throwable cause) {
        super(cause);
    }
}
