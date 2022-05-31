package it.uniba.sms2122.tourexperience.utility.filesystem;

import static it.uniba.sms2122.tourexperience.utility.filesystem.zip.MimeType.JSON;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        pathOperaForGames = buildGeneralPath(generalPath, new String[] {nomeMuseo, "Stanze", nomeStanza, nomeOpera});
    }

    /**
     * Controlla se esiste la cartella Quiz nel path dell'opera e successivamente
     * il file json di configurazione Config.json.
     * @return true se esiste tutto, false altrimenti.
     */
    public boolean existsQuiz() {
        final File quizDir = new File(Paths.get(pathOperaForGames.toString(), "Quiz").toString());
        if (quizDir.exists()) {
            final File quizConfigFile = Paths.get(quizDir.toPath().toString(), "Config.json").toFile();
            return quizConfigFile.exists();
        }
        return false;
    }

    /**
     * Legge un file json riga per riga e lo converte in una stringa.
     * @return stringa rappresentante l'intero file json letto.
     * @throws IOException
     */
    public String loadQuizJson() throws IOException {
        List<String> stringhe = Files.readAllLines(Paths.get(pathOperaForGames.toString(), "Quiz", "Config.json"));
        StringBuilder sb = new StringBuilder();
        for (String s : stringhe) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Crea la cartella Quiz nel path dell'opera. Se la cartella esiste già,
     * non la crea e restituisce true.
     * @return true se l'ha creata o già esisteva, false altrimenti.
     */
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
        final File spotTheDifferenceDir = new File(Paths.get(pathOperaForGames.toString(), "spotTheDifference").toString());
        if (spotTheDifferenceDir.exists()) {
            final File quizConfigFile = Paths.get(spotTheDifferenceDir.toPath().toString(), "configuration.json").toFile();
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
    public Bitmap loadSpotTheDifferenceImageOf(String imageFileName) throws IOException {

        Bitmap image = BitmapFactory.decodeFile(Paths.get(pathOperaForGames.toString(), "spotTheDifference", imageFileName + ".webp").toString());
        return image;
    }
}
