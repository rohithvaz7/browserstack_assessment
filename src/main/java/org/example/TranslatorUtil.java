package org.example;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import java.util.*;

public class TranslatorUtil {
    private static final String AUTH_KEY = "bcb86924-a195-4fec-a0f3-b13507a80df9:fx"; // Replace with your actual API key

    public static List<String> translateTitles(List<String> spanishTitles) {
        List<String> translatedTitles = new ArrayList<>();

        try {
            // ✅ Pass the API key when initializing the translator
            Translator translator = new Translator(AUTH_KEY);
            System.out.println("\n🔹 Translated Titles:");

            for (String title : spanishTitles) {
                TextResult translatedText = translator.translateText(title, null, "EN");
                translatedTitles.add(translatedText.getText());
                System.out.println(translatedText.getText());
            }
        } catch (DeepLException | InterruptedException e) {
            e.printStackTrace();
        }

        return translatedTitles;
    }
}
