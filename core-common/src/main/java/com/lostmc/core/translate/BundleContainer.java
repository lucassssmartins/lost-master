package com.lostmc.core.translate;

import com.lostmc.core.Commons;

import com.google.common.collect.Maps;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.io.File;
import java.util.*;

public final class BundleContainer {

    public static final String PREFIX = "messages";

    private final Map<String, MessageFormat> messageFormatCache = Maps.newHashMap();
    private ResourceBundle bundle;

    public BundleContainer(File resourceFile, Locale locale) {
        try {
            bundle = ResourceBundle.getBundle(PREFIX, locale,
                    new FileClassLoader(BundleContainer.class.getClassLoader(), resourceFile));
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }

    private String format(String key, Object... msg) {
        String unreplaced = translate(key);
        MessageFormat formatter = getFormatter(unreplaced);
        return formatter.format(msg);
    }

    private MessageFormat getFormatter(String unreplaced) {
        MessageFormat formatter = messageFormatCache.get(unreplaced);
        if (formatter == null)
            messageFormatCache.put(unreplaced, formatter = new MessageFormat(unreplaced));
        return formatter;
    }

    private String translate(String key) {
        try {
            return this.bundle.getString(key);
        } catch (MissingResourceException e) {
            return "[MISSING KEY: '" + key + "']";
        }
    }

    class FileClassLoader extends ClassLoader {
        private final File folder;

        public FileClassLoader(ClassLoader classLoader, File folder) {
            super(classLoader);
            this.folder = folder;
        }

        @Override
        public URL getResource(String string) {
            File file = new File(folder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ex) {
                }
            } else {
                string = string.replaceFirst("/", "");
                URL test = BundleContainer.class.getResource('/' + string);
                if (test != null)
                    return test;
            }
            return super.getResource(string);
        }

        @Override
        public InputStream getResourceAsStream(String string) {
            File file = new File(folder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                }
            } else {
                string = string.replaceFirst("/", "");
                InputStream stream = BundleContainer.class.getResourceAsStream('/' + string);
                if (stream != null) {
                    Commons.getPlatform().runAsync(new SaveResource(folder, string));
                    return stream;
                }
            }
            return super.getResourceAsStream(string);
        }
    }

    class SaveResource implements Runnable {
        private final String fileName;
        private final File rootFolder;

        private SaveResource(File rootFolder, String fileName) {
            this.rootFolder = rootFolder;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            File file = new File(rootFolder, fileName);
            if (file.exists())
                return;
            try {
                rootFolder.mkdirs();
                File to = File.createTempFile(fileName, null, rootFolder);
                to.deleteOnExit();
                Resources.asByteSource(Resources.getResource(BundleContainer.class, '/' + fileName))
                        .copyTo(Files.asByteSink(to, FileWriteMode.APPEND));
                if (!file.exists()) {
                    to.renameTo(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String translate(String key, Object... msg) {
        return msg.length == 0 ? translate(key)
                : format(key, msg);
    }
}
