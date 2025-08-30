package com.killer.perfectlinerestorer.model;

import com.killer.perfectlinerestorer.util.FileUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Processing Statistics
 * 
 * This class tracks statistics during the line number restoration process,
 * including file counts, processing times, and error information.
 */
public class ProcessingStats {
    private final Instant startTime;
    private Instant endTime;
    
    // File counts
    private final AtomicInteger totalFiles = new AtomicInteger(0);
    private final AtomicInteger processedFiles = new AtomicInteger(0);
    private final AtomicInteger skippedFiles = new AtomicInteger(0);
    private final AtomicInteger errorFiles = new AtomicInteger(0);
    
    // File type counts
    private final AtomicInteger jarFiles = new AtomicInteger(0);
    private final AtomicInteger classFiles = new AtomicInteger(0);
    private final AtomicInteger otherFiles = new AtomicInteger(0);
    
    // Processing counts
    private final AtomicInteger jarProcessed = new AtomicInteger(0);
    private final AtomicInteger classProcessed = new AtomicInteger(0);
    private final AtomicInteger jarSkipped = new AtomicInteger(0);
    private final AtomicInteger classSkipped = new AtomicInteger(0);
    
    // Size information
    private final AtomicLong totalInputSize = new AtomicLong(0);
    private final AtomicLong totalOutputSize = new AtomicLong(0);
    
    // Line number statistics
    private final AtomicInteger filesWithExistingLineNumbers = new AtomicInteger(0);
    private final AtomicInteger filesWithAddedLineNumbers = new AtomicInteger(0);
    private final AtomicInteger totalLineNumbersAdded = new AtomicInteger(0);
    
    // Strategy usage counts
    private final AtomicInteger sequentialStrategy = new AtomicInteger(0);
    private final AtomicInteger exceptionOrientedStrategy = new AtomicInteger(0);
    private final AtomicInteger intelligentStrategy = new AtomicInteger(0);
    private final AtomicInteger hybridStrategy = new AtomicInteger(0);
    private final AtomicInteger autoStrategy = new AtomicInteger(0);
    
    public ProcessingStats() {
        this.startTime = Instant.now();
    }
    
    // File counting methods
    public void incrementTotalFiles() {
        totalFiles.incrementAndGet();
    }
    
    public void incrementProcessedFiles() {
        processedFiles.incrementAndGet();
    }
    
    public void incrementSkippedFiles() {
        skippedFiles.incrementAndGet();
    }
    
    public void incrementErrorFiles() {
        errorFiles.incrementAndGet();
    }
    
    // File type counting methods
    public void incrementJarFiles() {
        jarFiles.incrementAndGet();
    }
    
    public void incrementClassFiles() {
        classFiles.incrementAndGet();
    }
    
    public void incrementOtherFiles() {
        otherFiles.incrementAndGet();
    }
    
    // Processing counting methods
    public void incrementJarProcessed() {
        jarProcessed.incrementAndGet();
    }
    
    public void incrementClassProcessed() {
        classProcessed.incrementAndGet();
    }
    
    public void incrementJarSkipped() {
        jarSkipped.incrementAndGet();
    }
    
    public void incrementClassSkipped() {
        classSkipped.incrementAndGet();
    }
    
    // Size tracking methods
    public void addInputSize(long size) {
        totalInputSize.addAndGet(size);
    }
    
    public void addOutputSize(long size) {
        totalOutputSize.addAndGet(size);
    }
    
    // Line number tracking methods
    public void incrementFilesWithExistingLineNumbers() {
        filesWithExistingLineNumbers.incrementAndGet();
    }
    
    public void incrementFilesWithAddedLineNumbers() {
        filesWithAddedLineNumbers.incrementAndGet();
    }
    
    public void addLineNumbersAdded(int count) {
        totalLineNumbersAdded.addAndGet(count);
    }
    
    // Strategy tracking methods
    public void incrementSequentialStrategy() {
        sequentialStrategy.incrementAndGet();
    }
    
    public void incrementExceptionOrientedStrategy() {
        exceptionOrientedStrategy.incrementAndGet();
    }
    
    public void incrementIntelligentStrategy() {
        intelligentStrategy.incrementAndGet();
    }
    
    public void incrementHybridStrategy() {
        hybridStrategy.incrementAndGet();
    }
    
    public void incrementAutoStrategy() {
        autoStrategy.incrementAndGet();
    }
    
    // Timing methods
    public void markCompleted() {
        this.endTime = Instant.now();
    }
    
    public Duration getProcessingDuration() {
        Instant end = endTime != null ? endTime : Instant.now();
        return Duration.between(startTime, end);
    }
    
    // Getter methods
    public int getTotalFiles() {
        return totalFiles.get();
    }
    
    public int getProcessedFiles() {
        return processedFiles.get();
    }
    
    public int getSkippedFiles() {
        return skippedFiles.get();
    }
    
    public int getErrorFiles() {
        return errorFiles.get();
    }
    
    public int getJarFiles() {
        return jarFiles.get();
    }
    
    public int getClassFiles() {
        return classFiles.get();
    }
    
    public int getOtherFiles() {
        return otherFiles.get();
    }
    
    public int getJarProcessed() {
        return jarProcessed.get();
    }
    
