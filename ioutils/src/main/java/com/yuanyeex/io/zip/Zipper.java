package com.yuanyeex.io.zip;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;

public interface Zipper {
    ProgressMonitor unzip(File srcPath) throws ZipException;
    ProgressMonitor unzip(File srcPath, char[] password) throws ZipException;
    ProgressMonitor unzip(File srcPath, File destDir, char[] password) throws ZipException, ZipException;
}
