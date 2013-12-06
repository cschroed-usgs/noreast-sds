package gov.usgs.noreast.noreastsds.utils.exception;

public class SDSExceptionID {
	private final Long exceptionId;
    private String name;

    private SDSExceptionID( String name, int id ) {
        this.name = name;
        this.exceptionId = Long.valueOf(id);
    }

    public String toString() { return this.name; }
    public Long value() { return this.exceptionId; }

    //-----------------------------------------
    // EXCEPTION DEFINITIONS
    //-----------------------------------------

    // SOSDataService Exceptions
    public static final SDSExceptionID INVALID_JOB_ID =
        new SDSExceptionID("SDSExceptionID Exception: Invalid Query ID", 0x00000);
    
    public static final SDSExceptionID JOB_ID_INTERRUPTED =
            new SDSExceptionID("SDSExceptionID Exception: Query ID has been interrupted.", 0x00001);
    
    public static final SDSExceptionID JOB_ID_ERROR =
            new SDSExceptionID("SDSExceptionID Exception: Query ID has encountered an error.", 0x00002);
}
