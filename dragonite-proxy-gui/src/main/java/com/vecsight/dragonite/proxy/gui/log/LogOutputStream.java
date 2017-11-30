package com.vecsight.dragonite.proxy.gui.log;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.log
 * Created by mritd on 17/11/30 下午10:50.
 * Description: LogOutputStream
 *******************************************************************************/
public class LogOutputStream extends OutputStream {
    private TextArea textArea;

    public LogOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {

        // redirects data to the text area
        textArea.appendText(String.valueOf((char)b));

    }
}
