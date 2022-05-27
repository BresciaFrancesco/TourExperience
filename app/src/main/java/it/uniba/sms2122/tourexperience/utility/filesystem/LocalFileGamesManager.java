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

    public LocalFileGamesManager(final String path, String nomeMuseo,
                                 String nomeStanza, String nomeOpera) {
        super(path);
        // Capitalizzo le stringhe
        nomeMuseo = nomeMuseo.substring(0,1).toUpperCase() + nomeMuseo.substring(1).toLowerCase();
        nomeStanza = nomeStanza.substring(0,1).toUpperCase() + nomeStanza.substring(1).toLowerCase();
        nomeOpera = nomeOpera.substring(0,1).toUpperCase() + nomeOpera.substring(1).toLowerCase();
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
                    resultMessage = context.getString(R.string.file_json_importato);
                }
            }
            else {
                Log.e("IMPORT_QUIZ", "ALTRO NON PREVISTO");
            }
            return resultMessage;
        }
        catch (NullPointerException | IllegalArgumentException | JsonParseException e) {
            e.printStackTrace();
            return frag.requireContext().getString(R.string.quiz_incompatibile);
        }
        catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }


    /**
     *  Funzione per capire se una data opera ha o non ha il gioco trova le differenze
     * @return true se l'opera ha il gioco, false altrimenti
     */
    public boolean existsSpotTheDifference() {
        final File quizDir = new File(Paths.get(pathOperaForGames.toString(), "spotTheDifference").toString());
        if (quizDir.exists()) {
            final File quizConfigFile = new File(Paths.get(quizDir.toPath().toString(), "configuration.json").toString());
            return quizConfigFile.exists();
        }
        return false;
    }

    /**
     * Funzione per recuperare il json di configurazione del minigioco trova le differenze
     * @return il percoso completo del file json di configurazione
     * @throws IOException
     */
    public String loadSpotTheDifferenceConfigurationFile() throws IOException {
        List<String> stringhe = Files.readAllLines(Paths.get(pathOperaForGames.toString(), "spotTheDifference", "configuration.json"));
        StringBuilder sb = new StringBuilder();
        for (String s : stringhe) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Funzione per recuperare una singola immagine da settare nel gioco trova le differenze
     * @return il percoso completo dell'immagine che si vuole caricare sul gioco
     * @throws IOException
     */
    public String loadSpotTheDifferenceImageOf(String imageFileName) throws IOException {
        List<String> stringhe = Files.readAllLines(Paths.get(pathOperaForGames.toString(), "spotTheDifference", imageFileName + ".webp"));
        StringBuilder sb = new StringBuilder();
        for (String s : stringhe) {
            sb.append(s);
        }
        return sb.toString();
    }
}
