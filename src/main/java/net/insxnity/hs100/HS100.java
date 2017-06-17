package net.insxnity.hs100;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
 
/**
 * TP-Link HS100
 *
 * @author Insxnity
 * @copyright Copyright (c) 2016, Insxnity Development
 */
public class HS100 {
 
    public static final String COMMAND_SWITCH_ON = "{\"system\":{\"set_relay_state\":{\"state\":1}}}}";
    public static final String COMMAND_SWITCH_OFF = "{\"system\":{\"set_relay_state\":{\"state\":0}}}}";
    public static final String COMMAND_INFO = "{\"system\":{\"get_sysinfo\":null}}";
 
    /**
     * ON Status
     */
    public static final int STATE_ON = 1;
 
    /**
     * OFF Status
     */
    public static final int STATE_OFF = 2;
 
    /**
     * IP of the Plug
     */
    private String ip;
 
    /**
     * Port
     */
    private int port = 9999;
 
    /**
     * @param ip IP Address
     */
    public HS100(String ip) {
        this.ip = ip;
    }
 
    /**
     * @param ip IP Address
     * @param port TCP Port Number
     */
    public HS100(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
 
    /**
     * get current IP Address of the Plug
     *
     * @return IP Address
     */
    public String getIp() {
        return ip;
    }
 
    /**
     * set IP Address of the Plug
     *
     * @param ip IP Address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
 
    /**
     * get current Port of the Plug
     *
     * @return Port
     */
    public int getPort() {
        return port;
    }
 
    /**
     * set port of the Plug
     *
     * @param port Port
     */
    public void setPort(int port) {
        this.port = port;
    }
 
    /**
     * return if the Plug is valid and present
     *
     * @return present
     */
    public boolean isPresent() {
 
        try {
 
            InetAddress ip = InetAddress.getByName(getIp());
            return ip.isReachable(500);
        } catch (IOException ex) {}
        return false;
    }
 
    /**
     * send "On" signal to plug
     *
     * @return true if successful 
     */
    public boolean switchOn() throws IOException {
 
        String jsonData = sendCommand(COMMAND_SWITCH_ON);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int errorCode = jo.get("system").getAsJsonObject().get("set_relay_state").getAsJsonObject().get("err_code").getAsInt();
            return errorCode == 0;
        }
        return false;
    }
 
    /**
     * send "Off" signal to plug
     *
     * @return true if successful
     */
    public boolean switchOff() throws IOException {
 
        String jsonData = sendCommand(COMMAND_SWITCH_OFF);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int errorCode = jo.get("system").getAsJsonObject().get("set_relay_state").getAsJsonObject().get("err_code").getAsInt();
            return errorCode == 0;
        }
        return false;
    }
 
    /**
     * check if the plug is on
     *
     * @return STATE_ON oder STATE_OFF
     */
    public boolean isOn() throws IOException {
 
        String jsonData = sendCommand(COMMAND_INFO);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            int state = jo.get("system").getAsJsonObject().get("get_sysinfo").getAsJsonObject().get("relay_state").getAsInt();
            return state == 1 ? true : false;
        }
        return false;
    }
 
    /**
     * Return a map containing plug system information
     *
     * @return Map of Information
     */
    public Map<String, String> getInfo() throws IOException {
 
        Map<String, String> result = new HashMap<>();
        String jsonData = sendCommand(COMMAND_INFO);
        if(jsonData.length() > 0) {
 
            JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
            JsonObject systemInfo = jo.get("system").getAsJsonObject().get("get_sysinfo").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : systemInfo.entrySet()) {
 
                result.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return result;
    }
 
    /**
     * send <code>command</code> to plug
     *
     * @param command Command
     * @return Json String of the returned data
     * @throws IOException
     */
    protected String sendCommand(String command) throws IOException {
 
        Socket socket = new Socket(getIp(), 9999);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(encryptWithHeader(command));
 
        InputStream inputStream = socket.getInputStream();
        String data = decrypt(inputStream);
 
        outputStream.close();
        inputStream.close();
        socket.close();
 
        return data;
    }
    
    
    /**
     * Decrypt given data from InputStream
     *  
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String decrypt(InputStream inputStream) throws IOException {
 
        int in;
        int key = 0x2B;
        int nextKey;
        StringBuilder sb = new StringBuilder();
        while((in = inputStream.read()) != -1) {
 
            nextKey = in;
            in = in ^ key;
            key = nextKey;
            sb.append((char) in);
        }
        return "{" + sb.toString().substring(5);
    }
    /**
     * Encrypt a command into plug-readable bytecode
     * 
     * @param command
     * @return
     */
    private int[] encrypt(String command) {
 
        int[] buffer = new int[command.length()];
        int key = 0xAB;
        for(int i = 0; i < command.length(); i++) {
 
            buffer[i] = command.charAt(i) ^ key;
            key = buffer[i];
        }
        return buffer;
    }
    /**
     * Encrypt a command into plug-readable bytecode with header
     * 
     * @param command
     * @return
     */
    private byte[] encryptWithHeader(String command) {
 
        int[] data = encrypt(command);
        byte[] bufferHeader = ByteBuffer.allocate(4).putInt(command.length()).array();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferHeader.length + data.length).put(bufferHeader);
        for(int in : data) {
 
            byteBuffer.put((byte) in);
        }
        return byteBuffer.array();
    }
}