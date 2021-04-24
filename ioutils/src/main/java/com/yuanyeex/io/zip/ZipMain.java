package com.yuanyeex.io.zip;

import com.google.common.base.Preconditions;
import com.yuanyeex.io.zip.impl.ZipperImpl;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;

/**
 * start with -Dfile.encoding=${charset} to avoid mismatch file name decoding.
 */
public class ZipMain {
    public static void main(String[] args) throws ZipException {

        if (args.length == 0 || args.length > 3) {
            System.err.println("Usage: unzip zipFilename <password>");
            return;
        }
        ZipArg zipArg = parseArgs(args);
        ZipperImpl zipper = new ZipperImpl();
        ProgressMonitor progressMonitor = zipper.unzip(zipArg.srcFile, zipArg.destFile, zipArg.password);
        waitFinish(progressMonitor);
    }

    private static void waitFinish(ProgressMonitor progressMonitor) {
        Preconditions.checkNotNull(progressMonitor);
        while (progressMonitor.getState() != ProgressMonitor.State.READY) {
            System.out.println("Done: " + progressMonitor.getPercentDone() + "%");
        }

        if (progressMonitor.getResult().equals(ProgressMonitor.Result.SUCCESS)) {
            System.out.println("Done!");
        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.ERROR)) {
            System.out.println("Error occurred. Error message: " + progressMonitor.getException().getMessage());
        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.CANCELLED)) {
            System.out.println("Task cancelled");
        }

    }

    private static ZipArg parseArgs(String[] args) {
        Preconditions.checkNotNull(args);
        Preconditions.checkArgument(args.length > 0);
        String srcFile = args[0];
        char[] password = null;
        if (args.length > 1) {
            password = args[1].toCharArray();
        }
        return new ZipArg(new File(srcFile), password, null);
    }

    private static class ZipArg {
        private final File srcFile;
        private final char[] password;
        private final File destFile;

        private ZipArg(File srcFile, char[] password, File destFile) {
            this.srcFile = srcFile;
            this.password = password;
            this.destFile = destFile;
        }
    }
}
