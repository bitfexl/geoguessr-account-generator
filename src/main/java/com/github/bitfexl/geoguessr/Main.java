package com.github.bitfexl.geoguessr;

import com.github.bitfexl.geoguessr.mailclient.MailResponse;
import com.github.bitfexl.geoguessr.mailclient.TenMinMailClient;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        TenMinMailClient tenMinMailClient = new TenMinMailClient();
        GeoguessrAccountGen geoguessrAccountGen = new GeoguessrAccountGen();

        final String mail = tenMinMailClient.newMail();
        log("mail: " + mail);

        if (!geoguessrAccountGen.createAccount(mail)) {
            log("Error requesting activation mail.");
            System.exit(1);
        }

        List<MailResponse> mails;
        do {
            Thread.sleep(1000);
            mails = tenMinMailClient.getNewMails();
        } while (mails.isEmpty());

        final String activationUrl = mails.get(0).getBodyHtmlContent().split("href=\"")[1].split("\"")[0];

        // set mail as password
        if (geoguessrAccountGen.setPassword(activationUrl, mail)) {
            log("pass: " + mail);
        } else {
            log("Error setting password.");
            System.exit(1);
        }

        log();
        log("----------------------------");
        log(mail);
        log("----------------------------");
        log();

        GeoguessrTutorialCompleter tutorial = new GeoguessrTutorialCompleter(mail, mail);

        log("Completing tutorial...");

        final long startTimeMs = System.currentTimeMillis();
        tutorial.completeTutorial();
        final long durationMs = System.currentTimeMillis() - startTimeMs;

        log("Completed tutorial in " + durationMs / 1000d + "s");
    }

    private static void log() {
        log("");
    }

    private static void log(String s) {
        System.out.println(s);
    }
}
