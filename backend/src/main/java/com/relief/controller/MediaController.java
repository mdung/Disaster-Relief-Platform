package com.relief.controller;

import com.relief.entity.Media;
import com.relief.media.MediaService;
import com.relief.repository.UserRepository;
import com.relief.security.RequiresPermission;
import com.relief.security.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/media")
@Tag(name = "Media", description = "Media upload and management endpoints")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create media record after file upload")
    @RequiresPermission(Permission.MEDIA_UPLOAD)
    public ResponseEntity<Map<String, Object>> createMediaRecord(
            @RequestBody MediaService.CreateMediaRequest request,
            Authentication authentication) {
        
        try {
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            var user = userRepository.findByEmail(principal.getUsername())
                    .orElseGet(() -> userRepository.findByPhone(principal.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found")));
            
            Media media = mediaService.createMediaRecord(request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "id", media.getId().toString(),
                "url", media.getUrl(),
                "objectName", request.getObjectName(),
                "type", media.getType(),
                "success", true
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to create media record",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload media file")
    @RequiresPermission(Permission.MEDIA_UPLOAD)
    public ResponseEntity<Map<String, Object>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        
        try {
            String userId = authentication.getName();
            String objectName = userId + "/" + file.getOriginalFilename();
            String presignedUrl = mediaService.getPresignedUploadUrl(objectName, file.getContentType(), userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "uploadUrl", presignedUrl,
                "objectName", objectName,
                "originalFilename", file.getOriginalFilename(),
                "contentType", file.getContentType(),
                "message", "Presigned URL generated successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Upload failed",
                "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/presign")
    @Operation(summary = "Get presigned URL for direct upload to MinIO")
    @RequiresPermission(Permission.MEDIA_UPLOAD)
    public ResponseEntity<Map<String, String>> getPresignedUploadUrl(
            @RequestParam String objectName,
            @RequestParam String contentType,
            Authentication authentication) {
        
        try {
            String userId = authentication.getName();
            String presignedUrl = mediaService.getPresignedUploadUrl(objectName, contentType, userId);
            
            return ResponseEntity.ok(Map.of(
                "uploadUrl", presignedUrl,
                "objectName", objectName,
                "contentType", contentType
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate presigned URL",
                "message", e.getMessage()
            ));
        }
    }

}