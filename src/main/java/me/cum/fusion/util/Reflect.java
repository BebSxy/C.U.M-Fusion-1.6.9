
package me.cum.fusion.util;

import java.util.jar.*;
import java.util.*;
import sun.net.www.protocol.file.*;
import java.io.*;
import java.net.*;

public class Reflect implements Util
{
    private static void checkDirectory(final File directory, final String pckgname, final ArrayList<Class<?>> classes) throws ClassNotFoundException {
        if (directory.exists() && directory.isDirectory()) {
            final String[] list;
            final String[] files = list = directory.list();
            for (final String file : list) {
                if (file.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                    catch (NoClassDefFoundError noClassDefFoundError) {}
                }
                else {
                    final File tmpDirectory;
                    if ((tmpDirectory = new File(directory, file)).isDirectory()) {
                        checkDirectory(tmpDirectory, pckgname + "." + file, classes);
                    }
                }
            }
        }
    }
    
    private static void checkJarFile(final JarURLConnection connection, final String pckgname, final ArrayList<Class<?>> classes) throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry jarEntry = null;
        while (entries.hasMoreElements() && (jarEntry = entries.nextElement()) != null) {
            String name = jarEntry.getName();
            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');
                if (!name.contains(pckgname)) {
                    continue;
                }
                try {
                    classes.add(Class.forName(name));
                }
                catch (NoClassDefFoundError noClassDefFoundError) {}
            }
        }
    }
    
    public static ArrayList<Class<?>> getClassesForPackage(final String pckgname) throws ClassNotFoundException {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            final ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
            URL url = null;
            while (resources.hasMoreElements() && (url = resources.nextElement()) != null) {
                try {
                    final URLConnection connection = url.openConnection();
                    if (!(connection instanceof JarURLConnection)) {
                        if (connection instanceof FileURLConnection) {
                            try {
                                checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
                                continue;
                            }
                            catch (UnsupportedEncodingException ex) {
                                throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)", ex);
                            }
                        }
                        throw new ClassNotFoundException(pckgname + " (" + url.getPath() + ") does not appear to be a valid package");
                    }
                    checkJarFile((JarURLConnection)connection, pckgname, classes);
                    continue;
                }
                catch (IOException ioex) {
                    throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex);
                }
                break;
            }
        }
        catch (NullPointerException ex2) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)", ex2);
        }
        catch (IOException ioex2) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex2);
        }
        return classes;
    }
}
