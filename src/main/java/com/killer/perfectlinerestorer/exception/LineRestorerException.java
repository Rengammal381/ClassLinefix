package com.killer.perfectlinerestorer.exception;

/**
 * Base exception for Perfect Line Restorer
 * 
 * This is the base exception class for all custom exceptions in the
 * line number restoration tool.
 */
public class LineRestorerException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final ErrorCode errorCode;
    private final String context;
    
    public LineRestorerException(String message) {
        super(message);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.context = null;
    }
    
    public LineRestorerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.context = null;
    }
    
    public LineRestorerException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public LineRestorerException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public LineRestorerException(ErrorCode errorCode, String message, String context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public LineRestorerException(ErrorCode errorCode, String message, String context, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getContext() {
        return context;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (errorCode != null) {
            sb.append("[").append(errorCode.getCode()).append("] ");
        }
        
        sb.append(super.getMessage());
        
        if (context != null && !context.trim().isEmpty()) {
            sb.append(" (Context: ").append(context).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * Error codes for different types of errors
     */
    public enum ErrorCode {
        GENERAL_ERROR("GEN001", "General error"),
        FILE_NOT_FOUND("FILE001", "File not found"),
        FILE_READ_ERROR("FILE002", "File read error"),
        FILE_WRITE_ERROR("FILE003", "File write error"),
        INVALID_CLASS_FILE("CLASS001", "Invalid class file"),
        CLASS_PARSE_ERROR("CLASS002", "Class parsing error"),
        LINE_NUMBER_ERROR("LINE001", "Line number processing error"),
        JAR_PROCESSING_ERROR("JAR001", "JAR processing error"),
        INVALID_ARGUMENTS("ARG001", "Invalid command line arguments"),
        DIRECTORY_ERROR("DIR001", "Directory operation error"),
        STRATEGY_ERROR("STRAT001", "Strategy execution error"),
        BACKUP_ERROR("BACKUP001", "Backup operation error"),
        VALIDATION_ERROR("VAL001", "Validation error");
        
        private final String code;
        private final String description;
        
        ErrorCode(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return code + ": " + description;
        }
    }
}