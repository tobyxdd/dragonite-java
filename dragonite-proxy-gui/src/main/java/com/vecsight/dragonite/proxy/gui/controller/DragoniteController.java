package com.vecsight.dragonite.proxy.gui.controller;

import com.vecsight.dragonite.proxy.config.ProxyClientConfig;
import com.vecsight.dragonite.proxy.exception.IncorrectHeaderException;
import com.vecsight.dragonite.proxy.exception.ServerRejectedException;
import com.vecsight.dragonite.proxy.gui.log.LogOutputStream;
import com.vecsight.dragonite.proxy.network.client.ProxyClient;
import com.vecsight.dragonite.sdk.exception.DragoniteException;
import com.vecsight.dragonite.sdk.exception.EncryptionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.controller
 * Created by mritd on 17/11/28 下午9:40.
 * Description: DragoniteController
 *******************************************************************************/
public class DragoniteController {
    @FXML
    private TextField tfServer;
    @FXML
    private TextField tfServerPort;
    @FXML
    private TextField tfLocalPort;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private TextField tfDownloadMbps;
    @FXML
    private TextField tfUploadMbps;
    @FXML
    private TextField tfLimitMbps;
    @FXML
    private TextField tfMTU;
    @FXML
    private TextArea taLogs;

    @FXML
    private Label lServer;
    @FXML
    private Label lPassword;
    @FXML
    private Label lServerPort;
    @FXML
    private Label lLocalPort;
    @FXML
    private Label lDownload;
    @FXML
    private Label lUpload;
    @FXML
    private Label lLimit;
    @FXML
    private Label lMTU;

    private ProxyClientConfig clientConfig;
    private ProxyClient proxyClient;


    @FXML
    public void dragoniteProxyStart(ActionEvent event) throws UnknownHostException {

        initLog();

        if (StringUtils.isBlank(tfServer.getText())) {
            Logger.error("Server address is Blank!");
            return;
        }
        if (StringUtils.isBlank(pfPassword.getText())) {
            Logger.error("Server password is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfServerPort.getText())) {
            Logger.error("Server port is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfLocalPort.getText())) {
            Logger.error("Local socks5 port is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfDownloadMbps.getText())) {
            Logger.error("Download mbps is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfUploadMbps.getText())) {
            Logger.error("Upload mbps is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfLimitMbps.getText())) {
            Logger.error("Limit mbps is Blank!");
            return;
        }


        InetAddress serverAddress = InetAddress.getByName(tfServer.getText());
        int serverPort = Integer.parseInt(tfServerPort.getText());
        int localSocks5Port = Integer.parseInt(tfLocalPort.getText());
        String serverPassword = pfPassword.getText();
        int downloadMbps = Integer.parseInt(tfDownloadMbps.getText());
        int uploadMbps = Integer.parseInt(tfUploadMbps.getText());

        try {
            clientConfig = new ProxyClientConfig(new InetSocketAddress(serverAddress, serverPort), localSocks5Port, serverPassword, downloadMbps, uploadMbps);
        } catch (EncryptionException e) {
            Logger.error("Init Proxy Config Failed: ", e);
            return;
        }

        try {
            proxyClient = new ProxyClient(clientConfig);
        } catch (IOException | InterruptedException | ServerRejectedException | IncorrectHeaderException | DragoniteException e) {
            Logger.error("DragoniteProxy Start Failed: ", e);
        }

    }

    @FXML
    public void dragoniteProxyStop(ActionEvent event) {

        proxyClient.close();
        Logger.info("DragoniteProxy Stoped...");

    }

    @FXML
    public void dragoniteProxySave(ActionEvent event) {
        Logger.info("Hello World!");
    }


    public void initLog() {

        PrintStream printStream = new PrintStream(new LogOutputStream(taLogs));
        System.setOut(printStream);
        System.setErr(printStream);
    }
}
