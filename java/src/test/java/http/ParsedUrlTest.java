package http;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class ParsedUrlTest {

    @Test
    public void invalid_inputs() {
        assertThat(this.tryToCreateHttpUrl(null),
                is("Provided URL must not be null"));

        assertThat(this.tryToCreateHttpUrl("http://com"),
                is("Not under a registry suffix: com, provided URL: http://com"));

        assertThat(this.tryToCreateHttpUrl("google"),
                is("Not under a registry suffix: google, provided URL: google"));

        assertThat(this.tryToCreateHttpUrl("google.asdf"),
                is("Not under a registry suffix: google.asdf, provided URL: google.asdf"));

        assertThat(this.tryToCreateHttpUrl("http://google.asdf"),
                is("Not under a registry suffix: google.asdf, provided URL: http://google.asdf"));

        assertThat(this.tryToCreateHttpUrl("httxp://google.asdf"),
                is("Not under a registry suffix: httxp, provided URL: httxp://google.asdf"));

        assertThat(this.tryToCreateHttpUrl("google.com/\\"),
                is("Illegal character in path at index 18: http://google.com/\\"));

        assertThat(this.tryToCreateHttpUrl("google.com/ /"),
                is("Illegal character in path at index 18: http://google.com/ /"));

        assertThat(this.tryToCreateHttpUrl("ftp://google.com"),
                is("Protocol is not http or https, provided URL: ftp://google.com"));

        assertThat(this.tryToCreateHttpUrl("http://google.com:65536"),
                is("Port must be between 0 - 65535, provided URL: http://google.com:65536"));

        assertThat(this.tryToCreateHttpUrl("http://google.com:-2"),
                is("Invalid port number :-2, provided URL: http://google.com:-2"));

        assertThat(this.tryToCreateHttpUrl("http://google.com:0"),
                is("Port must be between 0 - 65535, provided URL: http://google.com:0"));
    }

    @Test
    public void valid_inputs() throws Exception {

        // PROTOCOL
        ParsedUrl httpUrl = new ParsedUrl("google.com");
        assertThat(httpUrl.getUrl(), is("http://google.com"));
        assertThat(httpUrl.getProtocol(), is("http"));

        httpUrl = new ParsedUrl("https://google.com");
        assertThat(httpUrl.getUrl(), is("https://google.com"));
        assertThat(httpUrl.getProtocol(), is("https"));


        // PATH
        httpUrl = new ParsedUrl("google.com");
        assertThat(httpUrl.getUrl(), is("http://google.com"));
        assertThat(httpUrl.getPath(), is(""));

        httpUrl = new ParsedUrl("google.com ");
        assertThat(httpUrl.getUrl(), is("http://google.com"));
        assertThat(httpUrl.getPath(), is(""));

        httpUrl = new ParsedUrl("http://google.com/");
        assertThat(httpUrl.getUrl(), is("http://google.com/"));
        assertThat(httpUrl.getPath(), is("/"));

        httpUrl = new ParsedUrl("http://google.com/asdf");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf"));
        assertThat(httpUrl.getPath(), is("/asdf"));

        httpUrl = new ParsedUrl("http://google.com/asdf/qwer");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf/qwer"));
        assertThat(httpUrl.getPath(), is("/asdf/qwer"));

        httpUrl = new ParsedUrl("http://google.com/asdf/qwer/");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf/qwer/"));
        assertThat(httpUrl.getPath(), is("/asdf/qwer/"));


        // PORT
        httpUrl = new ParsedUrl("http://google.com:65000");
        assertThat(httpUrl.getUrl(), is("http://google.com:65000"));
        assertThat(httpUrl.getPort(), is(65000));

        httpUrl = new ParsedUrl("http://google.com:42222/");
        assertThat(httpUrl.getUrl(), is("http://google.com:42222/"));
        assertThat(httpUrl.getPort(), is(42222));

        httpUrl = new ParsedUrl("http://google.com:9000/asdf/qwer/");
        assertThat(httpUrl.getUrl(), is("http://google.com:9000/asdf/qwer/"));
        assertThat(httpUrl.getPort(), is(9000));


        // QUERY
        httpUrl = new ParsedUrl("http://google.com?q=123");
        assertThat(httpUrl.getUrl(), is("http://google.com?q=123"));
        assertThat(httpUrl.getQuery(), is("q=123"));

        httpUrl = new ParsedUrl("http://google.com/?q=123");
        assertThat(httpUrl.getUrl(), is("http://google.com/?q=123"));
        assertThat(httpUrl.getQuery(), is("q=123"));

        httpUrl = new ParsedUrl("http://google.com/asdf?q=123");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf?q=123"));
        assertThat(httpUrl.getQuery(), is("q=123"));

        httpUrl = new ParsedUrl("http://google.com/asdf?q=123&2nd=qwer");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf?q=123&2nd=qwer"));
        assertThat(httpUrl.getQuery(), is("q=123&2nd=qwer"));

        // ANCHOR
        httpUrl = new ParsedUrl("http://google.com/asdf#some-anchor");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf#some-anchor"));
        assertThat(httpUrl.getAnchor(), is("some-anchor"));

        httpUrl = new ParsedUrl("http://google.com/asdf?q=123#some-anchor");
        assertThat(httpUrl.getUrl(), is("http://google.com/asdf?q=123#some-anchor"));
        assertThat(httpUrl.getQuery(), is("q=123"));
        assertThat(httpUrl.getAnchor(), is("some-anchor"));


        // DOMAIN AND TLD
        httpUrl = new ParsedUrl("http://google.com");
        assertThat(httpUrl.getDomain(), is("google.com"));
        assertThat(httpUrl.getTld(), is("com"));

        httpUrl = new ParsedUrl("http://google.co.uk/");
        assertThat(httpUrl.getDomain(), is("google.co.uk"));
        assertThat(httpUrl.getTld(), is("co.uk"));

        httpUrl = new ParsedUrl("http://google.com.br/");
        assertThat(httpUrl.getDomain(), is("google.com.br"));
        assertThat(httpUrl.getTld(), is("com.br"));

        httpUrl = new ParsedUrl("http://blogspot.com/"); // actually a public suffix, see https://github.com/google/guava/wiki/InternetDomainNameExplained
        assertThat(httpUrl.getDomain(), is("blogspot.com"));
        assertThat(httpUrl.getTld(), is("com"));


        // USER INFO
        httpUrl = new ParsedUrl("http://userid:password@example.com:8080/");
        assertThat(httpUrl.getUserInfo(), is("userid:password"));
    }

    @Test
    public void specialCase_inputs() throws Exception {
        // Probably bug in java.net.URL. Anything < -1 throws, but -1 is used to signal that there's no port and it gets through
        assertThat(new ParsedUrl("http://google.com:-1").getUrl(), is("http://google.com"));

        // IP or localhost
        ParsedUrl httpUrl = new ParsedUrl("http://127.0.0.1:8080/asdf");
        assertThat(httpUrl.getUrl(), is("http://127.0.0.1:8080/asdf"));
        assertThat(httpUrl.getDomain(), nullValue());
        assertThat(httpUrl.getTld(), nullValue());
        assertThat(httpUrl.getHost(), is("127.0.0.1"));

        httpUrl = new ParsedUrl("http://localhost:8080/asdf");
        assertThat(httpUrl.getUrl(), is("http://localhost:8080/asdf"));
        assertThat(httpUrl.getDomain(), is("localhost"));
        assertThat(httpUrl.getTld(), nullValue());
        assertThat(httpUrl.getHost(), is("localhost"));
    }


    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private String tryToCreateHttpUrl(final String url) {
        try {
            new ParsedUrl(url);
        } catch (final Exception e) {
            return e.getMessage();
        }
        throw new IllegalStateException("Provided URL didn't throw exception. URL: " + url);
    }

}
