package com.netease.xmpp.master.common;

import com.netease.xmpp.hash.HashAlgorithm;

/**
 * Abstract config cache, for server and client.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public abstract class ConfigCache {
    /**
     * The hash algorithm object.
     */
    private HashAlgorithm hashAlgorithm = null;

    /**
     * Class name hash algorithm.
     */
    private String hashAlgorithmClassName = null;

    public synchronized HashAlgorithm getHashAlgorithm() {
        return hashAlgorithm;
    }

    public synchronized void setHashAlgorithm(HashAlgorithm hash) {
        this.hashAlgorithm = hash;
    }

    public String getHashAlgorithmClassName() {
        return hashAlgorithmClassName;
    }

    public void setHashAlgorithmClassName(String className) {
        this.hashAlgorithmClassName = className;
    }
}
