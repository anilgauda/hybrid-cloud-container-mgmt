package ie.ncirl.container.manager.library.configurevm.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class DockerException.
 */
public class DockerException extends Exception{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8322950989546630674L;
	
	/** The message. */
	public String message;
	
	/**
	 * Instantiates a new docker exception.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public DockerException(String message,Throwable throwable) {
		super(message,throwable);
		message=this.message;
	}
	
	/**
	 * Gets the exception.
	 *
	 * @return the exception
	 */
	public String getException() {
		return message;
	}
}
