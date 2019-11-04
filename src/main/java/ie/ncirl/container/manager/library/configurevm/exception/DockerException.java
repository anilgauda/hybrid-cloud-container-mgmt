package ie.ncirl.container.manager.library.configurevm.exception;

public class DockerException extends Exception{
	private static final long serialVersionUID = 8322950989546630674L;
	public String message;
	public DockerException(String message,Throwable throwable) {
		super(message,throwable);
		message=this.message;
	}
	public String getException() {
		return message;
	}
}
