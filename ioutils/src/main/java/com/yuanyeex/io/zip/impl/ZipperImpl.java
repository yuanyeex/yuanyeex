package com.yuanyeex.io.zip.impl;

import com.google.common.base.Preconditions;
import com.yuanyeex.io.zip.Zipper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

public class ZipperImpl implements Zipper {

    @Override
    public ProgressMonitor unzip(File srcPath) throws ZipException {
       return unzip(srcPath, null);
    }

    @Override
    public ProgressMonitor unzip(File srcPath, char[] password) throws ZipException {
        return unzip(srcPath, null, password);
    }

    @Override
    public ProgressMonitor unzip(File srcZipFile, File destDir, char[] password) throws ZipException {
        Preconditions.checkNotNull(srcZipFile, "input zip file is null");
        Preconditions.checkArgument(srcZipFile.exists(), "input file: %s not exist", srcZipFile);

        ZipFile zipFile = new ZipFile(srcZipFile);
        zipFile.setCharset(Charset.defaultCharset());
        Preconditions.checkArgument(zipFile.isValidZipFile(), "input file: %s is not a valid zip file", srcZipFile);

        if (destDir == null) {
            File filePath = srcZipFile.getParentFile();
            String zipFileName = FileUtils.getFileNameWithoutExtension(srcZipFile.getName()) + "_extracted";
            destDir = new File(filePath, zipFileName);
            if (destDir.exists()) {
                throw new IllegalStateException("Cannot extract to " + destDir.getPath());
            }
        }

        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }
        zipFile.setRunInThread(true);
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
        zipFile.extractAll(destDir.getAbsolutePath());
        return progressMonitor;
    }

}
