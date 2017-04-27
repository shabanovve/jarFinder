package com.company;

import java.io.InputStream;

public class WarFileContext {
    private final InputStream stream;
    private final String earFileName;
    private final String warFileName;

    public WarFileContext(InputStream stream, String earFileName, String warFileName) {
        this.stream = stream;
        this.earFileName = earFileName;
        this.warFileName = warFileName;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getWarFileName() {
        return warFileName;
    }

    public String getEarFileName() {
        return earFileName;
    }
}
