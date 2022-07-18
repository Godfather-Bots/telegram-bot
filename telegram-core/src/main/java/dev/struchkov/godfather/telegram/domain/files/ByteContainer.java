package dev.struchkov.godfather.telegram.domain.files;

public class ByteContainer {

    public static final ByteContainer EMPTY = new ByteContainer(null, null);

    private final String fileName;
    private final byte[] bytes;

    public ByteContainer(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public static ByteContainer empty() {
        return EMPTY;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isNotEmpty() {
        return bytes != null && bytes.length > 0;
    }

}
