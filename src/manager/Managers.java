package manager;

import adapter.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;


public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("./resources/data.csv"));
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static Gson getGson(){
        GsonBuilder gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gson.create();
    }
}
