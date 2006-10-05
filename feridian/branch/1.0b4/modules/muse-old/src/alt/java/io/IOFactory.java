package alt.java.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IOFactory {
    InputStream createInputStream(File aFile) throws FileNotFoundException;

    OutputStream createOutputStream(File aFile) throws FileNotFoundException;

    File createFile(String fileName);
}
