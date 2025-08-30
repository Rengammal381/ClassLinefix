package com.killer.perfectlinerestorer.core;

/**
 * Line number restoration strategies
 * 
 * This enum defines different approaches to restore line numbers,
 * combining the best practices from JDR, jar-debug-line-restorer, and threadtear.
 */
public enum RestoreStrategy {
    /**
     * Sequential numbering - adds line numbers to every instruction
     * Classic line number restoration approach
     */
    SEQUENTIAL("Sequential", "Assigns sequential line numbers to each instruction"),
    
    /**
     * Exception-oriented - adds line numbers only to exception-prone instructions
     * Lightweight restoration approach
     */
    EXCEPTION_ORIENTED("Exception-Oriented", "Adds line numbers only to exception-prone instructions"),
    
    /**
     * Intelligent analysis - adds line numbers based on bytecode semantics
     * Advanced semantic analysis approach
     */
    INTELLIGENT("Intelligent", "Analyzes bytecode patterns for semantic line number placement"),
    
    /**
     * Hybrid strategy (our innovation)
     * Combines all three approaches for optimal results
     */
    HYBRID("Hybrid", "Combines sequential, exception-oriented, and intelligent strategies"),
    
    /**
     * Auto strategy
     * Automatically selects the best strategy based on bytecode characteristics
     */
    AUTO("Auto", "Automatically selects the optimal strategy for each class");
    
    private final String displayName;
    private final String description;
    
    RestoreStrategy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}