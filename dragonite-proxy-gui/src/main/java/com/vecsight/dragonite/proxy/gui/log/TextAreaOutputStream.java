package com.vecsight.dragonite.proxy.gui.log;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.module
 * Created by mritd on 17/11/28 下午9:38.
 * Description: TextAreaOutputStream
 *******************************************************************************/
public class TextAreaOutputStream extends OutputStream {

    private TextArea textArea;

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.appendText(String.valueOf((char) b));
    }
}
