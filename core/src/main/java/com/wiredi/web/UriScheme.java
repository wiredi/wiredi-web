package com.wiredi.web;

/**
 * Represents common URI schemes as defined by IANA.
 * <p>
 * This enum provides constants for the most commonly used URI schemes,
 * making it easier to create URIs without typos in the scheme part.
 * <p>
 * Example usage:
 * <pre>{@code
 * URI uri = UriBuilder.prepare()
 *     .scheme(UriScheme.HTTPS)
 *     .host("example.com")
 *     .build();
 * }</pre>
 * 
 * @see <a href="https://www.iana.org/assignments/uri-schemes/uri-schemes.xhtml">IANA URI Schemes</a>
 * @see UriBuilder#scheme(UriScheme)
 */
public enum UriScheme {
    
    /**
     * The Hypertext Transfer Protocol scheme (http://)
     */
    HTTP("http"),
    
    /**
     * The Hypertext Transfer Protocol Secure scheme (https://)
     */
    HTTPS("https"),
    
    /**
     * The File Transfer Protocol scheme (ftp://)
     */
    FTP("ftp"),
    
    /**
     * The File Transfer Protocol Secure scheme (ftps://)
     */
    FTPS("ftps"),
    
    /**
     * The Secure File Transfer Protocol scheme (sftp://)
     */
    SFTP("sftp"),
    
    /**
     * The File scheme for local files (file://)
     */
    FILE("file"),
    
    /**
     * The mailto scheme for email addresses (mailto:)
     */
    MAILTO("mailto"),
    
    /**
     * The tel scheme for telephone numbers (tel:)
     */
    TEL("tel"),
    
    /**
     * The WebSocket scheme (ws://)
     */
    WS("ws"),
    
    /**
     * The WebSocket Secure scheme (wss://)
     */
    WSS("wss"),
    
    /**
     * The LDAP scheme (ldap://)
     */
    LDAP("ldap"),
    
    /**
     * The LDAP Secure scheme (ldaps://)
     */
    LDAPS("ldaps"),
    
    /**
     * The Data scheme for embedding small data items inline (data:)
     */
    DATA("data");
    
    private final String value;
    
    UriScheme(String value) {
        this.value = value;
    }
    
    /**
     * Returns the string representation of this scheme.
     *
     * @return the scheme name as a string
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Attempts to convert a string to a UriScheme enum value.
     * <p>
     * The comparison is case-insensitive.
     *
     * @param scheme the scheme string to convert
     * @return the matching UriScheme or null if no match is found
     */
    public static UriScheme fromString(String scheme) {
        if (scheme == null || scheme.isEmpty()) {
            return null;
        }
        
        for (UriScheme uriScheme : values()) {
            if (uriScheme.value.equalsIgnoreCase(scheme)) {
                return uriScheme;
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
