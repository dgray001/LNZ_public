package FileSystem;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import processing.core.*;
import LNZApplet.LNZApplet;

public class FileSystem {
  public static void mkFile(LNZApplet sketch, String path) {
    mkFile(sketch, path, false);
  }
  public static void mkFile(LNZApplet sketch, String path, boolean replace) {
    mkFile(sketch, Paths.get(sketch.sketchPath(path)), replace);
  }
  public static void mkFile(LNZApplet sketch, Path p, boolean replace) {
    if (!Files.exists(p)) {
      try {
        Files.createFile(p);
      } catch (IOException e) {
        PApplet.println("ERROR: IOException at mkFile(" + p + ")");
      }
    }
    else if (replace && !Files.isDirectory(p)) {
      deleteFile(sketch, p);
      try {
        Files.createFile(p);
      } catch (IOException e) {
        PApplet.println("ERROR: IOException at mkFile(" + p + ")");
      }
    }
  }

  // move file
  public static void moveFile(LNZApplet sketch, String source_path, String target_path) {
    moveFile(sketch, Paths.get(sketch.sketchPath(source_path)), Paths.get(sketch.sketchPath(target_path)));
  }
  public static void moveFile(LNZApplet sketch, Path source, Path target) {
    try {
      Files.move(source, target);
    } catch(IOException e) {
      PApplet.println("ERROR: IOException at moveFile(" + source + ", " + target + ")");
    }
  }

  // copy file
  public static void copyFile(LNZApplet sketch, String source_path, String target_path) {
    copyFile(sketch, Paths.get(sketch.sketchPath(source_path)), Paths.get(sketch.sketchPath(target_path)));
  }
  public static void copyFile(LNZApplet sketch, Path source, Path target) {
    try {
      Files.copy(source, target);
    } catch(IOException e) {
      PApplet.println("ERROR: IOException at copyFile(" + source + ", " + target + ")");
    }
  }

