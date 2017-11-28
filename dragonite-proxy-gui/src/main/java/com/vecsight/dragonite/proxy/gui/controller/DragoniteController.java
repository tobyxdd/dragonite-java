package com.vecsight.dragonite.proxy.gui.controller;

import com.vecsight.dragonite.proxy.config.ProxyClientConfig;
import com.vecsight.dragonite.proxy.exception.IncorrectHeaderException;
import com.vecsight.dragonite.proxy.exception.ServerRejectedException;
import com.vecsight.dragonite.proxy.gui.log.TextAreaOutputStream;
import com.vecsight.dragonite.proxy.gui.log.TextAreaStaticOutputStreamAppender;
import com.vecsight.dragonite.proxy.network.client.ProxyClient;
import com.vecsight.dragonite.sdk.exception.DragoniteException;
import com.vecsight.dragonite.sdk.exception.EncryptionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
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
@Slf4j
public class DragoniteController {
    @FXML
    private TextField tfServer;
    @FXML
    private TextField tfServerPort;
    @FXML
    private TextField tfLoadlPort;
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
            log.error("Server address is Blank!");
            return;
        }
        if (StringUtils.isBlank(pfPassword.getText())) {
            log.error("Server password is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfServerPort.getText())) {
            log.error("Server port is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfLoadlPort.getText())) {
            log.error("Local socks5 port is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfDownloadMbps.getText())) {
            log.error("Download mbps is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfUploadMbps.getText())) {
            log.error("Upload mbps is Blank!");
            return;
        }
        if (StringUtils.isBlank(tfLimitMbps.getText())) {
            log.error("Limit mbps is Blank!");
            return;
        }


        InetAddress serverAddress = InetAddress.getByName(tfServer.getText());
        int serverPort = Integer.parseInt(tfServerPort.getText());
        int localSocks5Port = Integer.parseInt(tfLoadlPort.getText());
        String serverPassword = pfPassword.getText();
        int downloadMbps = Integer.parseInt(tfDownloadMbps.getText());
        int uploadMbps = Integer.parseInt(tfUploadMbps.getText());

        try {
            clientConfig = new ProxyClientConfig(new InetSocketAddress(serverAddress, serverPort), localSocks5Port, serverPassword, downloadMbps, uploadMbps);
        } catch (EncryptionException e) {
            log.error("Init Proxy Config Failed: ", e);
            return;
        }

        try {
            proxyClient = new ProxyClient(clientConfig);
        } catch (IOException | InterruptedException | ServerRejectedException | IncorrectHeaderException | DragoniteException e) {
            log.error("DragoniteProxy Start Failed: ", e);
        }

    }

    @FXML
    public void dragoniteProxyStop(ActionEvent event) {

        log.info("DragoniteProxy Stoped...");


    }

    @FXML
    public void dragoniteProxySave(ActionEvent event) {
        log.info("Hello World!");
    }


    public void initLog() {
        OutputStream os = new TextAreaOutputStream(taLogs);
        TextAreaStaticOutputStreamAppender.setStaticOutputStream(os);

    }
}
