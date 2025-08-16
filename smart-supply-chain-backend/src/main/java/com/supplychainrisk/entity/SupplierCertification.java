package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "supplier_certifications")
public class SupplierCertification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;
    
    @Column(name = "certification_type", nullable = false, length = 50)
    @NotBlank(message = "Certification type is required")
    private String certificationType;
    
    @Column(name = "certification_name", nullable = false, length = 100)
    @NotBlank(message = "Certification name is required")
    private String certificationName;
    
    @Column(name = "certification_number", length = 100)
    private String certificationNumber;
    
    @Column(name = "issuing_authority", length = 100)
    private String issuingAuthority;
    
    @Column(name = "issue_date")
    private LocalDate issueDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;
    
    @Column(name = "document_url")
    private String documentUrl;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public enum Status {
        ACTIVE, EXPIRED, SUSPENDED, PENDING_RENEWAL
    }
    
    // Default constructor
    public SupplierCertification() {}
    
    // Constructor with required fields
    public SupplierCertification(Supplier supplier, String certificationType, String certificationName) {
        this.supplier = supplier;
        this.certificationType = certificationType;
        this.certificationName = certificationName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public String getCertificationType() {
        return certificationType;
    }
    
    public void setCertificationType(String certificationType) {
        this.certificationType = certificationType;
    }
    
    public String getCertificationName() {
        return certificationName;
    }
    
    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }
    
    public String getCertificationNumber() {
        return certificationNumber;
    }
    
    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }
    
    public String getIssuingAuthority() {
        return issuingAuthority;
    }
    
    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }
    
    public LocalDate getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}