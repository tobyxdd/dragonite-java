package com.vecsight.dragonite.proxy.gui.log;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.module
 * Created by mritd on 17/11/28 下午9:38.
 * Description: TextAreaStaticOutputStreamAppender
 *******************************************************************************/
public class TextAreaStaticOutputStreamAppender <E> extends OutputStreamAppender<E> {

    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {

        /**
         * Creates a delegating outputstream with a NO-OP delegate
         */
        public DelegatingOutputStream(OutputStream out){
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {}
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }
}
