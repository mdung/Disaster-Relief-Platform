package com.relief.media;

import com.relief.entity.Media;
import com.relief.entity.User;
import com.relief.repository.MediaRepository;
import com.relief.repository.UserRepository;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MinioClient minioClient;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${spring.servlet.context-path:/api}")
    private String contextPath;

    public String createPresignedPutUrl(String objectName, String contentType) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .extraQueryParams(Map.of("Content-Type", contentType))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create presigned URL", e);
        }
    }

    public String getPresignedUploadUrl(String objectName, String contentType, String userId) {
        // Add user-specific prefix to object name for organization
        String userObjectName = userId + "/" + objectName;
        return createPresignedPutUrl(userObjectName, contentType);
    }

    @Transactional
    public Media createMediaRecord(CreateMediaRequest request, UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Determine media type from content type or file extension
        String mediaType = determineMediaType(request.getContentType(), request.getFileName());
        
        // Build media URL from object name
        String mediaUrl = buildMediaUrl(request.getObjectName());

        Media.MediaBuilder mediaBuilder = Media.builder()
                .ownerUser(owner)
                .type(mediaType)
                .url(mediaUrl)
                .redacted(false);

        // Set taken_at if provided
        if (request.getTakenAt() != null && !request.getTakenAt().isEmpty()) {
            try {
                LocalDateTime takenAt = LocalDateTime.parse(request.getTakenAt(), 
                    DateTimeFormatter.ISO_DATE_TIME);
                mediaBuilder.takenAt(takenAt);
            } catch (Exception e) {
                // If parsing fails, ignore takenAt
            }
        }

        // Set location if provided
        if (request.getLocation() != null && 
            request.getLocation().getLatitude() != null && 
            request.getLocation().getLongitude() != null) {
            Point point = geometryFactory.createPoint(
                new Coordinate(
                    request.getLocation().getLongitude(),
                    request.getLocation().getLatitude()
                )
            );
            mediaBuilder.geomPoint(point);
        }

        return mediaRepository.save(mediaBuilder.build());
    }

    private String determineMediaType(String contentType, String fileName) {
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            } else if (contentType.startsWith("audio/")) {
                return "audio";
            } else if (contentType.contains("pdf") || 
                      contentType.contains("document") ||
                      contentType.contains("text")) {
                return "document";
            }
        }
        
        if (fileName != null) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (extension.matches("jpg|jpeg|png|gif|webp|bmp|svg")) {
                return "image";
            } else if (extension.matches("mp4|avi|mov|wmv|flv|webm|mkv")) {
                return "video";
            } else if (extension.matches("mp3|wav|ogg|aac|flac")) {
                return "audio";
            } else {
                return "document";
            }
        }
        
        return "document"; // default
    }

    private String buildMediaUrl(String objectName) {
        // Remove /api prefix if present in contextPath
        String basePath = contextPath.equals("/api") ? "" : contextPath.replace("/api", "");
        return basePath + "/media/" + objectName;
    }

    // Inner class for request DTO
    public static class CreateMediaRequest {
        private String objectName;
        private String contentType;
        private String fileName;
        private Long fileSize;
        private String takenAt;
        private LocationData location;

        // Getters and setters
        public String getObjectName() { return objectName; }
        public void setObjectName(String objectName) { this.objectName = objectName; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public String getTakenAt() { return takenAt; }
        public void setTakenAt(String takenAt) { this.takenAt = takenAt; }
        
        public LocationData getLocation() { return location; }
        public void setLocation(LocationData location) { this.location = location; }

        public static class LocationData {
            private Double latitude;
            private Double longitude;

            public Double getLatitude() { return latitude; }
            public void setLatitude(Double latitude) { this.latitude = latitude; }
            
            public Double getLongitude() { return longitude; }
            public void setLongitude(Double longitude) { this.longitude = longitude; }
        }
    }
}



