package com.yiwise.esl;

import javaslang.control.Try;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PropertyLoader {
    private static final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);
    private static Properties RESOURCE_BUNDLE;

    static {
        Properties properties = new Properties();
        Try.run(() -> properties.putAll(loadProperties("application.properties"))).onFailure(e -> logger.warn("application.properties", e.getMessage()));
        RESOURCE_BUNDLE = properties;
    }

    public static String getProperty(String key) {
        String property;
        try {
            property = RESOURCE_BUNDLE.getProperty(key);
            logger.debug("load property, key={}, val={}.", key, property);
        } catch (MissingResourceException e) {
            throw new RuntimeException("can't load context file,key:" + key, e);
        }
        Assert.notNull(property, "key=" + key + ", 值为空，请检查配置文件。");
        return property;
    }

    public static int getIntProperty(String key) {
        return Integer.valueOf(getProperty(key));
    }

    public static Long getLongProperty(String key) {
        return Long.valueOf(getProperty(key));
    }

    public static Set<Long> getSetLongProperty(String key) {
        String str = getProperty(key);
        String[] strList = StringUtils.split(str, ",");
        Set<Long> set = new HashSet<>();
        for (String istr : strList) {
            Long id = 0L;
            try {
                id = Long.valueOf(istr);
            } catch (Exception e) {
                continue;
            }
            set.add(id);
        }
        return set;
    }

    public static Set<String> getSetStringProperty(String key) {
        String str = getProperty(key);
        String[] strList = StringUtils.split(str, ",");
        return new HashSet<>(Arrays.asList(strList));
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(getProperty(key));
    }

    /**
     * a,b,c,d -> (a, b)(c, d)
     *
     * @param key
     * @return
     */
    public static Map<String, String> getMapProperty(String key) {
        String str = getProperty(key);
        String[] strList = StringUtils.split(str, ",");
        if (strList.length % 2 == 1) {
            throw new RuntimeException("can't load context file, key:" + key);
        }
        Map<String, String> ret = new HashMap<>(strList.length / 2);
        for (int i = 0; i + 1 < strList.length; i += 2) {
            ret.put(strList[i], strList[i + 1]);
        }
        return ret;
    }

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(loadAsInputStream(path), "UTF-8");
            BufferedReader bf = new BufferedReader(isr);
            properties.load(bf);
        } catch (IOException e) {
            throw new RuntimeException("配置文件解析失败。", e);
        }
        return properties;
    }

    private static InputStream loadAsInputStream(String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("配置文件加载失败或文件不存在:" + path);
        }
        logger.debug("加载配置文件, path={}.", path);
        return stream;
    }
}
