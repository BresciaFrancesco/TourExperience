package it.uniba.sms2122.tourexperience.percorso.pagina_opera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.database.CacheGames;
import it.uniba.sms2122.tourexperience.database.GameTypes;
import it.uniba.sms2122.tourexperience.games.SpotDifference.SpotDifferences;
import it.uniba.sms2122.tourexperience.percorso.ImageAndDescriptionFragment;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileGamesManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.DTO.OpenFileAndroidStorageDTO;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;


public class OperaFragment extends Fragment {

    public static final String OPERA_JSON = "OperaJson";
    public static final String NOME_STANZA = "NomeStanza";
    public static final String NOME_MUSEO = "NomeMuseo";

    private static final String OPERA_ID = "id";
    private static final String OPERA_NOME = "nome";
    private static final String OPERA_PERCORSO_IMG = "percorsoImg";
    private static final String OPERA_DESCRIZIONE = "descrizione";

    private Opera opera;
    private String nomeMuseo;
    private String nomeStanza;
    private LocalFileGamesManager localFileGamesManager;
    private final int requestCodeGC = 900007;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!ripristino(savedInstanceState)) {
            final Bundle bundle = getArguments();
            final String operaJson = Objects.requireNonNull(bundle).getString(OPERA_JSON);
            this.opera = new Gson().fromJson(Objects.requireNonNull(operaJson), Opera.class);
            this.nomeMuseo = bundle.getString(NOME_MUSEO);
            this.nomeStanza = bundle.getString(NOME_STANZA);
        }
        setActionBar(opera.getNome());
        FragmentManager fragmentManager = getParentFragmentManager();
        ImageAndDescriptionFragment fragment = new ImageAndDescriptionFragment(opera.getPercorsoImg(), opera.getDescrizione());
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.imageanddescription_fragment_container_view, fragment)
                .commit();

        localFileGamesManager = new LocalFileGamesManager(
                view.getContext().getFilesDir().toString(),
                nomeMuseo,
                nomeStanza,
                opera.getNome()
        );

        triggerQuizButton(view);
        showSpotDifferenceGameCard(view);

    }

    /**
     * funzione per far visualizzare il bottone per giocare a trova le differenze solo se la data opera ha il minigioco
     *
     * @param view il layout da cui selezionare gli elementi grafici da gestire
     */
    private void showSpotDifferenceGameCard(View view) {

        if (localFileGamesManager.existsSpotTheDifference()) {

            //faccio visualizzare la card
            CardView spotDifferenceGameCard = view.findViewById(R.id.spotTheDifference_card);
            spotDifferenceGameCard.setVisibility(View.VISIBLE);

            //triggero il click per far partire il gioco
            ConstraintLayout spotDifferenceGameButton = view.findViewById(R.id.spotTheDifference_layout);
            triggerSpotTheDifferenceButton(spotDifferenceGameButton);
        }
    }

    private void triggerSpotTheDifferenceButton(ConstraintLayout spotDifferenceGameButton) {

        ArrayList<String> artData = new ArrayList<String>();
        artData.add(this.nomeMuseo);
        artData.add(this.nomeStanza);
        artData.add(this.opera.getNome());

        spotDifferenceGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), SpotDifferences.class);
                intent.putExtra("museumName", nomeMuseo);
                intent.putExtra("roomName", nomeStanza);
                intent.putExtra("artName", opera.getNome());
                startActivity(intent);

            }
        });
    }

    private void triggerQuizButton(@NonNull View view) {
        final ConstraintLayout quizButton = view.findViewById(R.id.quiz_layout);
        quizButton.setOnClickListener((view2) -> {
            if (!localFileGamesManager.existsQuiz()) {
                new AlertDialog.Builder(view2.getContext())
                        .setTitle("Quiz")
                        .setMessage(getString(R.string.quiz_import_request))
                        .setPositiveButton(getString(R.string.importa_quiz),
                                (dialog, whichButton) -> {
                                    dialog.dismiss();
                                    startImportQuiz(view2);
                                })
                        .setNeutralButton(view2.getContext().getString(R.string.NO),
                                (dialog, whichButton) -> dialog.dismiss())
                        .show();
                return;
            }
            try {
                String json = localFileGamesManager.loadQuizJson();
                ((PercorsoActivity) requireActivity()).getFgManagerOfPercorso().nextFragmentQuiz(json);
            } catch (IOException e) {
                Toast.makeText(view.getContext(), getString(R.string.errore_apertura_quiz), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        Button quizOptions = view.findViewById(R.id.quiz_option_btn);
        quizOptions.setOnClickListener(this::startImportQuiz);
    }

    private void startImportQuiz(final View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle(getString(R.string.local_import_dialog_title))
                .setMessage(getString(R.string.quiz_import_message))
                .setPositiveButton(view.getContext().getString(R.string.continua),
                        (dialog, whichButton) -> {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/json");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, requestCodeGC);
                        })
                .setNeutralButton(view.getContext().getString(R.string.NO),
                        (dialog, whichButton) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeGC) {
            if (resultCode == PercorsoActivity.RESULT_OK) {
                if (data != null) {
                    try {
                        Uri returnUri = data.getData();
                        String mimeType = requireActivity().getContentResolver().getType(returnUri);
                        OpenFile dto = new OpenFileAndroidStorageDTO(requireContext(), returnUri);
                        final String message = localFileGamesManager.saveQuizJson(mimeType, dto, this);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        return;
                    } catch (NullPointerException | IllegalStateException e) {
                        Log.e("OperaFragment.onActivityResult", "qualcosa è null, guardare lo Stack Trace");
                        e.printStackTrace();
                    }
                } else Log.e("OperaFragment.onActivityResult", "data è null");
            } else Log.e("OperaFragment.onActivityResult", "resultCode " + resultCode);
        }
        Toast.makeText(requireContext(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (opera != null) {
            outState.putString(OPERA_ID, opera.getId());
            outState.putString(OPERA_NOME, opera.getNome());
            outState.putString(OPERA_PERCORSO_IMG, opera.getPercorsoImg());
            outState.putString(OPERA_DESCRIZIONE, opera.getDescrizione());
        }
        if (nomeStanza != null) {
            outState.putString(NOME_STANZA, nomeStanza);
        }
        if (nomeMuseo != null) {
            outState.putString(NOME_MUSEO, nomeMuseo);
        }
    }

    @Override
    public void onViewStateRestored(@NonNull Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Ripristina i dati salvati da uno stato precedente se e solo se non sono nulli.
     *
     * @param savedInstanceState bundle dello stato precedente.
     */
    private boolean ripristino(final Bundle savedInstanceState) {
        if (savedInstanceState == null || savedInstanceState.isEmpty())
            return false;
        final String tmpMuseo = savedInstanceState.getString(NOME_MUSEO);
        if (tmpMuseo == null || tmpMuseo.isEmpty()) return false;
        if (savedInstanceState.getString(NOME_STANZA) == null ||
                savedInstanceState.getString(OPERA_ID) == null ||
                savedInstanceState.getString(OPERA_NOME) == null ||
                savedInstanceState.getString(OPERA_PERCORSO_IMG) == null ||
                savedInstanceState.getString(OPERA_DESCRIZIONE) == null)
            return false;

        Log.v("RIPRISTINO OperaFragment", "Ripristino effettuato correttamente.");
        nomeMuseo = tmpMuseo;
        nomeStanza = savedInstanceState.getString(NOME_STANZA);
        opera = new Opera(
                savedInstanceState.getString(OPERA_ID),
                savedInstanceState.getString(OPERA_NOME),
                savedInstanceState.getString(OPERA_PERCORSO_IMG),
                savedInstanceState.getString(OPERA_DESCRIZIONE)
        );
        return true;
    }

    /**
     * Imposta la action bar con pulsante back e titolo.
     *
     * @param title titolo da impostare per l'action bar.
     */
    private void setActionBar(final String title) {
        try {
            final ActionBar actionBar = ((PercorsoActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true); // abilita il pulsante "back" nella action bar
            actionBar.setTitle(title);
        } catch (NullPointerException e) {
            Log.e("OperaFragment", "ActionBar null");
            e.printStackTrace();
        }
    }

}