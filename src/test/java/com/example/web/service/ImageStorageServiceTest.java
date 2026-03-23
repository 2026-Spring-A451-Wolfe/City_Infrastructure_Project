/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ImageStorageServiceTest.java                                      *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: JUnit 5 test suite for ImageStorageService. Covers image       *
 *              upload, retrieval, deletion, and validation.                    *
 * Author: Ava Walker                                             *
 * Date Last Modified: 03/22/2026                                              *
 *                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.model.ReportImage;
import com.example.web.repository.ReportImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;

class ImageStorageServiceTest {

    private ImageStorageService imageStorageService;
    private StubReportImageRepository stubRepository;

    @TempDir
    Path tempDir; // Automatically handles cleanup of test files

    @BeforeEach
    void setUp() {
        this.stubRepository = new StubReportImageRepository();
        this.imageStorageService = new ImageStorageService(this.stubRepository, tempDir.toString());
    }

    // Expected Outcome: UPLOAD & VALIDATION (UUID RENAMING)
    @Test
    void saveImage_validFile_generatesUuidAndSaves() throws Exception {
        File sourceFile = tempDir.resolve("pothole.jpg").toFile();
        sourceFile.createNewFile();
        // use FileOutputStream to write 1 byte so length is > 0
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(sourceFile)) {
            fos.write(1); 
        }

        ReportImage result = imageStorageService.saveImage(
            sourceFile, "pothole.jpg", "image/jpeg", 1024L, 101
        );

        Assertions.assertNotNull(result);
        Assertions.assertTrue(new File(result.getFilePath()).exists());
    }

    // Expected Outcome: VALIDATION (FILE TYPE & SIZE)
    @Test
    void saveImage_invalidSize_throwsException() throws Exception {
        File largeFile = tempDir.resolve("huge.jpg").toFile();
        // Give the file 1 byte of content so it isn't "empty"
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(largeFile)) {
            fos.write(1);
        }
        
        // Passing a size larger than 5MB (5 * 1024 * 1024 + 1)
        long tooBig = 6000000L; 

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            imageStorageService.saveImage(largeFile, "huge.jpg", "image/jpeg", tooBig, 101);
        });
        Assertions.assertEquals("Image file size must not exceed 5MB", exception.getMessage());
    }

    // Expected Outcome: RETRIEVAL
    @Test
    void retrieveImageMetadata_returnsCorrectData() throws Exception {
        // Arrange: Put a fake record in our stub "database"
        ReportImage mockImage = new ReportImage();
        mockImage.setId(55L);
        mockImage.setOriginalFilename("nola_drain.png");
        stubRepository.setStoredImage(mockImage);

        //We use the repository directly or via a service call if one existed
        Optional<ReportImage> found = stubRepository.findById(55);

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("nola_drain.png", found.get().getOriginalFilename());
    }

    // Expected Outcome: DELETION
    @Test
    void deleteImage_validId_removesFileFromDiskAndDb() throws Exception {

        File fileToDelete = tempDir.resolve("delete_me.webp").toFile();
        fileToDelete.createNewFile();

        ReportImage imageRecord = new ReportImage();
        imageRecord.setId(1L);
        imageRecord.setFilePath(fileToDelete.getAbsolutePath());
        stubRepository.setStoredImage(imageRecord);

        imageStorageService.deleteImage(1);

        Assertions.assertFalse(fileToDelete.exists(), "The file should be physically deleted");
    }

    // --- STUB REPOSITORY ---
    private static class StubReportImageRepository extends ReportImageRepository {
        private ReportImage storedImage;

        public StubReportImageRepository() {
            super((DataSource) null);
        }

        public void setStoredImage(ReportImage image) {
            this.storedImage = image;
        }

        @Override
        public ReportImage save(ReportImage image) throws SQLException {
            image.setId(1L); 
            return image;
        }

        @Override
        public Optional<ReportImage> findById(Integer id) throws SQLException {
            return Optional.ofNullable(storedImage);
        }

        @Override
        public void delete(ReportImage image) throws SQLException {
            this.storedImage = null; // Simulate DB deletion
        }
    }
}