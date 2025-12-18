package FileManagerApp;

import java.io.File;

public interface IFileService {
    File[] getFilesInDirectory(File directory);
    boolean deleteFile(File file);
    boolean createDirectory(File parentDirectory, String newFolderName);
}