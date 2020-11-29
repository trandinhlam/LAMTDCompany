package com.lamtd.maven.chatbotservice;

import com.google.gson.JsonObject;
import com.lamtd.maven.lamtddal.FeedNewsEntity;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lamtd
 */
@Log4j2
@RestController("/")
public class ChatBotController {

    static final String API_CHAT = "https://graph.facebook.com/v8.0/me/messages?access_token=EAAEbPuiypIIBAMRpV7knR5CHVydqEOJZAxg9rjGIo6OEg2ipSIGMJbMuhh43bTeIKqBltIvt1DYaTjoZBhpR8CBCDh9h6oegwENsWiL2vIpQJUmdTmcutmPgeJh2PiULSY1itkt28JgpXZASZCMY3MgCzfuojSierUQaOiOWQAZDZD";

    static final String tdlam123 = "3748382801858136";

    static final String ACCESS_TOKEN = "EAAEbPuiypIIBAMRpV7knR5CHVydqEOJZAxg9rjGIo6OEg2ipSIGMJbMuhh43bTeIKqBltIvt1DYaTjoZBhpR8CBCDh9h6oegwENsWiL2vIpQJUmdTmcutmPgeJh2PiULSY1itkt28JgpXZASZCMY3MgCzfuojSierUQaOiOWQAZDZD";

    public String send(FeedNewsEntity news) {
        return sendText(String.format("[%d] [%s] %s", news.getId(), news.getLink(), news.getTitle().replaceAll("Permalink to ", "")));
    }

    @GetMapping("/sendText")
    public String sendText(@RequestParam String text) {
        try {
            JsonObject data = new JsonObject();

            JsonObject recipient = new JsonObject();
            data.add("recipient", recipient);
            recipient.addProperty("id", tdlam123);

            JsonObject message = new JsonObject();
            data.add("message", message);
            message.addProperty("text", text);

            return sendPostJSON(API_CHAT, data);
        } catch (Exception ex) {
            log.error(ex);
            return "Exception";
        }
    }

    public static String sendPostJSON(String url, JsonObject data) throws Exception {
        String srtParams = data.toString();
        byte[] postData = srtParams.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        URL obj = new URL(url);
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            return String.valueOf(responseCode);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        return response.toString();
    }
}
