package it.uniba.sms2122.tourexperience.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import it.uniba.sms2122.tourexperience.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    private TextView title;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (TextView) view.findViewById(R.id.title_ranking);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRankings);

        if(getArguments().getInt("ranking") == 2)
            title.setText(R.string.title_classifica_visitati);

        MaterialDividerItemDecoration materialDividerItemDecoration = new MaterialDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
    }
}