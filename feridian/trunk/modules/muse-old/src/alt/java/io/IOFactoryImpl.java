package alt.java.io;

import java.io.*;

public class IOFactoryImpl implements IOFactory {
    public InputStream createInputStream(File aFile) throws FileNotFoundException {
        return new FileInputStream(aFile.getRealFile());
    }

    public OutputStream createOutputStream(File aFile) throws FileNotFoundException {
        return new FileOutputStream(aFile.getRealFile());
    }

    public File createFile(String fileName) {
        return new FileImpl(fileName);
    }
}
