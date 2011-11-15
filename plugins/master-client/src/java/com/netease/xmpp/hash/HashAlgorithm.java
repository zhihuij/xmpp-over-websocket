package com.netease.xmpp.hash;

/**
 * Interface for hash algorithm.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public interface HashAlgorithm {

    /**
     * For server use.
     * 
     * @param digest
     *            md5 of the server key
     * @param nTime
     *            index
     * @return hash result
     */
    public abstract long hash(byte[] digest, int nTime);

    /**
     * For client use.
     * 
     * @param k
     *            key
     * @return hash result
     */
    public abstract long hash(String k);

    /**
     * Get the md5 of the given key.
     */
    public abstract byte[] computeMd5(String k);
}