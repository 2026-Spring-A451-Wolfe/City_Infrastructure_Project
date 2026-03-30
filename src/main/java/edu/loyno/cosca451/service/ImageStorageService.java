/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ImageStorageService.java                                          *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles saving and deleting image files attached to reports.   *
 *              Validates that uploaded files are image/jpeg, image/png, or    *
 *              image/webp and do not exceed 5MB. Renames each file to a UUID  *
 *              before saving to prevent collisions. Works alongside           *
 *              ReportImageRepository to persist image metadata.               *
 * Author: Jana El-Khatib
 *         - Changes: - Added reportImage.setReportId(reportID) before 
 *                    saving — reportId was being passed in 
 *                    but never set on the object, causing it to insert 0 
 *                    and violate the foreign key constraint on report_images  
 * Date Last Modified: 03/20/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.service;

import edu.loyno.cosca451.model.ReportImage;
import edu.loyno.cosca451.repository.ReportImageRepository;

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
    public ReportImage saveImage(File file, String originalFilename, String contentType, long fileSize,
            Integer reportID) throws Exception {

        if (file == null || file.length() == 0) {
            throw new Exception("Image file is required");
        }

        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                        !contentType.equals("image/png") &&
                        !contentType.equals("image/webp"))) {
            throw new Exception("Only JPEG, PNG, and WEBP image files are allowed");
        }

        if (fileSize > 5 * 1024 * 1024) {
            throw new Exception("Image file size must not exceed 5MB");
        }

        String fileExtension = getFileExtension(originalFilename);

        String storedFilename = UUID.randomUUID().toString() + fileExtension;

        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destinationFile = new File(directory, storedFilename);
        if (!file.renameTo(destinationFile)) {
            throw new Exception("Failed to save file");
        }

        ReportImage reportImage = new ReportImage();
        reportImage.setReportId(reportID);
        reportImage.setOriginalFilename(originalFilename);
        reportImage.setStoredFilename(storedFilename);
        reportImage.setContentType(contentType);
        reportImage.setFilePath(destinationFile.getAbsolutePath());
        reportImage.setImageUrl("/uploads/" + storedFilename);
        reportImage.setFileSize(fileSize);

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
