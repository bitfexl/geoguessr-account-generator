package com.github.bitfexl.geoguessr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class GeoguessrTutorialCompleter {
    /**
     * Class for tutorial step completion json.
     */
    private static class RequestJson {
        public boolean timedOut = false;
        public String token;
        public double lat;
        public double lng;

        public RequestJson(String token, double lat, double lng) {
            this.token = token;
            this.lat = lat;
            this.lng = lng;
        }
    }

    private static final String URL_BASE = "https://www.geoguessr.com";
    private static final String PATH_SIGN_IN = "/api/v3/accounts/signin";
    private static final String PATH_START_TUTORIAL = "/api/v3/games/tutorial";
    private static final String PATH_GAMES = "/api/v3/games/"; // + game id

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient okHttpClient;


    private final String mail;
    private final String pass;

    private String cookies;

    public GeoguessrTutorialCompleter(String mail, String pass) {
        this.mail = mail;
        this.pass = pass;
        this.okHttpClient = new OkHttpClient();
    }

    public void completeTutorial() throws IOException {
        // 1. sign in (cookies, "_ncfa")
        getCookies();

        // 2. complete tutorial
        // get token
        final String token = getTutorialToken();

        // the 3 tutorial steps hard coded
        completeTutorialStep(token, 48.8562112, 2.2976245);
        completeTutorialStep(token, 40.6886071, -74.0441929);
        completeTutorialStep(token, -33.8603713, 151.2171988);
    }

    private void completeTutorialStep(String token, double lat, double lng) throws IOException {
        final RequestJson json = new RequestJson(token, lat, lng);
        final String body = new Gson().toJson(json);

        final Request startGuess = new Request.Builder()
                .url(URL_BASE + PATH_GAMES + token + "?client=web")
                .header("cookie", cookies)
                .get()
                .build();

        final Request request = new Request.Builder()
                .url(URL_BASE + PATH_GAMES + token)
                .header("cookie", cookies)
                .post(RequestBody.create(body, JSON))
                .build();

        okHttpClient.newCall(startGuess).execute().close();

        try (final Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                throw new Exception("Call returned with http status code " + response.code() + ".");
            }
        } catch (Exception ex) {
            throw new IOException("Error completing guessing step (tutorial).", ex);
        }
    }

    private String getTutorialToken() throws IOException {
        final Request request = new Request.Builder()
                .url(URL_BASE + PATH_START_TUTORIAL)
                .header("cookie", cookies)
                .post(RequestBody.create("{}", JSON))
                .build();

        try (final Response response = okHttpClient.newCall(request).execute()) {
            JsonObject jsonObject = (JsonObject)JsonParser.parseString(Objects.requireNonNull(response.body()).string());
            return jsonObject.get("token").getAsString();
        } catch (Exception ex) {
            throw new IOException("Error getting tutorial token.", ex);
        }
    }

    private void getCookies() throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("email", JsonParser.parseString(mail));
        jsonObject.add("password", JsonParser.parseString(pass));
        final String body = jsonObject.toString();

        final Request login = new Request.Builder()
                .url(URL_BASE + PATH_SIGN_IN)
                .post(RequestBody.create(body, JSON))
                .build();

        try (final Response response = okHttpClient.newCall(login).execute()) {
            cookies = response.header("set-cookie").split(" ")[0];
        } catch (Exception ex) {
            throw new IOException("Error logging in (tutorial).", ex);
        }
    }
}
