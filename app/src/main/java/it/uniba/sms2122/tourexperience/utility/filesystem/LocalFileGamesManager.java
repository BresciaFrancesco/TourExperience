package it.uniba.sms2122.tourexperience.utility.filesystem;

import static it.uniba.sms2122.tourexperience.utility.filesystem.zip.MimeType.JSON;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.quiz.Quiz;
import it.uniba.sms2122.tourexperience.games.quiz.dto.QuizJson;
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.OperaFragment;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

public class LocalFileGamesManager extends LocalFilePercorsoManager {
    private final Path pathOperaForGames;

    public LocalFileGamesManager(final String generalPath, final String nomeMuseo,
                                 final String nomeStanza, final String nomeOpera) {
        super(generalPath);
        this.pathOperaForGames = Paths.get(generalPath, nomeMuseo, "Stanze", nomeStanza, nomeOpera);
    }

    public boolean existsQuiz() {
        final File quizDir = new File(Paths.get(pathOperaForGames.toString(), "Quiz").toString());
        if (quizDir.exists()) {
            final File quizConfigFile = new File(Paths.get(quizDir.toPath().toString(), "Config.json").toString());
            return quizConfigFile.exists();
        }
        return false;
    }

    public String loadQuizJson() throws IOException {
        List<String> stringhe = Files.readAllLines(Paths.get(pathOperaForGames.toString(), "Quiz", "Config.json"));
        StringBuilder sb = new StringBuilder();
        for (String s : stringhe) {
            sb.append(s);
        }
        return sb.toString();
    }

    public boolean createQuizDir() {
        final File quizDir = Paths.get(pathOperaForGames.toString(), "Quiz").toFile();
        if (!quizDir.exists()) {
            return quizDir.mkdir();
        }
        return true;
    }

    public String saveQuizJson(final String mimeType, final OpenFile dto, final OperaFragment frag) {
        try {
            final Context context = frag.requireContext();
            String resultMessage = context.getString(R.string.mime_type_error);
            if (mimeType.equals(JSON.mimeType())) {
                try ( Reader reader = new InputStreamReader(dto.openFile()) ) {
                    final QuizJson quizJson = gson.fromJson(reader, QuizJson.class);
                    Quiz.buildFromJson(quizJson);
                    // write file in local
                    if (!createQuizDir()) {
                        throw new IOException("Quiz dir non creabile");
                    }
                    String pathToWrite = Paths.get(pathOperaForGames.toString(), "Quiz", "Config.json").toString();
                    File configQuizFile = new File(pathToWrite);
                    if (configQuizFile.exists()) {
                        if (!configQuizFile.delete()) {
                            throw new IOException("Impossibile eliminare il file quiz precedente.");
                        }
                    }
                    if (!configQuizFile.createNewFile()) {
                        throw new IOException("File quiz non creato");
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(pathToWrite));
                    writer.write(gson.toJson(quizJson));
                    writer.close();
                    resultMessage = "File json importato correttamente";
                }
            }
            else {
                Log.e("IMPORT_QUIZ", "ALTRO NON PREVISTO");
            }
            return resultMessage;
        }
        catch (NullPointerException | IllegalArgumentException | JsonParseException e) {
            e.printStackTrace();
            return "Il quiz importato non è compatibile";
        }
        catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
