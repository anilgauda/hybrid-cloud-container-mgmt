package ie.ncirl.container.manager.library.configurevm.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class ContainerException.
 */
public class ContainerException extends Exception{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6063941682640997197L;

	/** The message. */
	public String message;
	
	/**
	 * Instantiates a new container exception.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public ContainerException(String message,Throwable throwable) {
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