    public int getClassProcessed() {
        return classProcessed.get();
    }
    
    public int getJarSkipped() {
        return jarSkipped.get();
    }
    
    public int getClassSkipped() {
        return classSkipped.get();
    }
    
    public long getTotalInputSize() {
        return totalInputSize.get();
    }
    
    public long getTotalOutputSize() {
        return totalOutputSize.get();
    }
    
    public int getFilesWithExistingLineNumbers() {
        return filesWithExistingLineNumbers.get();
    }
    
    public int getFilesWithAddedLineNumbers() {
        return filesWithAddedLineNumbers.get();
    }
    
    public int getTotalLineNumbersAdded() {
        return totalLineNumbersAdded.get();
    }
    
    // Calculated properties
    public double getProcessingRate() {
        Duration duration = getProcessingDuration();
        long seconds = duration.getSeconds();
        return seconds > 0 ? (double) getProcessedFiles() / seconds : 0.0;
    }
    
    public double getSuccessRate() {
        int total = getTotalFiles();
        return total > 0 ? (double) getProcessedFiles() / total * 100.0 : 0.0;
    }
    
    public double getSkipRate() {
        int total = getTotalFiles();
        return total > 0 ? (double) getSkippedFiles() / total * 100.0 : 0.0;
    }
    
    public double getErrorRate() {
        int total = getTotalFiles();
        return total > 0 ? (double) getErrorFiles() / total * 100.0 : 0.0;
    }
    
    public double getCompressionRatio() {
        long input = getTotalInputSize();
        long output = getTotalOutputSize();
        return input > 0 ? (double) output / input : 1.0;
    }
    
    // Summary methods
    public String getQuickSummary() {
        return String.format("Processed: %d/%d (%.1f%%), Skipped: %d, Errors: %d, Duration: %s",
                           getProcessedFiles(), getTotalFiles(), getSuccessRate(),
                           getSkippedFiles(), getErrorFiles(), formatDuration(getProcessingDuration()));
    }
    
    public String getDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Processing Summary ===").append("\n");
        sb.append(String.format("Total Files: %d", getTotalFiles())).append("\n");
        sb.append(String.format("  - JAR files: %d", getJarFiles())).append("\n");
        sb.append(String.format("  - CLASS files: %d", getClassFiles())).append("\n");
        sb.append(String.format("  - Other files: %d", getOtherFiles())).append("\n");
        sb.append("\n");
        
        sb.append("Processing Results:").append("\n");
        sb.append(String.format("  - Processed: %d (%.1f%%)", getProcessedFiles(), getSuccessRate())).append("\n");
        sb.append(String.format("  - Skipped: %d (%.1f%%)", getSkippedFiles(), getSkipRate())).append("\n");
        sb.append(String.format("  - Errors: %d (%.1f%%)", getErrorFiles(), getErrorRate())).append("\n");
        sb.append("\n");
        
        sb.append("File Type Processing:").append("\n");
        sb.append(String.format("  - JAR processed: %d, skipped: %d", getJarProcessed(), getJarSkipped())).append("\n");
        sb.append(String.format("  - CLASS processed: %d, skipped: %d", getClassProcessed(), getClassSkipped())).append("\n");
        sb.append("\n");
        
        sb.append("Line Number Statistics:").append("\n");
        sb.append(String.format("  - Files with existing line numbers: %d", getFilesWithExistingLineNumbers())).append("\n");
        sb.append(String.format("  - Files with added line numbers: %d", getFilesWithAddedLineNumbers())).append("\n");
        sb.append(String.format("  - Total line numbers added: %d", getTotalLineNumbersAdded())).append("\n");
        sb.append("\n");
        
        sb.append("Strategy Usage:").append("\n");
        sb.append(String.format("  - Sequential: %d", sequentialStrategy.get())).append("\n");
        sb.append(String.format("  - Exception-oriented: %d", exceptionOrientedStrategy.get())).append("\n");
        sb.append(String.format("  - Intelligent: %d", intelligentStrategy.get())).append("\n");
        sb.append(String.format("  - Hybrid: %d", hybridStrategy.get())).append("\n");
        sb.append(String.format("  - Auto: %d", autoStrategy.get())).append("\n");
        sb.append("\n");
        
        sb.append("Size Information:").append("\n");
        sb.append(String.format("  - Input size: %s", FileUtils.formatFileSize(getTotalInputSize()))).append("\n");
        sb.append(String.format("  - Output size: %s", FileUtils.formatFileSize(getTotalOutputSize()))).append("\n");
        sb.append(String.format("  - Compression ratio: %.2f", getCompressionRatio())).append("\n");
        sb.append("\n");
        
        sb.append("Performance:").append("\n");
        sb.append(String.format("  - Duration: %s", formatDuration(getProcessingDuration()))).append("\n");
        sb.append(String.format("  - Processing rate: %.2f files/sec", getProcessingRate())).append("\n");
        
        return sb.toString();
    }
    
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else if (seconds > 0) {
            return String.format("%d.%03ds", seconds, millis);
        } else {
            return String.format("%dms", millis);
        }
    }
    
    @Override
    public String toString() {
        return getQuickSummary();
    }
}