package com.luv2code.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    private Path foundfile;
    public Resource getFileAsResourse(String downloadDir, String fileName) throws IOException {

        Path path = Paths.get(downloadDir);
        Files.list(path).forEach(file -> {
            if(file.getFileName().toString().startsWith(fileName)){
                this.foundfile = file;
            }
        });

        if (this.foundfile != null) {
            return new UrlResource(this.foundfile.toUri());
        }

        return null;
    }
}
