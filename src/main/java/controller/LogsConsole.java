package controller;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class LogsConsole extends OutputStream {

    private TextArea taLogs;

    public LogsConsole(TextArea taLogs) {
        this.taLogs = taLogs;
    }

    @Override
    public void write(int i) throws IOException {
        taLogs.appendText(String.valueOf((char) i));
    }
}
