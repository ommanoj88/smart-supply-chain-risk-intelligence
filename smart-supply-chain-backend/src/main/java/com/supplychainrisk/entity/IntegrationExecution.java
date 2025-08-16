package com.supplychainrisk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "integration_executions")
public class IntegrationExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "erp_integration_id")
    private ERPIntegration erpIntegration;
    
    // Execution Details
    @Column(name = "execution_id", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Execution ID is required")
    private String executionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type", nullable = false)
    private ExecutionType executionType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status = ExecutionStatus.PENDING;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    // Data Processing Information
    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;
    
    @Column(name = "records_success")
    private Integer recordsSuccess = 0;
    
    @Column(name = "records_error")
    private Integer recordsError = 0;
    
    @Column(name = "records_skipped")
    private Integer recordsSkipped = 0;
    
    // Error Information
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "error_code", length = 50)
    private String errorCode;
    
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;
    
    // Performance Data
    @Column(name = "throughput_records_per_second")
    private Double throughputRecordsPerSecond;
    
    @Column(name = "memory_usage_mb")
    private Long memoryUsageMB;
    
    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;
    
    // Execution Context
    @Column(name = "execution_context", columnDefinition = "TEXT") // JSON format
    private String executionContext;
    
    // Result Data
    @Column(name = "execution_result", columnDefinition = "TEXT") // JSON format
    private String executionResult;
    
    // Retry Information
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;
    
    // Metadata
    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;
    
    @Column(name = "execution_environment", length = 50)
    private String executionEnvironment;
    
    // Enums
    public enum ExecutionType {
        MANUAL, SCHEDULED, REAL_TIME, RETRY, BATCH
    }
    
    public enum ExecutionStatus {
        PENDING, RUNNING, SUCCESS, FAILED, CANCELLED, RETRY
    }
    
    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }
    
    // Default constructor
    public IntegrationExecution() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ERPIntegration getErpIntegration() {
        return erpIntegration;
    }
    
    public void setErpIntegration(ERPIntegration erpIntegration) {
        this.erpIntegration = erpIntegration;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public ExecutionType getExecutionType() {
        return executionType;
    }
    
    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }
    
    public ExecutionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }
    
    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
    
    public Integer getRecordsProcessed() {
        return recordsProcessed;
    }
    
    public void setRecordsProcessed(Integer recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }
    
    public Integer getRecordsSuccess() {
        return recordsSuccess;
    }
    
    public void setRecordsSuccess(Integer recordsSuccess) {
        this.recordsSuccess = recordsSuccess;
    }
    
    public Integer getRecordsError() {
        return recordsError;
    }
    
    public void setRecordsError(Integer recordsError) {
        this.recordsError = recordsError;
    }
    
    public Integer getRecordsSkipped() {
        return recordsSkipped;
    }
    
    public void setRecordsSkipped(Integer recordsSkipped) {
        this.recordsSkipped = recordsSkipped;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
    
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
    
    public Double getThroughputRecordsPerSecond() {
        return throughputRecordsPerSecond;
    }
    
    public void setThroughputRecordsPerSecond(Double throughputRecordsPerSecond) {
        this.throughputRecordsPerSecond = throughputRecordsPerSecond;
    }
    
    public Long getMemoryUsageMB() {
        return memoryUsageMB;
    }
    
    public void setMemoryUsageMB(Long memoryUsageMB) {
        this.memoryUsageMB = memoryUsageMB;
    }
    
    public Double getCpuUsagePercent() {
        return cpuUsagePercent;
    }
    
    public void setCpuUsagePercent(Double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }
    
    public String getExecutionContext() {
        return executionContext;
    }
    
    public void setExecutionContext(String executionContext) {
        this.executionContext = executionContext;
    }
    
    public String getExecutionResult() {
        return executionResult;
    }
    
    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public LocalDateTime getNextRetryTime() {
        return nextRetryTime;
    }
    
    public void setNextRetryTime(LocalDateTime nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }
    
    public String getTriggeredBy() {
        return triggeredBy;
    }
    
    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }
    
    public String getExecutionEnvironment() {
        return executionEnvironment;
    }
    
    public void setExecutionEnvironment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }
}