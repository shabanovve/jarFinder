package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        new Main().start(args[0]);
    }

    private void start(String path) throws IOException {
        List<Path> earFilesInDirecotory = getEarFilesInDirectory(path);
        System.out.println("Ear-files:" + earFilesInDirecotory.size());
        for (Path earFile : earFilesInDirecotory) {
            List<WarFileContext> warStreamsFromEar = getWarStreamFromEar(earFile.toFile());
            for (WarFileContext warFileContext : warStreamsFromEar) {
                try (ZipInputStream zipInputStream = new ZipInputStream(warFileContext.getStream())) {
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        String fileName = entry.getName().toLowerCase();
                        if (fileName != null && !fileName.isEmpty()) {
                            int length = fileName.length();
                            if (length > 5 && fileName.substring(length - 4, length).equals(".jar")) {
                                if ((fileName.contains("log4j") || fileName.contains("logback")) && !fileName.contains("slf4j")) {
                                    print("=================================");
                                    print("Ear file: " + warFileContext.getEarFileName());
                                    print("War file: " + warFileContext.getWarFileName());
                                    print("Jar file: " + fileName);
                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void print(String s) {
        System.out.println(s);
    }

    private static List<WarFileContext> getWarStreamFromEar(File file) throws IOException {
        ZipFile ear = new ZipFile(file);
        return ear
                .stream()
                .filter(
                        zipEntry -> zipEntry.getName()
                                .endsWith(".war".toLowerCase())
                )
                .map(warFile -> {
                    try {
                        return new WarFileContext(ear.getInputStream(warFile), file.getName(), warFile.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private static List<Path> getEarFilesInDirectory(String path) {
        try {
            return Files.find(Paths.get(path),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .filter(filePath -> {
                        String name = filePath.toAbsolutePath().toString();
                        String substring = name.substring(name.length() - 4, name.length());
                        return substring.toLowerCase().equals(".ear");
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
