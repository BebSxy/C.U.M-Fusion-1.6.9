
package me.cum.fusion.util;

import java.nio.charset.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil
{
    public static void appendTextFile(final String data, final String file) {
        try {
            final Path path = Paths.get(file, new String[0]);
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to write file: " + file);
        }
    }
    
    public static List<String> readTextFileAllLines(final String file) {
        try {
            final Path path = Paths.get(file, new String[0]);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to read file,  creating new file: " + file);
            appendTextFile("", file);
            return Collections.emptyList();
        }
    }
}
