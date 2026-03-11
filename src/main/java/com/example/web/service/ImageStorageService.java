/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ImageStorageService.java                                          *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles saving and deleting image files attached to reports.   *
 *              Validates that uploaded files are image/jpeg, image/png, or    *
 *              image/webp and do not exceed 5MB. Renames each file to a UUID  *
 *              before saving to prevent collisions. Works alongside           *
 *              ReportImageRepository to persist image metadata.               *
 * Author: Makayla Hairston                                                    *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.model.ReportImage;
import com.example.web.repository.ReportImageRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class ImageStorageService {

    // Repository used to save image metadata in the database
    private final ReportImageRepository reportImageRepository;

    // Directory where image files will be stored
    private final String uploadDirectory;

    public ImageStorageService(ReportImageRepository reportImageRepository, String uploadDirectory) {
        this.reportImageRepository = reportImageRepository;
        this.uploadDirectory = uploadDirectory;
    }

    // Save uploaded image
    public ReportImage saveImage(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new Exception("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") &&
             !contentType.equals("image/png") &&
             !contentType.equals("image/webp"))) {
            throw new Exception("Only JPEG, PNG, and WEBP image files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new Exception("Image file size must not exceed 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        String storedFilename = UUID.randomUUID().toString() + fileExtension;

        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destinationFile = new File(directory, storedFilename);
        file.transferTo(destinationFile);

        ReportImage reportImage = new ReportImage();
        reportImage.setOriginalFilename(originalFilename);
        reportImage.setStoredFilename(storedFilename);
        reportImage.setContentType(contentType);
        reportImage.setFilePath(destinationFile.getAbsolutePath());
        reportImage.setFileSize(file.getSize());

        return reportImageRepository.save(reportImage);
    }

    // Delete saved image
    public void deleteImage(Integer imageId) throws Exception {

        if (imageId == null || imageId <= 0) {
            throw new Exception("Invalid image ID");
        }

        ReportImage reportImage = reportImageRepository.findById(imageId)
            .orElseThrow(() -> new Exception("Image not found"));

        File imageFile = new File(reportImage.getFilePath());
        if (imageFile.exists() && !imageFile.delete()) {
            throw new Exception("Failed to delete image file");
        }

        reportImageRepository.delete(reportImage);
    }

    // Extract file extension from original filename
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}
