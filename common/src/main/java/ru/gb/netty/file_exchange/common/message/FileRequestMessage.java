package ru.gb.netty.file_exchange.common.message;

public class FileRequestMessage extends Message{
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
