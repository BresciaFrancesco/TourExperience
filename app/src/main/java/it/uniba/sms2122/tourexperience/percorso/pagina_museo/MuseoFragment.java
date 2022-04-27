package it.uniba.sms2122.tourexperience.percorso.pagina_museo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.musei.MuseiAdapter;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MuseoFragment} factory method to
 * create an instance of this fragment.
 */
public class MuseoFragment extends Fragment {

    ViewPager viewPager;
    TextView textView;
    RecyclerView recycleView;

    // Info items
    String[] names = {"percorso1","percorso2"};
    int[] images = {R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_museo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager)view.findViewById(R.id.museum_viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext());
        viewPager.setAdapter(viewPagerAdapter);

        textView = view.findViewById(R.id.museum_description);
        textView.setText("Descrizione museo...");

        recycleView = view.findViewById(R.id.routes_recycle_view);

        // Settare i dati per i percorsi qui, prima di creare l'adapter

        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(),names,images);
        recycleView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new RecycleViewAdapter.onItemClickListener() {
            @Override
            public void onClick(String str) {
                System.out.println(str);
                Bundle bundle = new Bundle();
                bundle.putString("position", str);
                ((PercorsoActivity) getActivity()).nextPercorsoFragment(bundle);
            }
        });
    }
}