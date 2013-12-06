package gov.usgs.noreast.noreastsds.utils.exception;

public class SDSException extends Exception {
	private static final long serialVersionUID = 1L;

	private final SDSExceptionID exceptionid;	// uniquely generated id for this exception
	private final String classname; 			// the name of the class that threw the exception
	private final String method; 				// the name of the method that threw the exception
	private final String message; 				// a detailed message
	private SDSException previous = null; 		// the exception which was caught
	private Exception baseException = null;		// original exception
	
	private String delimeter = "\n"; 			// line separator

	public SDSException(final SDSExceptionID id, final String classname,
			final String method, final String message) {
		this.exceptionid = id;
		this.classname = classname;
		this.method = method;
		this.message = message;
		this.previous = null;
		this.baseException = null;
	}

	public SDSException(final SDSExceptionID id, final String classname,
			final String method, final String message,
			final SDSException previous) {
		this.exceptionid = id;
		this.classname = classname;
		this.method = method;
		this.message = message;
		this.previous = previous;
		this.baseException = null;
	}
	
	public SDSException(final SDSExceptionID id, final String classname,
			final String method, final String message,
			final Exception base) {
		this.exceptionid = id;
		this.classname = classname;
		this.method = method;
		this.message = message;
		this.previous = null;
		this.baseException = base;
	}

	public String traceBack() {
		return traceBack("\n");
	}

	public String traceBack(final String sep) {
		this.delimeter = sep;
		int level = 0;
		SDSException e = this;
		final StringBuffer text = new StringBuffer(
				line("SDSException Trace: Calling sequence (top to bottom)"));
		while (e != null) {
			level++;
			text.append(this.delimeter);
			text.append(line("--level " + level
					+ "--------------------------------------"));
			text.append(line("Class/Method: " + e.classname + "/" + e.method));
			text.append(line("Id          : " + e.exceptionid));
			text.append(line("Message     : " + e.message));
			
			Exception base = e.getBaseException();
			if(base != null) {
				level++;
				text.append(this.delimeter);
				text.append(line("--level " + level + " [BASE]"
						+ "--------------------------------------"));
				text.append(line("Exception: " + base.getClass().toString()));
				text.append(line("Message     : " + base.getMessage()));
			}			
			
			e = e.previous;
		}
		
		return text.toString();
	}

	private String line(final String s) {
		return s + this.delimeter;
	}

	@Override
	public String getMessage() {
		return this.traceBack();
	}

	@Override
	public String toString() {
		return this.traceBack();
	}

	public SDSExceptionID getExceptionid() {
		return this.exceptionid;
	}

	public String getClassname() {
		return this.classname;
	}

	public String getMethod() {
		return this.method;
	}

	public SDSException getPrevious() {
		return this.previous;
	}
	
	public Exception getBaseException() {
		return this.baseException;
	}

	public String getMessageOnly() {
		return this.message;
	}
}
