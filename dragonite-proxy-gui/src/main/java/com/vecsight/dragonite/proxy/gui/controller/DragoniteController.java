package com.vecsight.dragonite.proxy.gui.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vecsight.dragonite.proxy.config.ProxyClientConfig;
import com.vecsight.dragonite.proxy.exception.IncorrectHeaderException;
import com.vecsight.dragonite.proxy.exception.ServerRejectedException;
import com.vecsight.dragonite.proxy.gui.log.LogOutputStream;
import com.vecsight.dragonite.proxy.gui.module.GuiConfig;
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

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final String CONFIG_PATH = "./dragonite-proxy-gui.json";


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
        if (StringUtils.isBlank(tfLimitMbps.getText()) || !isNumeric(tfLimitMbps.getText())) {
            Logger.error("Limit mbps is blank or format is incorrect!");
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

            InputStream input = new FileInputStream(configFile);
            JsonReader reader = new JsonReader(new InputStreamReader(input));
            GuiConfig config = new Gson().fromJson(reader, GuiConfig.class);
            Logger.info(config);

            tfServer.setText(config.getServerAddress());
            tfServerPort.setText(config.getServerPort() + "");
            pfPassword.setText(config.getServerPassword());
            tfLocalPort.setText(config.getLocalSocks5Port() + "");
            tfDownloadMbps.setText(config.getDownloadMbps() + "");
            tfUploadMbps.setText(config.getUploadMbps() + "");
            tfLimitMbps.setText(config.getLimitMbps() + "");
            tfMTU.setText(config.getMTU() != null ? config.getMTU() + "" : "");

        } catch (FileNotFoundException e) {
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

            OutputStream out = new FileOutputStream(configFile);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            GuiConfig config = new GuiConfig()
                    .setServerAddress(StringUtils.isNotBlank(tfServer.getText()) ? tfServer.getText() : "www.google.com")
                    .setServerPort(StringUtils.isNotBlank(tfServerPort.getText()) ? Integer.parseInt(tfServerPort.getText()) : 9000)
                    .setServerPassword(StringUtils.isNotBlank(pfPassword.getText()) ? pfPassword.getText() : "uuT466wJr4RAfA7KZ7GB39XKHmBazgGs")
                    .setLocalSocks5Port(StringUtils.isNotBlank(tfLocalPort.getText()) ? Integer.parseInt(tfLocalPort.getText()) : 4050)
                    .setDownloadMbps(StringUtils.isNotBlank(tfDownloadMbps.getText()) ? Integer.parseInt(tfDownloadMbps.getText()) : 100)
                    .setUploadMbps(StringUtils.isNotBlank(tfUploadMbps.getText()) ? Integer.parseInt(tfUploadMbps.getText()) : 10)
                    .setLimitMbps(StringUtils.isNotBlank(tfLimitMbps.getText()) ? Integer.parseInt(tfLimitMbps.getText()) : 100)
                    .setMTU(StringUtils.isNotBlank(tfMTU.getText()) ? Integer.parseInt(tfMTU.getText()) : 1300);

            Logger.info(config);

            new Gson().toJson(config, new TypeToken<GuiConfig>() {
            }.getType(), writer);
            writer.flush();
            writer.close();
            out.close();

        } catch (Exception e) {
            Logger.error(e, "Config save Failed");
        }
    }


    public static boolean isNumeric(String str) {
        String regEx = "^[0-9]+$";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }
}


