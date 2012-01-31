/*
 * Copyright (C) 2012 Felix Schulze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.felixschulze.maven.plugins.xcode.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Create zip files with subdirectories.
 *
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */

public class ZipCreator {
    File baseDir;

    public ZipCreator(File baseDir) {
        this.baseDir = baseDir;
    }

    public void createZipFromDirectory(File sourceDirectory, File outputPath) throws IOException {

        zipDir(outputPath.getAbsolutePath(), sourceDirectory.getPath());
    }

    private void zipDir(String zipFileName, String dir) throws IOException {
        File dirObj = new File(dir);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        addDir(dirObj, out);
        out.close();
    }

    private void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out);
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
            out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().substring(baseDir.getAbsolutePath().length())));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }
}
