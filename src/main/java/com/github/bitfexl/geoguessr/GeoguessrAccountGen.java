package com.github.bitfexl.geoguessr;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.regex.Pattern;

public class GeoguessrAccountGen {
    private static final String URL_BASE = "https://www.geoguessr.com";
    private static final String PATH_SIGNUP = "/api/v3/accounts/signup";
    private static final String PATH_SET_PASSWORD = "/api/v3/profiles/setpassword";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient okHttpClient;

    public GeoguessrAccountGen() {
        this.okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
    }

    /**
     * Requests the activation link (new account) for a given email.
     * @param mail The mail to register. Cannot contain '"'.
     * @return true: activation link request successful, false: :(
     * @throws IOException Error creating new account.
     */
    public boolean createAccount(String mail) throws IOException {
        if (mail.contains("\"")) {
            throw new IllegalArgumentException("Mail cannot contain '\"'.");
        }

        final String body = "{\"email\": \"" + mail + "\"}";

        final Request request = new Request.Builder()
                .url(URL_BASE + PATH_SIGNUP)
                .post(RequestBody.create(body, JSON))
                .build();

        try (final Response response = okHttpClient.newCall(request).execute()) {
            return response.code() == 200;
        } catch (Exception ex) {
            throw new IOException("Error creating new account.", ex);
        }
    }

    public boolean setPassword(String activationUrl, String password) throws IOException {
        final Request getToken = new Request.Builder()
                .url(activationUrl)
                .get()
                .build();

        String token;
        try (final Response response = okHttpClient.newCall(getToken).execute()) {
            // response is going to be a redirect, token in location header
            token = response.header("Location").split(Pattern.quote("https://www.geoguessr.com/profile/set-password/"))[1];
        } catch (Exception ex) {
            throw new IOException("Error requesting token with activation url.", ex);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("token", JsonParser.parseString(token));
        jsonObject.add("password", JsonParser.parseString(password));
        final String body = jsonObject.toString();

        final Request setPassword = new Request.Builder()
                .url(URL_BASE + PATH_SET_PASSWORD)
                .post(RequestBody.create(body, JSON))
                .build();

        try (final Response response = okHttpClient.newCall(setPassword).execute()) {
            return response.code() == 200;
        } catch (Exception ex) {
            throw new IOException("Error setting new password.", ex);
        }
    }
}
