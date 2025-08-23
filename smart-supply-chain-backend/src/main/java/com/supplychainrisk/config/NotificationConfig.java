package com.supplychainrisk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationConfig {
    
    private Email email = new Email();
    private Sms sms = new Sms();
    private Slack slack = new Slack();
    private Push push = new Push();
    private Template template = new Template();
    private Retry retry = new Retry();
    private Delivery delivery = new Delivery();
    
    // Getters and Setters
    public Email getEmail() {
        return email;
    }
    
    public void setEmail(Email email) {
        this.email = email;
    }
    
    public Sms getSms() {
        return sms;
    }
    
    public void setSms(Sms sms) {
        this.sms = sms;
    }
    
    public Slack getSlack() {
        return slack;
    }
    
    public void setSlack(Slack slack) {
        this.slack = slack;
    }
    
    public Push getPush() {
        return push;
    }
    
    public void setPush(Push push) {
        this.push = push;
    }
    
    public Template getTemplate() {
        return template;
    }
    
    public void setTemplate(Template template) {
        this.template = template;
    }
    
    public Retry getRetry() {
        return retry;
    }
    
    public void setRetry(Retry retry) {
        this.retry = retry;
    }
    
    public Delivery getDelivery() {
        return delivery;
    }
    
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
    
    // Nested Configuration Classes
    public static class Email {
        private boolean enabled = true;
        private String defaultProvider = "smtp";
        private String from = "notifications@supplychainrisk.com";
        private String fromName = "Supply Chain Risk Intelligence";
        private Aws aws = new Aws();
        private Sendgrid sendgrid = new Sendgrid();
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getDefaultProvider() {
            return defaultProvider;
        }
        
        public void setDefaultProvider(String defaultProvider) {
            this.defaultProvider = defaultProvider;
        }
        
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getFromName() {
            return fromName;
        }
        
        public void setFromName(String fromName) {
            this.fromName = fromName;
        }
        
        public Aws getAws() {
            return aws;
        }
        
        public void setAws(Aws aws) {
            this.aws = aws;
        }
        
        public Sendgrid getSendgrid() {
            return sendgrid;
        }
        
        public void setSendgrid(Sendgrid sendgrid) {
            this.sendgrid = sendgrid;
        }
        
        public static class Aws {
            private boolean enabled = false;
            private String region = "us-east-1";
            private String accessKey;
            private String secretKey;
            
            // Getters and Setters
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
            
            public String getRegion() {
                return region;
            }
            
            public void setRegion(String region) {
                this.region = region;
            }
            
            public String getAccessKey() {
                return accessKey;
            }
            
            public void setAccessKey(String accessKey) {
                this.accessKey = accessKey;
            }
            
            public String getSecretKey() {
                return secretKey;
            }
            
            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }
        }
        
        public static class Sendgrid {
            private boolean enabled = false;
            private String apiKey;
            
            // Getters and Setters
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
            
            public String getApiKey() {
                return apiKey;
            }
            
            public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
            }
        }
    }
    
    public static class Sms {
        private boolean enabled = true;
        private String provider = "twilio";
        private Twilio twilio = new Twilio();
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getProvider() {
            return provider;
        }
        
        public void setProvider(String provider) {
            this.provider = provider;
        }
        
        public Twilio getTwilio() {
            return twilio;
        }
        
        public void setTwilio(Twilio twilio) {
            this.twilio = twilio;
        }
        
        public static class Twilio {
            private String accountSid;
            private String authToken;
            private String fromNumber;
            
            // Getters and Setters
            public String getAccountSid() {
                return accountSid;
            }
            
            public void setAccountSid(String accountSid) {
                this.accountSid = accountSid;
            }
            
            public String getAuthToken() {
                return authToken;
            }
            
            public void setAuthToken(String authToken) {
                this.authToken = authToken;
            }
            
            public String getFromNumber() {
                return fromNumber;
            }
            
            public void setFromNumber(String fromNumber) {
                this.fromNumber = fromNumber;
            }
        }
    }
    
    public static class Slack {
        private boolean enabled = true;
        private String botToken;
        private String webhookUrl;
        private String defaultChannel = "#supply-chain-alerts";
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getBotToken() {
            return botToken;
        }
        
        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }
        
        public String getWebhookUrl() {
            return webhookUrl;
        }
        
        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }
        
        public String getDefaultChannel() {
            return defaultChannel;
        }
        
        public void setDefaultChannel(String defaultChannel) {
            this.defaultChannel = defaultChannel;
        }
    }
    
    public static class Push {
        private boolean enabled = true;
        private Firebase firebase = new Firebase();
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Firebase getFirebase() {
            return firebase;
        }
        
        public void setFirebase(Firebase firebase) {
            this.firebase = firebase;
        }
        
        public static class Firebase {
            private boolean enabled = true;
            
            // Getters and Setters
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
    
    public static class Template {
        private String engine = "freemarker";
        private boolean cacheEnabled = true;
        private String defaultLocale = "en";
        
        // Getters and Setters
        public String getEngine() {
            return engine;
        }
        
        public void setEngine(String engine) {
            this.engine = engine;
        }
        
        public boolean isCacheEnabled() {
            return cacheEnabled;
        }
        
        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }
        
        public String getDefaultLocale() {
            return defaultLocale;
        }
        
        public void setDefaultLocale(String defaultLocale) {
            this.defaultLocale = defaultLocale;
        }
    }
    
    public static class Retry {
        private boolean enabled = true;
        private int maxAttempts = 3;
        private int delaySeconds = 30;
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getMaxAttempts() {
            return maxAttempts;
        }
        
        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }
        
        public int getDelaySeconds() {
            return delaySeconds;
        }
        
        public void setDelaySeconds(int delaySeconds) {
            this.delaySeconds = delaySeconds;
        }
    }
    
    public static class Delivery {
        private boolean trackingEnabled = true;
        private int retentionDays = 30;
        
        // Getters and Setters
        public boolean isTrackingEnabled() {
            return trackingEnabled;
        }
        
        public void setTrackingEnabled(boolean trackingEnabled) {
            this.trackingEnabled = trackingEnabled;
        }
        
        public int getRetentionDays() {
            return retentionDays;
        }
        
        public void setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
        }
    }
}