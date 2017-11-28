package com.vecsight.dragonite.proxy.gui.module;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.module
 * Created by mritd on 17/11/28 下午9:38.
 * Description: GuiConfig
 *******************************************************************************/
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GuiConfig {
    private StringProperty serverAddress;
    private StringProperty serverPassword;
    private IntegerProperty serverPort;
    private IntegerProperty localSocks5Port;
    private IntegerProperty downloadMbps;
    private IntegerProperty uploadMbps;
    private IntegerProperty limitMbps;

    public GuiConfig() {

    }

    public GuiConfig(StringProperty serverAddress, StringProperty serverPassword, IntegerProperty serverPort, IntegerProperty localSocks5Port, IntegerProperty downloadMbps, IntegerProperty uploadMbps, IntegerProperty limitMbps) {
        this.serverAddress = serverAddress;
        this.serverPassword = serverPassword;
        this.serverPort = serverPort;
        this.localSocks5Port = localSocks5Port;
        this.downloadMbps = downloadMbps;
        this.uploadMbps = uploadMbps;
        this.limitMbps = limitMbps;
    }

}
