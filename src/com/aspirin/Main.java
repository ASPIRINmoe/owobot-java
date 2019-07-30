/*                       _           _
                        | |         | |
       _____      _____ | |__   ___ | |_
      / _ \ \ /\ / / _ \| '_ \ / _ \| __|
     | (_) \ V  V / (_) | |_) | (_) | |_
      \___/ \_/\_/ \___/|_.__/ \___/ \__|
                                            v.1.1

    An anime pics bot for Telegram, written on java, taking data from reddit.
    Бот для телеграмма, присылающий аниме девочек, написанный на java, берущий данные из reddit.

    Ссылка на бота:  https://t.me/owopics_bot
    Или его юзернейм: @owopics_bot
    Автор: @realASPIRIN                                                                                */

package com.aspirin;

import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.Paginator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.prefs.Preferences;

public class Main {

    final static long StartTime = System.currentTimeMillis();

    final private static int PagesLimit = 25;                           //Лимит страниц. Хз нах это надо, не делайте много, будет долго листать.
    final private static SubredditSort sort = SubredditSort.HOT;        //Тип сортировки постов на реддите.
    final private static TimePeriod period = TimePeriod.WEEK;           //За какой период будет происхоть сортировка.
    final private static Integer postlimit = Paginator.DEFAULT_LIMIT;   //Максимум постов который будет браться с страници.
    final private static boolean EnableHttpLog = false;                 //true - показывать всю активность с реддитом, false - ничего не показывать

    static Properties prop = new Properties();

    public static void main(String[] args) throws FileNotFoundException {

        try (InputStream input = new FileInputStream("config.properties")) {
            System.out.println("Reading config file");
            prop.load(input);
        } catch (IOException ex) {
            try (OutputStream output = new FileOutputStream("config.properties")) {
                System.out.println("Creating config file");

                prop.setProperty("Reddit_Username", "owonickname");
                prop.setProperty("Reddit_Password", "password");
                prop.setProperty("Reddit_Clientid", "someletters");
                prop.setProperty("Reddit_Clientsecret", "moresomeletters");

                prop.setProperty("BOT_USERNAME", "botname_bot");
                prop.setProperty("BOT_TOKEN", "numbers:lettersandnumbers");
                prop.setProperty("BOT_FEEDBACKID", "numbers");

                prop.store(output, "owobot-java, an anime bot for telegram. Github: https://github.com/ASPIRINswag/owobot-java");

                System.out.println("Please, setup config before next run!");
                System.exit(0);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        try {
            readPreference("UsedTimes");
        } catch (Exception e) {
            savePreference("UsedTimes", 0);
        }

        System.setErr(new PrintStream(new File("log.txt"))); //лог ошибок, не ваших данных.
        new RedditAPI(PagesLimit, sort, period, postlimit, EnableHttpLog);
        System.err.println("Logfile start: " + (new Date()).toString());
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        Bot bot = new Bot();

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        } finally {

            System.out.println("Bot has successfully started");
        }
    }


    static void savePreference(String key, Integer value) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        prefs.put(key, String.valueOf(value));
    }

    static Integer readPreference(String key) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        return Integer.valueOf(prefs.get(key, "default"));
    }
}