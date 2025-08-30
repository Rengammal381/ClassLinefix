package com.killer.perfectlinerestorer;

import com.killer.perfectlinerestorer.core.LineNumberRestorer;
import com.killer.perfectlinerestorer.core.RestoreStrategy;
import com.killer.perfectlinerestorer.processor.FileProcessor;
import com.killer.perfectlinerestorer.processor.JarProcessor;
import com.killer.perfectlinerestorer.processor.ClassProcessor;
import com.killer.perfectlinerestorer.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Perfect Line Restorer - A professional Java bytecode line number restoration tool
 * 
 * This tool provides multiple advanced restoration strategies:
 * - Sequential numbering: Classic line number restoration
 * - Exception-oriented: Lightweight restoration approach
 * - Intelligent analysis: Advanced semantic analysis
 */
public class PerfectLineRestorer {
    private static final Logger logger = LoggerFactory.getLogger(PerfectLineRestorer.class);
    
    private final Main.CommandLineConfig config;
    private final LineNumberRestorer restorer;
    private final AtomicInteger processedFiles = new AtomicInteger(0);
    private final AtomicInteger skippedFiles = new AtomicInteger(0);
    private final AtomicLong totalBytes = new AtomicLong(0);
    
    public PerfectLineRestorer(Main.CommandLineConfig config) {
        this.config = config;
        this.restorer = new LineNumberRestorer(config);
    }
    
    /**
     * Main processing method
     */
    public void process() throws IOException {
        logger.info("Starting line number restoration process...");
        long startTime = System.currentTimeMillis();
        
        Path inputPath = Paths.get(config.getInputDir());
        Path outputPath = Paths.get(config.getOutputDir());
        
        // Check if input is a JAR file or directory
        if (inputPath.toString().toLowerCase().endsWith(".jar")) {
            // Process single JAR file
            processJarFile(inputPath, outputPath);
        } else {
            // Walk through all files in input directory
            Files.walkFileTree(inputPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Create corresponding directory in output
                Path relativePath = inputPath.relativize(dir);
                Path outputDir = outputPath.resolve(relativePath);
                
                if (!Files.exists(outputDir)) {
                    Files.createDirectories(outputDir);
                    logger.debug("Created directory: {}", outputDir);
                }
                
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                processFile(file, inputPath, outputPath);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("Failed to visit file: {} - {}", file, exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    logger.warn("Error visiting directory: {} - {}", dir, exc.getMessage());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Print summary
        logger.info("Processing completed in {} ms", duration);
        logger.info("Files processed: {}", processedFiles.get());
        logger.info("Files skipped (already have line numbers): {}", skippedFiles.get());
        logger.info("Total bytes processed: {} bytes ({} MB)", 
                   totalBytes.get(), totalBytes.get() / (1024 * 1024));
    }
    
    /**
     * Process a single file
     */
    private void processFile(Path file, Path inputRoot, Path outputRoot) {
        try {
            String fileName = file.getFileName().toString().toLowerCase();
            Path relativePath = inputRoot.relativize(file);
            Path outputFile = outputRoot.resolve(relativePath);
            
            // Ensure output directory exists
            Files.createDirectories(outputFile.getParent());
            
            if (fileName.endsWith(".jar")) {
                processJarFile(file, outputFile);
            } else if (fileName.endsWith(".class")) {
                processClassFile(file, outputFile);
            } else {
                // Copy other files as-is
                copyFile(file, outputFile);
            }
            
        } catch (Exception e) {
            logger.error("Error processing file {}: {}", file, e.getMessage(), e);
        }
    }
    
    /**
     * Process JAR file
     */
    private void processJarFile(Path inputJar, Path outputJar) throws IOException {
        logger.info("Processing JAR file: {}", inputJar.getFileName());
        
        JarProcessor processor = new JarProcessor(restorer);
        boolean modified = processor.processJar(inputJar, outputJar, config);
        
        if (modified) {
            processedFiles.incrementAndGet();
            totalBytes.addAndGet(Files.size(outputJar));
            logger.info("JAR file processed: {} -> {}", inputJar.getFileName(), outputJar.getFileName());
        } else {
            skippedFiles.incrementAndGet();
            logger.info("JAR file skipped (no modifications needed): {}", inputJar.getFileName());
        }
    }
    
    /**
     * Process CLASS file
     */
    private void processClassFile(Path inputClass, Path outputClass) throws IOException {
        logger.debug("Processing CLASS file: {}", inputClass.getFileName());
        
        ClassProcessor processor = new ClassProcessor(restorer);
        boolean modified = processor.processClass(inputClass, outputClass, config);
        
        if (modified) {
            processedFiles.incrementAndGet();
            totalBytes.addAndGet(Files.size(outputClass));
            logger.debug("CLASS file processed: {} -> {}", inputClass.getFileName(), outputClass.getFileName());
        } else {
            skippedFiles.incrementAndGet();
            logger.debug("CLASS file skipped (already has line numbers): {}", inputClass.getFileName());
        }
    }
    
    /**
     * Copy non-Java files as-is
     */
    private void copyFile(Path source, Path target) throws IOException {
        if (!Files.exists(target) || Files.size(source) != Files.size(target) || 
            Files.getLastModifiedTime(source).compareTo(Files.getLastModifiedTime(target)) > 0) {
            
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.COPY_ATTRIBUTES);
            logger.debug("Copied file: {} -> {}", source.getFileName(), target.getFileName());
        }
    }
}