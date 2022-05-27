package it.uniba.sms2122.tourexperience.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;
import it.uniba.sms2122.tourexperience.utility.ranking.FileShare;
import it.uniba.sms2122.tourexperience.utility.ranking.MuseoDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    private TextView title;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FileShare fileShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.rankings));
        menu.removeItem(R.id.profile_pic);
        inflater.inflate(R.menu.share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.shareItem:
                Uri uri = FileProvider.getUriForFile(getContext(), "it.uniba.sms2122.tourexperience.fileprovider", fileShare.getTxt());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = (TextView) view.findViewById(R.id.title_ranking);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRankings);
        progressBar = view.findViewById(R.id.ranking_progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        int ranking = getArguments().getInt("ranking");
        if(ranking == 2)
            title.setText(R.string.title_classifica_visitati);

        ((MainActivity) getActivity()).getMuseoDatabaseList(
                (List<MuseoDatabase> museoDatabaseList) -> {
                    List<Map.Entry<String, String>> titleOrdered = new ArrayList<>(initializeTitle(museoDatabaseList, ranking).entrySet());
                    titleOrdered.sort(Comparator.comparing(Map.Entry<String, String>::getValue).reversed());
                    File txtsFolder = LocalFileManager.createLocalDirectoryIfNotExists(getActivity().getFilesDir(), "txts");
                    File txt = new File (txtsFolder, "ranking.txt");

                    if(txt.exists())
                        txt.delete();

                    try {
                        txt.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileShare = new FileShare(txt);

                    for(int i = 0; i < titleOrdered.size(); i++)
                        fileShare.writeToFile(getString(R.string.pos, String.valueOf(i+1)) + "\n" + titleOrdered.get(i).getKey() + titleOrdered.get(i).getValue() + "\n\n");

                    RankingAdapter rankingAdapter = new RankingAdapter(getContext(), museoDatabaseList, ranking, titleOrdered);
                    recyclerView.setAdapter(rankingAdapter);
                    progressBar.setVisibility(View.GONE);
                },
                (String errorMsg) -> {
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    requireActivity().onBackPressed();
                }
        );
    }

    private HashMap<String, String> initializeTitle(List<MuseoDatabase> museoDatabaseList, int ranking) {
        HashMap<String, String> title = new HashMap<>();
        for (int i = 0; i < museoDatabaseList.size(); i++){
            if(ranking == 1) {
                for (String key : museoDatabaseList.get(i).getVoti().keySet()) {
                    String media = String.valueOf(Math.round(museoDatabaseList.get(i).getVoti().get(key).calcolaMedia()* 100.0) / 100.0);
                    if(media.equals("-1.0"))
                        media = " " + getString(R.string.no_value);
                    title.put(museoDatabaseList.get(i).getNomeMuseo() + "\n" + key + "\n" + getString(R.string.value), media);
                }
            }else {
                for (String key : museoDatabaseList.get(i).getNumeroStarts().keySet()){
                    title.put(museoDatabaseList.get(i).getNomeMuseo() + "\n" + key + "\n" + getString(R.string.value), museoDatabaseList.get(i).getNumeroStarts().get(key));
                }
            }
        }

        return title;
    }
}