  // delete file
  public static void deleteFile(LNZApplet sketch, String path) {
    deleteFile(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static void deleteFile(LNZApplet sketch, Path p) {
    try {
      Files.deleteIfExists(p);
    } catch(IOException e) {
      PApplet.println("ERROR: IOException at deleteFile(" + p + ")");
    }
  }

  // list all entries in directory
  public static ArrayList<Path> listEntries(LNZApplet sketch, String path) {
    return listEntries(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static ArrayList<Path> listEntries(LNZApplet sketch, Path p) {
    ArrayList<Path> entries = new ArrayList<Path>();
    try {
      if (Files.isDirectory(p)) {
        Files.list(p).forEach(entry -> entries.add(entry));
      }
      else {
        PApplet.println("ERROR: Not a directory at listEntries(" + p + ")");
      }
    } catch(IOException e) {
      PApplet.println("ERROR: IOException at listEntries(" + p + ")");
    }
    return entries;
  }

  // list all files in directory
  public static ArrayList<Path> listFiles(LNZApplet sketch, String path) {
    return listFiles(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static ArrayList<Path> listFiles(LNZApplet sketch, Path p) {
    ArrayList<Path> files = listEntries(sketch, p);
    for (int i = 0; i < files.size(); i++) {
      if (Files.isDirectory(files.get(i))) {
        files.remove(i);
        i--;
      }
    }
    return files;
  }

  // list all folder in directory
  public static ArrayList<Path> listFolders(LNZApplet sketch, String path) {
    return listFolders(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static ArrayList<Path> listFolders(LNZApplet sketch, Path p) {
    ArrayList<Path> folders = listEntries(sketch, p);
    for (int i = 0; i < folders.size(); i++) {
      if (!Files.isDirectory(folders.get(i))) {
        folders.remove(i);
        i--;
      }
    }
    return folders;
  }

  // default to if folder does not exist create folder
  public static void mkdir(LNZApplet sketch, String path) {
    mkdir(sketch, path, false);
  }
  public static void mkdir(LNZApplet sketch, String path, boolean replace) {
    mkdir(sketch, path, replace, false);
  }
  public static void mkdir(LNZApplet sketch, String path, boolean replace, boolean replace_file) {
    mkdir(sketch, Paths.get(sketch.sketchPath(path)), replace, replace_file);
  }
  public static void mkdir(LNZApplet sketch, Path p, boolean replace, boolean replace_file) {
    if (!Files.exists(p)) {
      try {
        Files.createDirectory(p);
      } catch (IOException e) {
        PApplet.println("ERROR: IOException at mkdir(" + p + ")");
      }
    }
    else if (replace && Files.isDirectory(p)) {
      deleteFolder(sketch, p);
      try {
        Files.createDirectory(p);
      } catch (IOException e) {
        PApplet.println("ERROR: IOException at mkdir(" + p + ")");
      }
    }
    else if (replace_file && !Files.isDirectory(p)) {
      deleteFile(sketch, p);
      try {
        Files.createDirectory(p);
      } catch (IOException e) {
        PApplet.println("ERROR: IOException at mkdir(" + p + ")");
      }
    }
  }

  // move folder
  public static void moveFolder(LNZApplet sketch, String source_path, String target_path) {
    moveFolder(sketch, Paths.get(sketch.sketchPath(source_path)), Paths.get(sketch.sketchPath(target_path)));
  }
  public static void moveFolder(LNZApplet sketch, Path source, Path target) {
    if (Files.isDirectory(source)) {
      mkdir(sketch, target, false, false);
      for (Path filePath : listFiles(sketch, source)) {
        moveFile(sketch, filePath, target.resolve(filePath.getFileName()));
      }
      for (Path folderPath : listFolders(sketch, source)) {
        moveFolder(sketch, folderPath, target.resolve(folderPath.getFileName()));
      }
    }
    else {
      moveFile(sketch, source, target);
    }
    deleteFolder(sketch, source);
  }

  // copy folder
  public static void copyFolder(LNZApplet sketch, String source_path, String target_path) {
    copyFolder(sketch, Paths.get(sketch.sketchPath(source_path)), Paths.get(sketch.sketchPath(target_path)));
  }
  public static void copyFolder(LNZApplet sketch, Path source, Path target) {
    if (Files.isDirectory(source)) {
      mkdir(sketch, target, false, false);
      for (Path filePath : listFiles(sketch, source)) {
        copyFile(sketch, filePath, target.resolve(filePath.getFileName()));
      }
      for (Path folderPath : listFolders(sketch, source)) {
        copyFolder(sketch, folderPath, target.resolve(folderPath.getFileName()));
      }
    }
    else {
      copyFile(sketch, source, target);
    }
  }

  // recursively deletes folder
  public static void deleteFolder(LNZApplet sketch, String path) {
    deleteFolder(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static void deleteFolder(LNZApplet sketch, Path p) {
    if (Files.isDirectory(p)) {
      for (Path filePath : listFiles(sketch, p)) {
        deleteFile(sketch, filePath);
      }
      for (Path folderPath : listFolders(sketch, p)) {
        deleteFolder(sketch, folderPath);
      }
      try {
        Files.delete(p);
      } catch(IOException e) {
        PApplet.println("ERROR: IOException at deleteFolder(" + p + ")");
      }
    }
    else {
      deleteFile(sketch, p);
    }
  }

  // Entry exists
  public static boolean entryExists(LNZApplet sketch, String path) {
    return entryExists(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static boolean entryExists(LNZApplet sketch, Path p) {
    return Files.exists(p);
  }

  // File exists
  public static boolean fileExists(LNZApplet sketch, String path) {
    return fileExists(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static boolean fileExists(LNZApplet sketch, Path p) {
    return (Files.exists(p) && !Files.isDirectory(p));
  }

  // Folder exists
  public static boolean folderExists(LNZApplet sketch, String path) {
    return folderExists(sketch, Paths.get(sketch.sketchPath(path)));
  }
  public static boolean folderExists(LNZApplet sketch, Path p) {
    return (Files.exists(p) && Files.isDirectory(p));
  }
}