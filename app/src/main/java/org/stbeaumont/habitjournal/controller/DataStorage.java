package org.stbeaumont.habitjournal.controller;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.stbeaumont.habitjournal.model.Habit;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class DataStorage {

    private Gson gson;
    private String filename = "habitjournal_data";
    private Context context;

    public DataStorage(Context context) {
        this.context = context;

        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());

        gson = builder.create();

    }

    /* saveData: This method converts the users data to a json
     * string and saves the data to internal storage */
    public void updateData(ArrayList <Habit> habits) {
        String goalJson = gson.toJson(habits);

        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(goalJson.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Habit> loadData() {
        ArrayList<Habit> habits = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(filename);
            Scanner scanner = new Scanner(fis);
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            scanner.close();
            fis.close();

            Type listType = new TypeToken<ArrayList<Habit>>(){}.getType();
            habits = gson.fromJson(content, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return habits;
    }

    class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("\"yyyy-MM-dd\"");
            String jsonString = json.toString();
            return LocalDate.parse(jsonString, formatter);
        }
    }
}