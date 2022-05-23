package it.uniba.sms2122.tourexperience.percorso.pagina_opera;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.quiz.dto.QuizJson;
import it.uniba.sms2122.tourexperience.games.quiz.Domanda;
import it.uniba.sms2122.tourexperience.games.quiz.Quiz;
import it.uniba.sms2122.tourexperience.games.quiz.Risposta;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Punteggio;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    // the fragment initialization parameter
    private static final String QUIZ_JSON = "quizJson";
    private static final String QUIZ_COMPLETO = "QuizJsonCompleto";
    private static final String BTN_CONFERMA_CLICCATO = "confermaBtnClicked";
    private final Gson gson = new Gson();
    private Quiz quiz;
    private String quizJson;
    private TextView title;
    private TextView points;
    private Button confermaBtn;
    private ScrollView scrollView;
    private ConstraintLayout constraintLayoutForFinalBtns;
    private List<RadioGroup> risposteRadioGroups;
    private List<LinearLayout> cardLinearLayoutList;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param quizJson json string per un quiz completo.
     * @return una nuova istanza di un fragment QuizFragment.
     */
    public static QuizFragment newInstance(final String quizJson) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(QUIZ_JSON, quizJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizJson = getArguments().getString(QUIZ_JSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            quiz = gson.fromJson(savedInstanceState.getString(QUIZ_COMPLETO), Quiz.class);
        }

        final Context context = view.getContext();
        scrollView = view.findViewById(R.id.quiz_scroll_view);
        final LinearLayout linearLayout = view.findViewById(R.id.linear_layout_quiz);
        title = view.findViewById(R.id.quiz_title);
        points = view.findViewById(R.id.total_point);
        final LayoutInflater inflater = getLayoutInflater();
        final ViewGroup viewGroup = (ViewGroup) view.getParent();
        confermaBtn = view.findViewById(R.id.conferma_btn);
        constraintLayoutForFinalBtns = view.findViewById(R.id.final_buttons_layout);

        final Button ripetiQuizBtn = view.findViewById(R.id.ripeti_quiz_btn);
        ripetiQuizBtn.setOnClickListener(this::ripetiQuiz);
        final Button terminaQuizBtn = view.findViewById(R.id.termina_quiz_btn);
        terminaQuizBtn.setOnClickListener(this::terminaQuiz);

        try {
            setActionBar("Quiz");
            quiz = (quiz == null) ? Quiz.buildFromJson(gson.fromJson(quizJson, QuizJson.class)) : quiz;
            cardLinearLayoutList = new ArrayList<>(quiz.getDomande().size());
            risposteRadioGroups = new ArrayList<>(quiz.getDomande().size());
            confermaBtn.setVisibility(View.VISIBLE);
            confermaBtn.setOnClickListener(this::confermaQuizSicuro);
            int i = 0;

            title.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            title.setText(quiz.getTitolo().value());
            points.setText(context.getString(R.string.quiz_total, quiz.getValoreTotale().value()));
            for (final Domanda domanda : quiz.getDomande()) {

                View cardView = inflater.inflate(R.layout.quiz_card, viewGroup, false);

                final TextView domandaTxt = cardView.findViewById(R.id.domanda_txtview);
                domandaTxt.setId(domanda.getId().value());
                domandaTxt.setText(context.getString(R.string.quiz_question, (i+1), domanda.getDomanda().value()));

                final TextView puntiTxt = cardView.findViewById(R.id.punti_txtview);
                puntiTxt.setText(context.getString(R.string.quiz_total, domanda.getValore().value()));

                final RadioGroup radioGroup = cardView.findViewById(R.id.radio_group_risposte);
                for (final Risposta risposta : domanda.getRisposte()) {
                    radioGroup.addView(createRadioButton(cardView.getContext(), risposta));
                }
                risposteRadioGroups.add(i, radioGroup);
                cardLinearLayoutList.add(i, cardView.findViewById(R.id.quiz_card_linear_layout));
                linearLayout.addView(cardView);
                i++;
            }
        }
        catch (JsonParseException | NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void confermaQuizSicuro(View view) {
        if (!isQuizCompletato()) {
            Toast.makeText(getContext(), view.getContext().getString(R.string.quiz_non_completato), Toast.LENGTH_SHORT).show();
            return;
        }
        quiz.increasePunteggio(confermaQuiz(view));
    }

    private Punteggio confermaQuiz(View view) {
        Punteggio punteggio = new Punteggio(0);
        final List<Domanda> domande = quiz.getDomande();
        for (int i = 0; i < domande.size(); i++) {
            final int radioCheckedId = risposteRadioGroups.get(i).getCheckedRadioButtonId();
            for (final Risposta risposta : domande.get(i).getRisposte()) {
                if (radioCheckedId == risposta.getId().value()) {
                    if (risposta.isTrue().value()) {
                        punteggio = punteggio.add(domande.get(i).getValore());
                        cardLinearLayoutList.get(i).setBackgroundColor(view.getContext().getColor(R.color.success_green_card));
                    } else {
                        cardLinearLayoutList.get(i).setBackgroundColor(view.getContext().getColor(R.color.error_red_card));
                    }
                    break;
                }
            }
        }
        points.setText(view.getContext().getString(R.string.quiz_score, punteggio.value(), quiz.getValoreTotale().value()));
        constraintLayoutForFinalBtns.setVisibility(View.VISIBLE);
        goToTop();
        confermaBtn.setVisibility(View.GONE);
        return punteggio;
    }

    private boolean isQuizCompletato() {
        for (final RadioGroup rg : risposteRadioGroups) {
            if (rg.getCheckedRadioButtonId() == -1)
                return false;
        }
        return true;
    }

    private void ripetiQuiz(View view) {
        for (int i = 0; i < risposteRadioGroups.size(); i++) {
            risposteRadioGroups.get(i).clearCheck();
            cardLinearLayoutList.get(i).setBackgroundColor(view.getContext().getColor(R.color.color_card_quiz));
        }
        constraintLayoutForFinalBtns.setVisibility(View.GONE);
        confermaBtn.setVisibility(View.VISIBLE);
        points.setText(view.getContext().getString(R.string.quiz_total, quiz.getValoreTotale().value()));
        quiz.resetPunteggio();
    }

    private void terminaQuiz(View view) {
        try {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), view.getContext().getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
        }
    }

    private RadioButton createRadioButton(final Context context, final Risposta risposta) {
        final RadioButton button = new RadioButton(context);
        button.setId(risposta.getId().value());
        button.setText(risposta.getRisposta().value());
        button.setTextSize(18);
        RadioGroup.LayoutParams radioButtonParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );
        radioButtonParams.setMargins(0,15,0,20);
        button.setLayoutParams(radioButtonParams);
        return button;
    }

    private void goToTop() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (quiz != null) {
            outState.putString(QUIZ_COMPLETO, gson.toJson(quiz));
        }
        if (confermaBtn != null) {
            outState.putBoolean(BTN_CONFERMA_CLICCATO, confermaBtn.getVisibility() == View.GONE);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            if (savedInstanceState.getBoolean(BTN_CONFERMA_CLICCATO)) {
                confermaQuiz(getView());
            }
        }
    }

    /**
     * Imposta la action bar con pulsante back e titolo.
     * @param title titolo da impostare per l'action bar.
     */
    private void setActionBar(final String title) {
        try {
            final ActionBar actionBar = ((PercorsoActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true); // abilita il pulsante "back" nella action bar
            actionBar.setTitle(title);
        } catch (NullPointerException e) {
            Log.e("QuizFragment", "ActionBar null");
            e.printStackTrace();
        }
    }
}