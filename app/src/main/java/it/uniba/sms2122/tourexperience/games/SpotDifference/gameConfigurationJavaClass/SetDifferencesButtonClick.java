package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import it.uniba.sms2122.tourexperience.R;

public class SetDifferencesButtonClick {

    private HashMap<String, ImageView> allDifferencesView;
    AppCompatActivity activityToSet;

    public SetDifferencesButtonClick(AppCompatActivity activityToSet) {

        this.activityToSet = activityToSet;
        allDifferencesView = new HashMap<String, ImageView>();

        allDifferencesViewFillWithImage1ViewDifferences();//riempio l'hashmap con le differenze della prima immagine
        allDifferencesViewFillWithImage2ViewDifferences();//riempio l'hashmap con le differenze della seconda immagine

        triggerDifferenceButton();
    }

    /**
     * funzione per riempire l'hasmamp delle differenze con con le diverse view rappresentanti delle differenze presenti nella prima immagine
     */
    private void allDifferencesViewFillWithImage1ViewDifferences() {


        ViewGroup image1Container = this.activityToSet.findViewById(R.id.image1Container);

        int differenceCounterImage1 = image1Container.getChildCount();
        for (int i = 0; i < differenceCounterImage1; i++) {

            ImageView foundCircle = (ImageView) image1Container.getChildAt(i);
            allDifferencesView.put("differenceBtn" + (i + 1), foundCircle);

        }

    }

    /**
     * funzione per riempire l'hasmamp delle differenze con con le diverse view rappresentanti delle differenze presenti nella seconda immagine
     */
    private void allDifferencesViewFillWithImage2ViewDifferences() {

        ViewGroup image2Container = this.activityToSet.findViewById(R.id.image2Container);

        int differenceCounterImage2 = image2Container.getChildCount();

        int lenght = differenceCounterImage2 + allDifferencesView.size();

        int j = 0;
        for (int i = allDifferencesView.size(); i < lenght; i++) {

            ImageView foundCircle = (ImageView) image2Container.getChildAt(j);
            allDifferencesView.put("reflex_differenceBtn" + (j + 1), foundCircle);
            j++;
        }
        Log.e("size of hasmap", String.valueOf(allDifferencesView.size()));

    }

    /**
     * funzione che si occupa di scannerizzare l'hasmap, quindi triggerare ogni vista rappresentante di differenze in essa contanuta
     */
    private void triggerDifferenceButton() {

        allDifferencesView.forEach((key, value) -> {

            ImageView clickedView = allDifferencesView.get(key);
            clickedView.setOnClickListener(new CustomClickListerners(clickedView, key));
        });
    }

    /**
     * classe che configura il comportamento del click su un differenceBtn ovvere la vista che rappresenta n
     */
    class CustomClickListerners implements View.OnClickListener {

        private String hashMapKeyOfClickedView;
        private View clickedView;

        public CustomClickListerners(View clickedView, String hashMapKeyOfClickedView) {
            this.hashMapKeyOfClickedView = hashMapKeyOfClickedView;
            this.clickedView = clickedView;
        }


        @Override
        public void onClick(View view) {

            clickedView.setBackground(activityToSet.getDrawable(R.drawable.find_difference_circle_button));//triggero l'image view cliccata

            //setto il background anche per l'imageview della foto opposta
            if (hashMapKeyOfClickedView.contains("reflex_")) {

                String newString = this.hashMapKeyOfClickedView.replace("reflex_", "");
                allDifferencesView.get(newString).setBackground(activityToSet.getDrawable(R.drawable.find_difference_circle_button));

            } else {

                String newString = "reflex_" + this.hashMapKeyOfClickedView;
                allDifferencesView.get(newString).setBackground(activityToSet.getDrawable(R.drawable.find_difference_circle_button));
            }


        }
    }
}
