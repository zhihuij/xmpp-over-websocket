package com.netease.xmpp.hash;

import com.netease.xmpp.master.common.ConfigCache;

/**
 * Class loader for hash algorithm.
 * <p>
 * When hash algorithm updated, this loader will load the new implementation from the updated data,
 * and create a new hash algorithm object, and the all the hash request will operation with the
 * newly created hash algorithm object.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class HashAlgorithmLoader extends ClassLoader {
    private ClassLoader parent = null;
    private byte[] classData = null;
    private ConfigCache config = null;

    public HashAlgorithmLoader(ClassLoader parent, byte[] classData, ConfigCache config) {
        this.parent = parent;
        this.classData = classData;
        this.config = config;
    }

    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.contains(config.getHashAlgorithmClassName())) {
            if (classData == null) {
                throw new ClassNotFoundException();
            } else {
                try {
                    return defineClass(name, classData, 0, classData.length);
                } catch (ClassFormatError e) {
                    throw new ClassNotFoundException("class formate error: ", e);
                }
            }
        } else {
            Class<?> c = null;
            // Lets see if the class is in system class loader.
            try {
                c = findSystemClass(name);
            } catch (ClassNotFoundException cnfe) {
            } finally {
                if (c != null)
                    return c;
            }

            // Lets see if the class is in parent class loader.
            try {
                if (parent != null)
                    c = parent.loadClass(name);
            } catch (ClassNotFoundException cnfe) {
            } finally {
                if (c != null)
                    return c;
            }

            throw new ClassNotFoundException();
        }
    }
}
