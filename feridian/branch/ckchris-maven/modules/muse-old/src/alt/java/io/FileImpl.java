package alt.java.io;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileImpl implements File {
    private final java.io.File file;

    public FileImpl(java.io.File file) {
        this.file = file;
    }

    public FileImpl(String fileName) {
        this.file = new java.io.File(fileName);
    }

    public String getName() {
        return file.getName();
    }

    public String getParent() {
        return file.getParent();
    }

    public File getParentFile() {
        return new FileImpl(file.getParentFile());
    }

    public String getPath() {
        return file.getPath();
    }

    public boolean isAbsolute() {
        return file.isAbsolute();
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public File getAbsoluteFile() {
        return new FileImpl(file.getAbsoluteFile());
    }

    public String getCanonicalPath() throws IOException {
        return file.getCanonicalPath();
    }

    public File getCanonicalFile() throws IOException {
        return new FileImpl(file.getCanonicalFile());
    }

    public URL toURL() throws MalformedURLException {
        return file.toURL();
    }

    public boolean canRead() {
        return file.canRead();
    }

    public boolean canWrite() {
        return file.canWrite();
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isHidden() {
        return file.isHidden();
    }

    public long lastModified() {
        return file.lastModified();
    }

    public long length() {
        return file.length();
    }

    public boolean createNewFile() throws IOException {
        return file.createNewFile();
    }

    public boolean delete() {
        return file.delete();
    }

    public void deleteOnExit() {
        file.deleteOnExit();
    }

    public String[] list() {
        return file.list();
    }

    public String[] list(FilenameFilter filter) {
        return file.list();
    }

    public File[] listFiles() {
        return toAltFileArray(file.listFiles());
    }

    public File[] listFiles(FilenameFilter filter) {
        return toAltFileArray(file.listFiles(filter));
    }

    public File[] listFiles(FileFilter filter) {
        return toAltFileArray(file.listFiles(filter));
    }

    private final File[] toAltFileArray(java.io.File[] files) {
        if (files == null) return null;

        File[] altFiles = new File[files.length];

        for (int i = 0; i < files.length; i++) {
            altFiles[i] = new FileImpl(files[i]);
        }

        return altFiles;
    }

    public boolean mkdir() {
        return file.mkdir();
    }

    public boolean mkdirs() {
        return file.mkdirs();
    }

    public File createTempFile(String prefix, String suffix, File directory) throws IOException {
        return new FileImpl(java.io.File.createTempFile(prefix, suffix,
                directory.getRealFile()));
    }

    public File createTempFile(String prefix, String suffix) throws IOException {
        return new FileImpl(java.io.File.createTempFile(prefix, suffix));
    }

    public File[] listRoots() {
        return toAltFileArray(java.io.File.listRoots());
    }

    public boolean renameTo(File dest) {
        return file.renameTo(dest.getRealFile());
    }

    public boolean setLastModified(long time) {
        return file.setLastModified(time);
    }

    public boolean setReadOnly() {
        return file.setReadOnly();
    }

    public int compareTo(File pathname) {
        return file.compareTo(pathname.getRealFile());
    }

    public int compareTo(Object o) {
        return file.compareTo(o);
    }

    public java.io.File getRealFile() {
        return file;
    }
}
