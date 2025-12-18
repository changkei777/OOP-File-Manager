package FileManagerApp;

import java.io.File;

public class FileService implements IFileService {

    @Override
    public File[] getFilesInDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            return directory.listFiles();
        }
        return new File[0];
    }

    @Override
    public boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public boolean createDirectory(File parentDirectory, String newFolderName) {
        File newDir = new File(parentDirectory, newFolderName);
        if (!newDir.exists()) {
            return newDir.mkdir();
        }
        return false;
    }
}