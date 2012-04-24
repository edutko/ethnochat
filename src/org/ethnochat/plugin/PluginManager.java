package org.ethnochat.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.ethnochat.io.FileFilterUsingExtension;
import org.ethnochat.io.NotADirectoryException;

public class PluginManager {

    private static final PluginManager soleInstance = new PluginManager();
    private HashMap<UUID, Plugin> pluginMap;
    private TreeSet<File> pluginDirectories;

    public static PluginManager getInstance() {
        return soleInstance;
    }

    public void addPluginDirectory(File dir) throws NotADirectoryException {
        if (!dir.isDirectory()) {
            throw new NotADirectoryException(dir.getName());
        }
        pluginDirectories.add(dir);
    }

    public Set<UUID> getPluginIDs() {
        return pluginMap.keySet();
    }

    public Collection<Plugin> getPlugins() {
        return pluginMap.values();
    }

    public Collection<Plugin> getPlugins(ECPluginType type) {
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        Iterator<Plugin> iter = pluginMap.values().iterator();
        while (iter.hasNext()) {
            Plugin p = iter.next();
            if (p.getType() == type) {
                plugins.add(p);
            }
        }
        return plugins;
    }

    public Plugin getPlugin(UUID id) {
        return pluginMap.get(id);
    }

    public void loadAllPlugins() {
        Iterator<File> iter = pluginDirectories.iterator();
        while (iter.hasNext()) {
            loadPluginsFromDirectory(iter.next());
        }
    }

    public void unloadAllPlugins() {
        ArrayList<UUID> pluginList = new ArrayList<UUID>(getPluginIDs());
        Iterator<UUID> iter = pluginList.iterator();
        while (iter.hasNext()) {
            UUID id = iter.next();
            pluginMap.get(id).onUnload();
            pluginMap.remove(id);
        }
    }

    private PluginManager() {
        pluginMap = new HashMap<UUID, Plugin>();
        pluginDirectories = new TreeSet<File>();
    }

    private void loadPluginsFromDirectory(File directory) {
        File[] pluginJarFiles = directory.listFiles(
            new FileFilterUsingExtension("jar"));

        for (int i = 0; i < pluginJarFiles.length; i++) {
            try {
                Plugin pluginObject = getPluginObject(pluginJarFiles[i]);
                Plugin oldPluginObject =
                    pluginMap.put(pluginObject.getID(), pluginObject);

                if (oldPluginObject == null) {

                    // No plugin with this ID is loaded.
                    pluginObject.onLoad();

                } else if (oldPluginObject.equals(pluginObject)) {

                    // This is a different version - or another copy - of a
                    // plugin that is already loaded.
                    oldPluginObject.onUnload();
                    pluginObject.onLoad();

                } else {

                    // This is a plugin with the same ID as a plugin that is
                    // already loaded, but it is not the same plugin.
                    throw new RuntimeException("Duplicate plugin id");
                }
            } catch (IOException e) {
                System.err.println(e);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            } catch (InvocationTargetException e) {
                System.err.println(e);
            } catch (NoSuchMethodException e) {
                System.err.println(e);
            } catch (ManifestMissingException e) {
                System.err.println(e);
            }
        }
    }

    private static Plugin getPluginObject(File file) throws IOException,
            ClassNotFoundException,
            InvocationTargetException,
            NoSuchMethodException,
            ManifestMissingException {

        Plugin p = null;

        Manifest manifest = (new JarFile(file)).getManifest();
        if (manifest == null) {
            throw new ManifestMissingException(file.getName());
        }
        Attributes attr = manifest.getMainAttributes();
        String className = attr.getValue("Plugin-Main-Class");
        String pluginName = attr.getValue("Plugin-Name");

        URLClassLoader classLoader =
            createURLClassLoader(file, attr.getValue("Class-Path"));
        Class<?> c = classLoader.loadClass(className);
        Method creationMethod = c.getMethod("create", new Class[] {});
        creationMethod.setAccessible(true);
        int mods = creationMethod.getModifiers();
        if (creationMethod.getReturnType() != Plugin.class
                || !Modifier.isStatic(mods)
                || !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("create");
        }

        try {

            // Call this plugin's static create() method to get an instance
            // of it.
            p = (Plugin)creationMethod.invoke(null, new Object[] {});
            p.setName(pluginName);
        } catch (IllegalAccessException e) {

            // This should never happen because we called setAccessible()
            // above.
            throw new RuntimeException(e);
        }
        return p;
    }

    private static URLClassLoader createURLClassLoader(
            File pluginJarFile,
            String paths) {

        String[] pathList = paths.split(System.getProperty("path.separator"));
        URL[] dependecyURLs = new URL[pathList.length + 1];

        try {
            dependecyURLs[0] = pluginJarFile.toURL();
        } catch (MalformedURLException e) {}

        for (int i = 1; i < pathList.length; i++) {
            try {
                dependecyURLs[i] = (new File(pathList[i])).toURL();
            } catch (MalformedURLException e) {}
        }

        return new URLClassLoader(dependecyURLs);
    }
}
