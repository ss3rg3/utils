package http;

import com.google.common.net.InternetDomainName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Validates and parses a given string into a URL.<br>
 * - Only allows http & https<br>
 * - TLD parsing with https://publicsuffix.org/ via https://github.com/google/guava/wiki/InternetDomainNameExplained<br>
 * - `co.uk` is not a TLD, but we call it that anyway.<br>
 * - Domain endings (TLDs) are broken down to "registry suffix", e.g. `blogspot.com` = `com` although it's a public suffix in itself<br>
 * - Warning about InternetDomainName as unstable is because it's in eternal @Beta due to the changing public suffix list<br>
 * <br>
 * <b>NOTE:</b> This is relatively slow. Only 200k per second on a single core (2018).
 * <pre>
 * INPUTS:<br>
 * google.com                   - ok. Result: http://google.com
 * http://google.com            - ok
 * http://qwer.google.com       - ok
 * http://asdf.qwer.google.com  - ok
 * google.asdf                  - unknown public suffix
 * ftp://google.com             - disallowed protocol
 * http://google.com/\/         - invalid character
 * </pre>
 * See ParsedUrlTest for more
 */
public class ParsedUrl {

    private static final Pattern startsWithProtocolPattern = Pattern.compile("^(jar|file|ftp|http|https)://.*");
    private static final Pattern ipPattern = Pattern.compile("(localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
    private final String protocol;
    private final String host;
    private final Integer port;
    private final String path;
    private final String query;
    private final String anchor;
    private final String userInfo;

    private final String domain;
    private final String tld;
    private final String url; // Protocol + host + domain


    public ParsedUrl(final String urlCandidate) throws ParsedUrlException {
        if (urlCandidate == null) {
            throw new ParsedUrlException("Provided URL must not be null");
        }

        // If pattern doesn't match we assume it's either a raw domain name or has some disallowed protocol (will fail later)
        String unvalidatedUrlString = urlCandidate.trim();
        if (!startsWithProtocolPattern.matcher(unvalidatedUrlString).matches()) {
            unvalidatedUrlString = "http://" + unvalidatedUrlString;
        }

        // This is used to extract the host. Protocol must be provided, anything else seems to be optional
        final URL url;
        try {
            url = new URL(unvalidatedUrlString);
            this.host = url.getHost();
        } catch (final MalformedURLException e) {
            throw new ParsedUrlException(e.getMessage() + ", provided URL: " + urlCandidate);
        }

        // Domain + TLD
        final String host = url.getHost();
        if (ipPattern.matcher(host).matches()) {
            // Host is an IP or localhost
            this.domain = host.equals("localhost") ? "localhost" : null;
            this.tld = null;
        } else {
            // This extract the actual domain name + tld, without any subdomains (these are included in host)
            final InternetDomainName idn = InternetDomainName.from(url.getHost());
            try {
                this.domain = idn.topDomainUnderRegistrySuffix().toString();
            } catch (final Exception e) {
                throw new ParsedUrlException(e.getMessage() + ", provided URL: " + urlCandidate);
            }

            // This extracts the public suffix (e.g. tld).
            if (!idn.hasRegistrySuffix()) {
                throw new ParsedUrlException("No registry suffix recognized, provided URL: " + urlCandidate);
            }
            this.tld = String.valueOf(idn.registrySuffix());
        }

        // This checks for invalid characters in the URL
        try {
            url.toURI();
        } catch (final Exception e) {
            throw new ParsedUrlException(e.getMessage()); // Shows full URL
        }
        this.path = url.getPath();
        this.query = url.getQuery();
        this.anchor = url.getRef();

        // Check if protocol is http or https. Others are disallowed.
        if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
            throw new ParsedUrlException("Protocol is not http or https, provided URL: " + urlCandidate);
        }
        this.protocol = url.getProtocol();

        // Port -1 means no port provided. Anything below 0 fails via java.net.URL
        final int urlPort = url.getPort();
        if (urlPort < -1 || urlPort == 0 || urlPort > 65535) {
            throw new ParsedUrlException("Port must be between 0 - 65535, provided URL: " + urlCandidate);
        }
        if (urlPort == -1) {
            this.port = null;
        } else {
            this.port = urlPort;
        }

        this.userInfo = url.getUserInfo();

        this.url = this.buildUrl();
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public String getUrl() {
        return this.url;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getPath() {
        return this.path;
    }

    public String getQuery() {
        return this.query;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getTld() {
        return this.tld;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    @Override
    public String toString() {
        return this.url;
    }

    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private String buildUrl() {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.protocol);
        sb.append("://");
        sb.append(this.host);

        if (this.port != null && this.port != -1) {
            sb.append(":");
            sb.append(this.port);
        }

        if (this.path != null) {
            sb.append(this.path);
        }

        if (this.query != null) {
            sb.append("?");
            sb.append(this.query);
        }

        if (this.anchor != null) {
            sb.append("#");
            sb.append(this.anchor);
        }

        return sb.toString();
    }


    // ------------------------------------------------------------------------------------------ //
    // INNER OBJECTS
    // ------------------------------------------------------------------------------------------ //

    public static class ParsedUrlException extends Exception {

        public ParsedUrlException(final String s) {
            super(s);
        }

    }

}
