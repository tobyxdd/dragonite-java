package com.vecsight.dragonite.proxy.gui.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.vecsight.dragonite.proxy.config.ProxyClientConfig;
import com.vecsight.dragonite.proxy.exception.IncorrectHeaderException;
import com.vecsight.dragonite.proxy.exception.ServerRejectedException;
import com.vecsight.dragonite.proxy.gui.log.LogOutputStream;
import com.vecsight.dragonite.proxy.gui.module.GuiConfig;
import com.vecsight.dragonite.proxy.network.client.ProxyClient;
import com.vecsight.dragonite.sdk.exception.DragoniteException;
import com.vecsight.dragonite.sdk.exception.EncryptionException;
import io.datafx.controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/*******************************************************************************
 * Copyright (c) 2005-2017 Mritd, Inc.
 * dragonite
 * com.vecsight.dragonite.proxy.gui.controller
 * Created by mritd on 17/11/28 下午9:40.
 * Description: DragoniteController
 *******************************************************************************/
@ViewController(value = "/DragoniteController.fxml", title = "DragoniteX")
public class DragoniteController {
    @FXML
    private JFXTextField tfServer;
    @FXML
    private JFXTextField tfServerPort;
    @FXML
    private JFXTextField tfLocalPort;
    @FXML
    private JFXPasswordField pfPassword;
    @FXML
    private JFXTextField tfDownloadMbps;
    @FXML
    private JFXTextField tfUploadMbps;
    @FXML
    private JFXTextField tfMTU;
    @FXML
    private JFXTextArea taLogs;

    private ProxyClientConfig clientConfig;
    private ProxyClient proxyClient;

    public static final String CONFIG_PATH = "./dragonite-proxy-gui.json";


    public void init() {
        initValidate();
        initLog();
        loadConfig();
    }


    public void initValidate() {

        tfServer.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfServer.validate();
            }
        });
        tfServerPort.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfServerPort.validate();
            }
        });
        tfLocalPort.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfLocalPort.validate();
            }
        });
        pfPassword.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                pfPassword.validate();
            }
        });
        tfDownloadMbps.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfDownloadMbps.validate();
            }
        });
        tfUploadMbps.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfUploadMbps.validate();
            }
        });
        tfMTU.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tfMTU.validate();
            }
        });
    }


    @FXML
    public void dragoniteProxyStart(ActionEvent event) throws UnknownHostException {

        if (StringUtils.isBlank(tfServer.getText())) {
            Logger.error("Server address is blank!");
            return;
        }
        if (StringUtils.isBlank(pfPassword.getText())) {
            Logger.error("Server password is blank! ");
            return;
        }
        if (StringUtils.isBlank(tfServerPort.getText()) || !isNumeric(tfServerPort.getText())) {
            Logger.error("Server port is blank or format is incorrect!");
            return;
        }
        if (StringUtils.isBlank(tfLocalPort.getText()) || !isNumeric(tfLocalPort.getText())) {
            Logger.error("Local socks5 port is blank or format is incorrect!");
            return;
        }
        if (StringUtils.isBlank(tfDownloadMbps.getText()) || !isNumeric(tfDownloadMbps.getText())) {
            Logger.error("Download mbps is blank or format is incorrect!");
            return;
        }
        if (StringUtils.isBlank(tfUploadMbps.getText()) || !isNumeric(tfUploadMbps.getText())) {
            Logger.error("Upload mbps is blank or format is incorrect!");
            return;
        }

        if (StringUtils.isNotBlank(tfMTU.getText()) && !isNumeric(tfMTU.getText())) {
            Logger.error("MTU format is incorrect!");
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
            clientConfig.setMTU(StringUtils.isNotBlank(tfMTU.getText()) ? Integer.parseInt(tfMTU.getText()) : 1300);
            proxyClient = new ProxyClient(clientConfig);
        } catch (EncryptionException | IOException | ServerRejectedException | InterruptedException | DragoniteException | IncorrectHeaderException e) {
            Logger.error(e, "DragoniteProxy Start Failed");
        }

    }

    @FXML
    public void dragoniteProxyStop(ActionEvent event) {

        if (proxyClient != null) proxyClient.close();
        Logger.info("DragoniteProxy Stoped!");

    }

    @FXML
    public void dragoniteProxySave(ActionEvent event) {

        saveConfig();
        Logger.info("Config saved...");

    }


    public void initLog() {

        PrintStream printStream = new PrintStream(new LogOutputStream(taLogs));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    public void loadConfig() {


        try {

            File configFile = new File(CONFIG_PATH);

            if (!configFile.exists()) {
                Logger.warn("Config file not found!");
                return;
            }

            @Cleanup InputStream input = new FileInputStream(configFile);
            @Cleanup JsonReader reader = new JsonReader(new InputStreamReader(input));
            GuiConfig config = new Gson().fromJson(reader, GuiConfig.class);
            Logger.info(config);

            tfServer.setText(config.getServerAddress());
            tfServerPort.setText(config.getServerPort() + "");
            pfPassword.setText(config.getServerPassword());
            tfLocalPort.setText(config.getLocalSocks5Port() + "");
            tfDownloadMbps.setText(config.getDownloadMbps() + "");
            tfUploadMbps.setText(config.getUploadMbps() + "");
            tfMTU.setText(config.getMTU() != null ? config.getMTU() + "" : "");

        } catch (IOException e) {
            Logger.error(e, "Load Config Error");
        }

    }

    public void saveConfig() {

        try {

            File configFile = new File(CONFIG_PATH);
            if (!configFile.exists()) {
                Logger.warn("Config file not found!");
                if (configFile.createNewFile()) {
                    Logger.info("Config file crate success!");
                } else {
                    Logger.error("Config file create failed!");
                    return;
                }
            }

            @Cleanup OutputStream out = new FileOutputStream(configFile);
            @Cleanup JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            GuiConfig config = new GuiConfig()
                    .setServerAddress(StringUtils.isNotBlank(tfServer.getText()) ? tfServer.getText() : "google.com")
                    .setServerPort(StringUtils.isNotBlank(tfServerPort.getText()) ? Integer.parseInt(tfServerPort.getText()) : 9000)
                    .setServerPassword(StringUtils.isNotBlank(pfPassword.getText()) ? pfPassword.getText() : "jFThJnp2hppzzPJy")
                    .setLocalSocks5Port(StringUtils.isNotBlank(tfLocalPort.getText()) ? Integer.parseInt(tfLocalPort.getText()) : 5234)
                    .setDownloadMbps(StringUtils.isNotBlank(tfDownloadMbps.getText()) ? Integer.parseInt(tfDownloadMbps.getText()) : 100)
                    .setUploadMbps(StringUtils.isNotBlank(tfUploadMbps.getText()) ? Integer.parseInt(tfUploadMbps.getText()) : 10)
                    .setMTU(StringUtils.isNotBlank(tfMTU.getText()) ? Integer.parseInt(tfMTU.getText()) : 1300);

            Logger.info(config);

            new Gson().toJson(config, new TypeToken<GuiConfig>() {
            }.getType(), writer);
            writer.flush();

        } catch (IOException e) {
            Logger.error(e, "Config save Failed");
        }
    }


    private boolean isNumeric(String str) {
        String regEx = "^[0-9]+$";
        return Pattern.compile(regEx).matcher(str).find();
    }
}


