package com.eulerian.android.sdk;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francois Rouault on 15/03/2015.
 */
class FileHelper {

    private static final String FILENAME = "eulerian.txt";
    private static final String FILENAME_TEMP = "eulerian-temp.text";
    private static final String SEPARATOR = System.getProperty("line.separator");

    static List<String> getLines() {
        List<String> res = new ArrayList<>();
        try {
            InputStream is = EAnalytics.getContext().openFileInput(FILENAME);
            if (is != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    res.add(line);
                }
                is.close();
            }
        } catch (IOException e) {
            EALog.e("Unable to get lines from file");
        }
        return res;
    }

    static boolean deleteLines(int numberOfLineToDelete) {
        File fileDir = EAnalytics.getContext().getFilesDir();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(EAnalytics.getContext().openFileInput
                    (FILENAME)));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(EAnalytics.getContext().openFileOutput
                    (FILENAME_TEMP, Context.MODE_APPEND)));

            int counter = -1;
            String line;
            while ((line = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                counter++;
                if (counter < numberOfLineToDelete) {
                    continue;
                }
                writer.write(line + SEPARATOR);
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            EALog.e("Unable to delete first line");
        }
        return new File(fileDir, FILENAME_TEMP).renameTo(new File(fileDir, FILENAME));
    }

    static void appendLine(String jsonProperties) {
        OutputStreamWriter osw;
        try {
            osw = new OutputStreamWriter(EAnalytics.getContext().openFileOutput(FILENAME, Context.MODE_APPEND));
            osw.append(jsonProperties);
            osw.append(SEPARATOR);
            osw.close();
            EALog.d("-> properties stored.");
        } catch (IOException e) {
            EALog.e("Unable to store properties: " + jsonProperties);
        }
    }

}
