package com.github.bitfexl.geoguessr.mailclient;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Client for <a href="https://10minutemail.com/">https://10minutemail.com/</a>.
 */
public class TenMinMailClient {
    private static final String URL_BASE = "https://10minutemail.com";
    private static final String PATH_GET_ADDRESS = "/session/address";
    private static final String PATH_SECONDS_LEFT = "/session/secondsLeft";
    private static final String PATH_EXPIRED = "/session/expired";
    private static final String PATH_MESSAGE_COUNT = "/messages/messageCount";
    private static final String PATH_MESSAGES_AFTER = "/messages/messagesAfter/"; // trailing "/" because index is appended

    private final OkHttpClient okHttpClient;

    /**
     * Current mail. Set by newMail().
     */
    private String mail;

    /**
     * Current session cookies. Set by newMail().
     */
    private String cookies;

    private int msgCount;

    public TenMinMailClient() {
        this.okHttpClient = new OkHttpClient();
    }

    /**
     * Creates a new ten-min-mail.
     * @return The newly created mail.
     * @throws IOException Error creating new mail.
     */
    public String newMail() throws IOException {
        final Request request = new Request.Builder()
                .url(URL_BASE + PATH_GET_ADDRESS)
                .get()
                .build();

        try (final Response response = okHttpClient.newCall(request).execute()) {
            this.cookies = response.header("set-cookie");
            this.mail = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("address").getAsString();
            this.msgCount = 0;
        } catch (Exception ex) {
            this.cookies = null;
            this.mail = null;
            throw new IOException("Error creating new mail.", ex);
        }

        return mail;
    }

    /**
     * Get all new mails since last call to getNewMails.
     * @return A list of all new received mails or an empty list.
     * @throws IOException Error reading new mails.
     */
    public List<MailResponse> getNewMails() throws IOException {
        final TypeToken<List<MailResponse>> typeToken = new TypeToken<>() {};

        try (final Response response = okHttpClient.newCall(request(PATH_MESSAGES_AFTER + msgCount)).execute()) {
            final String body = Objects.requireNonNull(response.body()).string();
            final List<MailResponse> newMails = new Gson().fromJson(body, typeToken.getType());
            msgCount += newMails.size();
            return newMails;
        } catch (Exception ex) {
            throw new IOException("Error reading new mails.", ex);
        }
    }

    /**
     * Checks if the mail is expired.
     * @return true: expired (do not use anymore, call newMail()), false: still active;
     * @throws IOException Error checking expired.
     */
    public boolean isExpired() throws IOException {
        try (final Response response = okHttpClient.newCall(request(PATH_EXPIRED)).execute()) {
            return JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("expired").getAsBoolean();
        } catch (Exception ex) {
            throw new IOException("Error checking expired.", ex);
        }
    }

    public String getMail() {
        return mail;
    }

    public String getCookies() {
        return cookies;
    }

    private Request request(String path) {
        return new Request.Builder()
                .url(URL_BASE + path)
                .header("cookie", cookies)
                .get()
                .build();
    }
}
