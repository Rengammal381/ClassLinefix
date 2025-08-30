package com.killer.perfectlinerestorer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * File Utilities
 * 
 * This class provides utility methods for file operations used throughout
 * the line number restoration process.
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    
    /**
     * Find all files matching the given predicate in a directory tree
     */
    public static List<Path> findFiles(Path rootDir, Predicate<Path> filter) throws IOException {
        List<Path> result = new ArrayList<>();
        
        if (!Files.exists(rootDir) || !Files.isDirectory(rootDir)) {
            logger.warn("Directory does not exist or is not a directory: {}", rootDir);
            return result;
        }
        
        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (filter.test(file)) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("Failed to visit file: {} - {}", file, exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });
        
        return result;
    }
    
    /**
     * Find all JAR files in a directory tree
     */
    public static List<Path> findJarFiles(Path rootDir) throws IOException {
        return findFiles(rootDir, path -> path.toString().toLowerCase().endsWith(".jar"));
    }
    
    /**
     * Find all CLASS files in a directory tree
     */
    public static List<Path> findClassFiles(Path rootDir) throws IOException {
        return findFiles(rootDir, path -> path.toString().toLowerCase().endsWith(".class"));
    }
    
    /**
     * Find all Java-related files (JAR and CLASS) in a directory tree
     */
    public static List<Path> findJavaFiles(Path rootDir) throws IOException {
        return findFiles(rootDir, path -> {
            String fileName = path.toString().toLowerCase();
            return fileName.endsWith(".jar") || fileName.endsWith(".class");
        });
    }
    
    /**
     * Create directory if it doesn't exist
     */
    public static void ensureDirectoryExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            logger.debug("Created directory: {}", directory);
        } else if (!Files.isDirectory(directory)) {
            throw new IOException("Path exists but is not a directory: " + directory);
        }
    }
    
    /**
     * Copy file with error handling
     */
    public static void copyFile(Path source, Path target) throws IOException {
        ensureDirectoryExists(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, 
                  StandardCopyOption.COPY_ATTRIBUTES);
        logger.debug("Copied file: {} -> {}", source, target);
    }
    
    /**
     * Get file extension (without dot)
     */
    public static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }
    
    /**
     * Get file name without extension
     */
    public static String getFileNameWithoutExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }
    
    /**
     * Check if a file is a Java bytecode file (JAR or CLASS)
     */
    public static boolean isJavaFile(Path filePath) {
        String extension = getFileExtension(filePath);
        return "jar".equals(extension) || "class".equals(extension);
    }
    
    /**
     * Check if a file is a JAR file
     */
    public static boolean isJarFile(Path filePath) {
        return "jar".equals(getFileExtension(filePath));
    }
    
    /**
     * Check if a file is a CLASS file
     */
    public static boolean isClassFile(Path filePath) {
        return "class".equals(getFileExtension(filePath));
    }
    
    /**
     * Format file size in human-readable format
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Get relative path from base directory
     */
    public static Path getRelativePath(Path basePath, Path targetPath) {
        try {
            return basePath.relativize(targetPath);
        } catch (IllegalArgumentException e) {
            // Paths are not relative to each other
            return targetPath;
        }
    }
    
    /**
     * Check if a directory is empty
     */
    public static boolean isDirectoryEmpty(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return true;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            return !stream.iterator().hasNext();
        }
    }
    
    /**
     * Delete directory recursively
     */
    public static void deleteDirectoryRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.debug("Deleted directory recursively: {}", directory);
    }
    
    /**
     * Count files in directory tree
     */
    public static FileCount countFiles(Path rootDir) throws IOException {
        FileCount count = new FileCount();
        
        if (!Files.exists(rootDir) || !Files.isDirectory(rootDir)) {
            return count;
        }
        
        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                count.totalFiles++;
                count.totalSize += attrs.size();
                
                String extension = getFileExtension(file);
                switch (extension) {
                    case "jar":
                        count.jarFiles++;
                        break;
                    case "class":
                        count.classFiles++;
                        break;
                    default:
                        count.otherFiles++;
                        break;
                }
                
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                count.directories++;
                return FileVisitResult.CONTINUE;
            }
        });
        
        return count;
    }
    
    /**
     * File count statistics
     */
    public static class FileCount {
        public int totalFiles = 0;
        public int jarFiles = 0;
        public int classFiles = 0;
        public int otherFiles = 0;
        public int directories = 0;
        public long totalSize = 0;
        
        @Override
        public String toString() {
            return String.format("FileCount{total=%d, jars=%d, classes=%d, others=%d, dirs=%d, size=%s}", 
                               totalFiles, jarFiles, classFiles, otherFiles, directories, 
                               formatFileSize(totalSize));
        }
    }
    
    /**
     * Validate that a path is safe (no directory traversal attacks)
     */
    public static boolean isSafePath(Path basePath, Path targetPath) {
        try {
            Path normalizedBase = basePath.normalize().toAbsolutePath();
            Path normalizedTarget = targetPath.normalize().toAbsolutePath();
            return normalizedTarget.startsWith(normalizedBase);
        } catch (Exception e) {
            logger.warn("Path validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Create a backup of a file
     */
    public static Path createBackup(Path originalFile) throws IOException {
        Path backupFile = Paths.get(originalFile.toString() + ".backup");
        Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Created backup: {} -> {}", originalFile, backupFile);
        return backupFile;
    }
    
    /**
     * Restore a file from backup
     */
    public static void restoreFromBackup(Path backupFile, Path targetFile) throws IOException {
        if (!Files.exists(backupFile)) {
            throw new IOException("Backup file does not exist: " + backupFile);
        }
        
        Files.copy(backupFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Restored from backup: {} -> {}", backupFile, targetFile);
    }
}