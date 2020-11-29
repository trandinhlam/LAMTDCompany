package com.lamtd.maven.hcmussdhcrawler.task;

import com.lamtd.maven.clientpool.ChatBotServiceClient;
import com.lamtd.maven.lamtddal.FeedNewsEntity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

/**
 * @author lamtd
 */
@Log4j2
public class HCMUSCrawlTask extends TimerTask {

    boolean isSend = false;

    @Override
    public void run() {

        int hour = Calendar.getInstance(TimeZone.getTimeZone("GMT+7")).getTime().getHours();
        int minute = Calendar.getInstance(TimeZone.getTimeZone("GMT+7")).getTime().getMinutes();
        if (!isSend) {
            isSend = true;
            ChatBotServiceClient.sendText("Like để đăng kí nhận tin nào");
        }
        while (hour < 24) {
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException ex) {
                Logger.getLogger(HCMUSCrawlTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.info("LOG DEMO");
        }
        log.info("HCMUS CRAWLER Made by LAMTD " + Calendar.getInstance().getTime().toString());
        if (hour == 7 && minute < 11) {
            ChatBotServiceClient.Instance.sendText("Like để đăng kí nhận tin nào");
        }

        try {
            log.info("HCMUS CRAWLER Made by LAMTD " + Calendar.getInstance().getTime().toString());
            String url = "https://sdh.hcmus.edu.vn/?s=";
            String html = getHTMLFromURL(url);
            if (html.isEmpty()) {
                log.error("get HTML failed");
                return;
            }
            //parse ra map các bài đăng có trong html
            TreeMap<Integer, FeedNewsEntity> news = _parseToNewsMap(html);
            //Lấy ra các bài mới nhất
            int lastId = _getLastSavedId();
            List<Integer> latestIds = news.keySet().stream().filter(i -> i > lastId).collect(Collectors.toList());
            if (latestIds.isEmpty()) {
                return;
            }
            for (Integer latestId : latestIds) {
                FeedNewsEntity t = news.get(latestId);
                //Gửi danh sách bài mới qua messenger
                _sendNews(t);
                log.info(t);
            }
            //lưu lại Id mới nhất của bài mới nhất bắt được
            _saveLastId(latestIds.stream().max(Integer::compareTo).get());
        } catch (IOException ex) {
            Logger.getLogger(HCMUSCrawlTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getHTMLFromURL(String url) {
        try {
            URL obj = new URL(url);
            StringBuilder response = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                log.error(url + responseCode);
            }
            return response.toString();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    private static final Pattern LI_MATCHER = Pattern.compile("post-([0-9]+)[\\s\\S]*<a href=\"([\\S]+)\" title=\"([^<]+)\"");

    private TreeMap<Integer, FeedNewsEntity> _parseToNewsMap(String html) {
        String[] split = html.split("<ul class=\"academia-posts-archive academia-loop-posts\">");
        TreeMap<Integer, FeedNewsEntity> rs = new TreeMap<>();

        String[] split1 = split[1].split("</li><!-- .academia-post -->");
        for (String li : split1) {
            try {
                //trong ul lọc ra từng li
                Matcher matcher = LI_MATCHER.matcher(li);
                if (matcher.find()) {
                    String id = matcher.group(1);
                    String link = matcher.group(2);
                    String title = matcher.group(3);
                    Integer idInt = Integer.valueOf(id);
                    log.info(link);
                    log.info(title);
                    rs.put(idInt, new FeedNewsEntity(idInt, title, link));
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return rs;
    }

    private void _sendNews(FeedNewsEntity t) {
        log.info(t.toString());
        ChatBotServiceClient.Instance.sendNews(t);

    }
    private static final String fileName = "store.txt";

    private int _getLastSavedId() throws IOException {
        File file = new File(fileName);
        BufferedReader input = new BufferedReader(new FileReader(file));
        String last = null, line;
        while ((line = input.readLine()) != null) {
            last = line;
        }
        if (last == null || last.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(last.trim());
    }

    private void _saveLastId(Integer lastId) throws IOException {
        File file = new File(fileName);
        //if file doesnt exists, then create it    
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter fileWritter = new FileWriter(file, false)) {
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(String.valueOf(lastId));
            bufferWritter.close();
        }
    }
}
