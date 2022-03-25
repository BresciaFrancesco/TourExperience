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
    private WelcomeActivity welcomeActivity;
    private FloatingActionButton fab;
    private TextView skip, later;
    private Button loginBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        welcomeActivity = (WelcomeActivity) getActivity();
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
    }

    private void goToLogin(View view) {
        welcomeActivity.goToLogin();
    }

    private void fabClickEvent(View view) {
        welcomeActivity.nextPage();
    }

    private void skipOnClickEvent(View view) {
        welcomeActivity.lastPage();
    }

    public void setLastPage() {
        fab.setVisibility(View.INVISIBLE);
        skip.setVisibility(View.GONE);
        later.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) { }
}