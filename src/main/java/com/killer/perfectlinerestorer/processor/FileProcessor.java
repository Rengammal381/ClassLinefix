package com.killer.perfectlinerestorer.processor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Base interface for file processors
 * 
 * This interface defines the contract for processing different types of files
 * in the line number restoration process.
 */
public interface FileProcessor {
    
    /**
     * Process a file and return whether it was modified
     * 
     * @param inputFile the input file path
     * @param outputFile the output file path
     * @return true if the file was modified, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean processFile(Path inputFile, Path outputFile) throws IOException;
    
    /**
     * Check if this processor can handle the given file
     * 
     * @param filePath the file path to check
     * @return true if this processor can handle the file, false otherwise
     */
    boolean canProcess(Path filePath);
    
    /**
     * Get the processor name for logging purposes
     * 
     * @return the processor name
     */
    String getProcessorName();
}