package za.nmu.wrpv;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Helpers {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDefaultFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.from(dateTime).format(dateFormatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDefaultFormattedTime(LocalDateTime dateTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.from(dateTime).format(timeFormatter);
    }

    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }
}
