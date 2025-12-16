package gov.uspto.session.security;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;

public class SessionHijackingPrevention {

    private static final Logger LOG = LoggerFactory.getLogger(SessionHijackingPrevention.class);

    private final SessionHijackingConfig config;

    public SessionHijackingPrevention() {
        this(new SessionHijackingConfig());
    }

    public SessionHijackingPrevention(SessionHijackingConfig config) {
        this.config = Objects.requireNonNull(config, "config cannot be null");
    }

    public ValidationResult validateRequest(Session session, String requestIpAddress, String requestUserAgent) {
        ValidationResult result = new ValidationResult();

        if (config.isIpBindingEnabled() && session.getIpAddress() != null) {
            if (!isIpAddressValid(session.getIpAddress(), requestIpAddress)) {
                LOG.warn("IP address mismatch for session {}: expected={}, actual={}",
                        session.getSessionId(), session.getIpAddress(), requestIpAddress);
                result.addViolation(ReauthReason.IP_ADDRESS_CHANGE, "IP address changed");
            }
        }

        if (config.isUserAgentBindingEnabled() && session.getUserAgent() != null) {
            if (!isUserAgentValid(session.getUserAgent(), requestUserAgent)) {
                LOG.warn("User-Agent mismatch for session {}", session.getSessionId());
                result.addViolation(ReauthReason.USER_AGENT_CHANGE, "User-Agent changed");
            }
        }

        return result;
    }

    private boolean isIpAddressValid(String sessionIp, String requestIp) {
        if (sessionIp == null || requestIp == null) {
            return true;
        }

        if (config.isStrictIpBinding()) {
            return sessionIp.equals(requestIp);
        }

        return isSameSubnet(sessionIp, requestIp);
    }

    private boolean isSameSubnet(String ip1, String ip2) {
        if (ip1 == null || ip2 == null) {
            return false;
        }

        String[] parts1 = ip1.split("\\.");
        String[] parts2 = ip2.split("\\.");

        if (parts1.length != 4 || parts2.length != 4) {
            return ip1.equals(ip2);
        }

        int subnetMaskBits = config.getSubnetMaskBits();
        int fullOctets = subnetMaskBits / 8;

        for (int i = 0; i < fullOctets && i < 4; i++) {
            if (!parts1[i].equals(parts2[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isUserAgentValid(String sessionUserAgent, String requestUserAgent) {
        if (sessionUserAgent == null || requestUserAgent == null) {
            return true;
        }

        if (config.isStrictUserAgentBinding()) {
            return sessionUserAgent.equals(requestUserAgent);
        }

        return extractBrowserFamily(sessionUserAgent).equals(extractBrowserFamily(requestUserAgent));
    }

    private String extractBrowserFamily(String userAgent) {
        if (userAgent == null) {
            return "";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome") && !ua.contains("edg")) {
            return "chrome";
        } else if (ua.contains("firefox")) {
            return "firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "safari";
        } else if (ua.contains("edg")) {
            return "edge";
        } else if (ua.contains("msie") || ua.contains("trident")) {
            return "ie";
        }
        return "other";
    }

    public String generateSessionFingerprint(String ipAddress, String userAgent) {
        StringBuilder sb = new StringBuilder();
        if (ipAddress != null) {
            sb.append(ipAddress);
        }
        sb.append("|");
        if (userAgent != null) {
            sb.append(extractBrowserFamily(userAgent));
        }
        return sb.toString();
    }

    public void bindSession(Session session, String ipAddress, String userAgent) {
        if (config.isIpBindingEnabled()) {
            session.setIpAddress(ipAddress);
        }
        if (config.isUserAgentBindingEnabled()) {
            session.setUserAgent(userAgent);
        }
    }

    public SessionHijackingConfig getConfig() {
        return config;
    }

    public static class SessionHijackingConfig {
        private boolean ipBindingEnabled = true;
        private boolean userAgentBindingEnabled = true;
        private boolean strictIpBinding = false;
        private boolean strictUserAgentBinding = false;
        private int subnetMaskBits = 24;

        public boolean isIpBindingEnabled() {
            return ipBindingEnabled;
        }

        public void setIpBindingEnabled(boolean ipBindingEnabled) {
            this.ipBindingEnabled = ipBindingEnabled;
        }

        public boolean isUserAgentBindingEnabled() {
            return userAgentBindingEnabled;
        }

        public void setUserAgentBindingEnabled(boolean userAgentBindingEnabled) {
            this.userAgentBindingEnabled = userAgentBindingEnabled;
        }

        public boolean isStrictIpBinding() {
            return strictIpBinding;
        }

        public void setStrictIpBinding(boolean strictIpBinding) {
            this.strictIpBinding = strictIpBinding;
        }

        public boolean isStrictUserAgentBinding() {
            return strictUserAgentBinding;
        }

        public void setStrictUserAgentBinding(boolean strictUserAgentBinding) {
            this.strictUserAgentBinding = strictUserAgentBinding;
        }

        public int getSubnetMaskBits() {
            return subnetMaskBits;
        }

        public void setSubnetMaskBits(int subnetMaskBits) {
            this.subnetMaskBits = subnetMaskBits;
        }
    }

    public static class ValidationResult {
        private boolean valid = true;
        private ReauthReason reauthReason;
        private String message;

        public void addViolation(ReauthReason reason, String message) {
            this.valid = false;
            this.reauthReason = reason;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public ReauthReason getReauthReason() {
            return reauthReason;
        }

        public String getMessage() {
            return message;
        }
    }
}
