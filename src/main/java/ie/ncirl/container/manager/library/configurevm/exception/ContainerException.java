package ie.ncirl.container.manager.library.configurevm.exception;

public class ContainerException extends Exception{
	private static final long serialVersionUID = -6063941682640997197L;

	public String message;
	public ContainerException(String message,Throwable throwable) {
		super(message,throwable);
		message=this.message;
	}
	public String getException() {
		return message;
	}
}
