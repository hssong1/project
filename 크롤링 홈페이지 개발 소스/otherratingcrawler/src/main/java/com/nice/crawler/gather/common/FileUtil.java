package com.nice.crawler.gather.common;

import java.io.File;

public class FileUtil {

    /**
     * @description 폴더를 생성합니다.
     * @param folderLocation
     */
    public static void makeFolder(File folderLocation) {
        if(!folderLocation.exists()) {
            folderLocation.mkdirs();
        }
    }

    public static void removeFile(File fileLocation) {
        fileLocation.deleteOnExit();
    }
}