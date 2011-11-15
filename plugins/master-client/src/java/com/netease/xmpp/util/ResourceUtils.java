package com.netease.xmpp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Resource loading utility.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class ResourceUtils {
    /**
     * Returns a resource on the classpath as a Stream object
     * 
     * @param resource
     *            The resource to find
     * @throws IOException
     *             If the resource cannot be found or read
     * @return The resource
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        InputStream in = null;
        ClassLoader loader = ResourceUtils.class.getClassLoader();
        if (loader != null) {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    public static URI getResourceURI(String resource) throws IOException, URISyntaxException {
        URL url = null;
        ClassLoader loader = ResourceUtils.class.getClassLoader();
        if (loader != null) {
            url = loader.getResource(resource);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url.toURI();
    }

    /**
     * Returns a resource on the classpath as a Reader object
     * 
     * @param resource
     *            The resource to find
     * @throws IOException
     *             If the resource cannot be found or read
     * @return The resource
     */
    public static InputStreamReader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }
}
