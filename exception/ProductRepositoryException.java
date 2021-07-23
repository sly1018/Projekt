package exception;

public class ProductRepositoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductRepositoryException(String message) {
		super(message);
	}

	public ProductRepositoryException(String message, Exception cause) {
		super(message, cause);
	}

}
