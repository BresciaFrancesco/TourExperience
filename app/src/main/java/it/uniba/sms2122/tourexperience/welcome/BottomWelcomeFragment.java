package it.uniba.sms2122.tourexperience.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import it.uniba.sms2122.tourexperience.R;

/**
 * @author Catignano Francesco
 * Fragment per comandare le schermate di benvenuto
 */
public class BottomWelcomeFragment extends Fragment implements View.OnClickListener {
    private OnChangePageListener listener;
    private FloatingActionButton fab;
    private TextView skip, later;
    private Button loginBtn;

    /**
     * Interfaccia di comunicazione tra il fragment e l'activity ospitante
     */
    public interface OnChangePageListener {
        void nextPage();
        void lastPage();
        void goToLogin();
        void goToHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnChangePageListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        skip = (TextView) view.findViewById(R.id.skip_text_view);
        loginBtn = (Button) view.findViewById(R.id.goto_login_btn);
        later = (TextView) view.findViewById(R.id.later_text_view);

        fab.setOnClickListener(this::fabClickEvent);
        skip.setOnClickListener(this::skipOnClickEvent);
        loginBtn.setOnClickListener(this::goToLogin);
        later.setOnClickListener(this::goToHome);
    }

    /**
     * Imposta lo stato del fragment in base alla pagina mostrata.
     * Se è l'ultima pagina, mostra i pulsanti per passare alla pagina di login
     * @param actualPage Il numero di pagina attuale
     * @param lastPage Il numero dell'ultima pagina
     */
    public void setStateByPage(int actualPage, int lastPage) {
        if(actualPage == lastPage) {
            fab.setVisibility(View.INVISIBLE);
            skip.setVisibility(View.GONE);
            later.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
        else if(!fab.isShown()) {
            later.setVisibility(View.GONE);
            loginBtn.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Listener del floating action button
     * @param view
     */
    private void fabClickEvent(View view) {
        listener.nextPage();
    }

    /**
     * Listener del pulsante di skip
     * @param view
     */
    private void skipOnClickEvent(View view) {
        listener.lastPage();
    }

    /**
     * Listener del pulsante di login per passare direttamente alla pagina di login
     * @param view
     */
    private void goToLogin(View view) {
        listener.goToLogin();
    }

    private void goToHome(View view) {
        listener.goToHome();
    }

    @Override
    public void onClick(View view) { }
}