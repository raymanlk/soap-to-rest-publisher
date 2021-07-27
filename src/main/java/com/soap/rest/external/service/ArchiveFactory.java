package com.soap.rest.external.service;

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class ArchiveFactory {
    private static final Map<String, Class<? extends ArchiveFormat>> instances = new HashMap<>();

    public static void register(String archiveMedium, Class<? extends ArchiveFormat> instance) {
        if (archiveMedium != null && instance != null) {
            instances.put(archiveMedium, instance);
        }
    }

    public static ArchiveFormat getInstance(ApplicationContext context, String archiveMedium) {
        if (instances.containsKey(archiveMedium)) {
            return context.getBean(instances.get(archiveMedium));
        }
        return null;
    }

}
