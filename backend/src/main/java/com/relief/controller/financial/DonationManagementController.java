package com.relief.controller.financial;

import com.relief.service.financial.DonationManagementService;
import com.relief.service.financial.DonationManagementService.Donation;
import com.relief.service.financial.DonationManagementService.Donor;
import com.relief.service.financial.DonationManagementService.DonationSummary;
import com.relief.service.financial.DonationManagementService.DonationAnalytics;
import com.relief.service.financial.DonationManagementService.DonationType;
import com.relief.service.financial.DonationManagementService.DonationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Donation management controller
 */
@RestController
@RequestMapping("/api/donation-management")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Donation Management", description = "Donation tracking and management APIs")
public class DonationManagementController {

    private final DonationManagementService donationManagementService;

    @PostMapping("/donations")
    @Operation(summary = "Record a new donation")
    public ResponseEntity<Donation> recordDonation(
            @RequestBody RecordDonationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        
        UUID userId = UUID.fromString(principal.getUsername());
        
        Donation donation = donationManagementService.recordDonation(
            request.getDonorId(),
            request.getAmount(),
            request.getType(),
            request.getDescription(),
            request.getCampaignId(),
            userId,
            request.getReferenceId()
        );
        
        return ResponseEntity.ok(donation);
    }

    @PostMapping("/donors")
    @Operation(summary = "Register a new donor")
    public ResponseEntity<Donor> registerDonor(
            @RequestBody RegisterDonorRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        
        UUID userId = UUID.fromString(principal.getUsername());
        
        Donor donor = donationManagementService.registerDonor(
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getOrganization(),
            request.getType(),
            userId
        );
        
        return ResponseEntity.ok(donor);
    }

    @GetMapping("/donations/{donationId}")
    @Operation(summary = "Get donation details")
    public ResponseEntity<Donation> getDonation(@PathVariable String donationId) {
        Donation donation = donationManagementService.getDonation(donationId);
        return ResponseEntity.ok(donation);
    }

    @GetMapping("/donors/{donorId}")
    @Operation(summary = "Get donor details")
    public ResponseEntity<Donor> getDonor(@PathVariable String donorId) {
        Donor donor = donationManagementService.getDonor(donorId);
        return ResponseEntity.ok(donor);
    }

    @GetMapping("/donations")
    @Operation(summary = "Get donations with filters")
    public ResponseEntity<List<Donation>> getDonations(
            @RequestParam(required = false) String donorId,
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) DonationType type,
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<Donation> donations = donationManagementService.getDonations(
            donorId, campaignId, type, status, limit
        );
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/donors")
    @Operation(summary = "Get donors with filters")
    public ResponseEntity<List<Donor>> getDonors(
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<Donor> donors = donationManagementService.getDonors(organization, type, limit);
        return ResponseEntity.ok(donors);
    }

    @PutMapping("/donations/{donationId}/status")
    @Operation(summary = "Update donation status")
    public ResponseEntity<Donation> updateDonationStatus(
            @PathVariable String donationId,
            @RequestBody UpdateDonationStatusRequest request) {
        
        Donation donation = donationManagementService.updateDonationStatus(
            donationId, request.getStatus(), request.getNotes()
        );
        
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/donors/{donorId}")
    @Operation(summary = "Update donor information")
    public ResponseEntity<Donor> updateDonor(
            @PathVariable String donorId,
            @RequestBody UpdateDonorRequest request) {
        
        Donor donor = donationManagementService.updateDonor(
            donorId,
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getOrganization()
        );
        
        return ResponseEntity.ok(donor);
    }

    @GetMapping("/campaigns/{campaignId}/donations")
    @Operation(summary = "Get donations for a campaign")
    public ResponseEntity<List<Donation>> getCampaignDonations(
            @PathVariable String campaignId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<Donation> donations = donationManagementService.getCampaignDonations(campaignId, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/donors/{donorId}/donations")
    @Operation(summary = "Get donations by donor")
    public ResponseEntity<List<Donation>> getDonorDonations(
            @PathVariable String donorId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<Donation> donations = donationManagementService.getDonorDonations(donorId, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get donation summary")
    public ResponseEntity<DonationSummary> getDonationSummary(
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        DonationSummary summary = donationManagementService.getDonationSummary(
            campaignId, startDate, endDate
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get donation analytics")
    public ResponseEntity<DonationAnalytics> getDonationAnalytics(
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        DonationAnalytics analytics = donationManagementService.getDonationAnalytics(
            campaignId, startDate, endDate
        );
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/donors/{donorId}/summary")
    @Operation(summary = "Get donor summary")
    public ResponseEntity<DonationSummary> getDonorSummary(@PathVariable String donorId) {
        DonationSummary summary = donationManagementService.getDonorSummary(donorId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/donations/{donationId}/refund")
    @Operation(summary = "Process donation refund")
    public ResponseEntity<Donation> processRefund(
            @PathVariable String donationId,
            @RequestBody ProcessRefundRequest request) {
        
        Donation donation = donationManagementService.processRefund(
            donationId, request.getAmount(), request.getReason()
        );
        
        return ResponseEntity.ok(donation);
    }

    // Request DTOs
    public static class RecordDonationRequest {
        private String donorId;
        private BigDecimal amount;
        private DonationType type;
        private String description;
        private String campaignId;
        private String referenceId;

        // Getters and setters
        public String getDonorId() { return donorId; }
        public void setDonorId(String donorId) { this.donorId = donorId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public DonationType getType() { return type; }
        public void setType(DonationType type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCampaignId() { return campaignId; }
        public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    }

    public static class RegisterDonorRequest {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String organization;
        private String type;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class UpdateDonationStatusRequest {
        private DonationStatus status;
        private String notes;

        // Getters and setters
        public DonationStatus getStatus() { return status; }
        public void setStatus(DonationStatus status) { this.status = status; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class UpdateDonorRequest {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String organization;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }
    }

    public static class ProcessRefundRequest {
        private BigDecimal amount;
        private String reason;

        // Getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}


