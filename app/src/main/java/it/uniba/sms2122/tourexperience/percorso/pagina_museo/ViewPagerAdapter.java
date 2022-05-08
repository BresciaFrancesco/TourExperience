package it.uniba.sms2122.tourexperience.percorso.pagina_museo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import it.uniba.sms2122.tourexperience.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> images;

    public ViewPagerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.museum_image_view,null);

        ImageView imageView = (ImageView)view.findViewById(R.id.museumImageView);
        imageView.setImageURI(Uri.parse(images.get(position)));

        ImageView ind1 = view.findViewById(R.id.ind1);
        ImageView ind2 = view.findViewById(R.id.ind2);
        ImageView ind3 = view.findViewById(R.id.ind3);

        ImageView back = view.findViewById(R.id.back);
        ImageView next = view.findViewById(R.id.next);

        // Con i listener posso premere sulla freccia e cambiare immagine
        // Altrimenti posso solo scorrere per cambiare immagine
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MuseoFragment.viewPager.setCurrentItem(position-1);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MuseoFragment.viewPager.setCurrentItem(position+1);
            }
        });

        switch (position) {
            case 0:
                ind1.setImageResource(R.drawable.viewpager_selected_dot);
                ind2.setImageResource(R.drawable.viewpager_unselected_dot);
                ind3.setImageResource(R.drawable.viewpager_unselected_dot);
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                break;
            case 1:
                ind1.setImageResource(R.drawable.viewpager_unselected_dot);
                ind2.setImageResource(R.drawable.viewpager_selected_dot);
                ind3.setImageResource(R.drawable.viewpager_unselected_dot);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 2:
                ind1.setImageResource(R.drawable.viewpager_unselected_dot);
                ind2.setImageResource(R.drawable.viewpager_unselected_dot);
                ind3.setImageResource(R.drawable.viewpager_selected_dot);
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                break;
        }

        ViewPager viewPager = (ViewPager)container;
        viewPager.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager)container;
        View view = (View)object;
        viewPager.removeView(view);
    }
}
