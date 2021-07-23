package exception;

public class CategoryRepositoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CategoryRepositoryException(String message) {
		super(message);
	}
	
	public CategoryRepositoryException(String message, Exception cause) {
		super(message, cause);
	}

}
