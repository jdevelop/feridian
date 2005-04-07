package alt.java.io;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public interface File {
    public String getName();

    public String getParent();

    public File getParentFile();

    public String getPath();

    public boolean isAbsolute();

    public String getAbsolutePath();

    public File getAbsoluteFile();

    public String getCanonicalPath() throws IOException;

    public File getCanonicalFile() throws IOException;

    public URL toURL() throws MalformedURLException;

    public boolean canRead();

    public boolean canWrite();

    public boolean exists();

    public boolean isDirectory();

    public boolean isFile();

    public boolean isHidden();

    public long lastModified();

    public long length();

    public boolean createNewFile() throws IOException;

    public boolean delete();

    public void deleteOnExit();

    public String[] list();

    public String[] list(FilenameFilter filter);

    public File[] listFiles();

    public File[] listFiles(FilenameFilter filter);

    public File[] listFiles(FileFilter filter);

    public boolean mkdir();

    public boolean mkdirs();

    public boolean renameTo(File dest);

    public boolean setLastModified(long time);

    public boolean setReadOnly();

    public int compareTo(File pathname);

    public int compareTo(Object o);

    public java.io.File getRealFile();

    public File createTempFile(String prefix, String suffix, File directory) throws IOException;

    public File createTempFile(String prefix, String suffix) throws IOException;

    public File[] listRoots();
}
