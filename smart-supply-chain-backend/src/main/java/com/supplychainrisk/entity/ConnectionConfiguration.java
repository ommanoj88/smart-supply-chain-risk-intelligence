package com.supplychainrisk.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class ConnectionConfiguration {
    
    @Column(name = "base_url", length = 255)
    private String baseUrl;
    
    @Column(name = "api_endpoint", length = 255)
    private String apiEndpoint;
    
    @Column(name = "port")
    private Integer port;
    
    @Column(name = "protocol", length = 20)
    private String protocol;
    
    @Column(name = "connection_timeout")
    private Integer connectionTimeout;
    
    @Column(name = "read_timeout")
    private Integer readTimeout;
    
    @Column(name = "max_connections")
    private Integer maxConnections;
    
    @Column(name = "use_ssl")
    private Boolean useSSL;
    
    @Column(name = "proxy_host", length = 255)
    private String proxyHost;
    
    @Column(name = "proxy_port")
    private Integer proxyPort;
    
    @Column(name = "custom_headers", columnDefinition = "TEXT")
    private String customHeaders;
    
    // Default constructor
    public ConnectionConfiguration() {}
    
    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Integer getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public Integer getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public Boolean getUseSSL() {
        return useSSL;
    }
    
    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public Integer getProxyPort() {
        return proxyPort;
    }
    
    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public String getCustomHeaders() {
        return customHeaders;
    }
    
    public void setCustomHeaders(String customHeaders) {
        this.customHeaders = customHeaders;
    }
}