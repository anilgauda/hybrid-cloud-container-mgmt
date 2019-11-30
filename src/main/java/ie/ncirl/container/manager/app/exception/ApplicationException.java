package ie.ncirl.container.manager.app.exception;

public class ApplicationException extends Exception {

	private static final long serialVersionUID = 1L;
	public String message;

	public ApplicationException(String message, Throwable throwable) {
		super(message, throwable);
		message = this.message;
	}

	public ApplicationException(String message) {
		message=this.message;
	}

	public String getException() {
		return message;
	}
}
