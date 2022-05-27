package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.configurationObject.DifferencesCoordinates;

public class SetDifferencesButtonCordinates {

    DifferencesCoordinates diffCordinates;

    ConstraintLayout image1Container;
    ConstraintLayout image2Container;

    public SetDifferencesButtonCordinates(ConstraintLayout image1Container, ConstraintLayout image2Container, DifferencesCoordinates diffCordinates) {

        this.diffCordinates = diffCordinates;
        this.image1Container = image1Container;
        this.image2Container = image2Container;

        setCordinatesOfDifferencesButton();
    }


    /**
     * funzione per settare le cordinate di tutte i bottoni delle differenze presenti su entrambi le immagini di gioco
     */
    private void setCordinatesOfDifferencesButton() {

        float diff1xCordinate = diffCordinates.getXofDifference1();
        float diff1yCordinate = diffCordinates.getYofDifference1();
        setCordinatesOfDifferenceButton1onImage1(diff1xCordinate, diff1yCordinate);
        setCordinatesOfDifferenceButton1onImage2(diff1xCordinate, diff1yCordinate);


        float diff2xCordinate = diffCordinates.getXofDifference2();
        float diff2yCordinate = diffCordinates.getYofDifference2();
        setCordinatesOfDifferenceButton2onImage1(diff2xCordinate, diff2yCordinate);
        setCordinatesOfDifferenceButton2onImage2(diff2xCordinate, diff2yCordinate);


        float diff3xCordinate = diffCordinates.getXofDifference3();
        float diff3yCordinate = diffCordinates.getYofDifference3();
        setCordinatesOfDifferenceButton3onImage1(diff3xCordinate, diff3yCordinate);
        setCordinatesOfDifferenceButton3onImage2(diff3xCordinate, diff3yCordinate);

    }

    /**
     * funzioner per settare le cordinate della prima differenza sulla prima immagine
     *
     * @param diff1xCordinate
     * @param diff1yCordinate
     */
    private void setCordinatesOfDifferenceButton1onImage1(float diff1xCordinate, float diff1yCordinate) {
        ConstraintSet csDiff1 = new ConstraintSet();
        csDiff1.clone(image1Container);
        csDiff1.setHorizontalBias(R.id.differenceBtn1, diff1xCordinate);
        csDiff1.setVerticalBias(R.id.differenceBtn1, diff1yCordinate);
        csDiff1.applyTo(image1Container);
    }


    /**
     * funzioner per settare le cordinate della seconda differenza sulla prima immagine
     *
     * @param diff2xCordinate
     * @param diff2yCordinate
     */
    private void setCordinatesOfDifferenceButton2onImage1(float diff2xCordinate, float diff2yCordinate) {
        ConstraintSet csDiff2 = new ConstraintSet();
        csDiff2.clone(image1Container);
        csDiff2.setHorizontalBias(R.id.differenceBtn2, diff2xCordinate);
        csDiff2.setVerticalBias(R.id.differenceBtn2, diff2yCordinate);
        csDiff2.applyTo(image1Container);
    }


    /**
     * funzioner per settare le cordinate della terza differenza sulla prima immagine
     *
     * @param diff3xCordinate
     * @param diff3yCordinate
     */
    private void setCordinatesOfDifferenceButton3onImage1(float diff3xCordinate, float diff3yCordinate) {
        ConstraintSet csDiff3 = new ConstraintSet();
        csDiff3.clone(image1Container);
        csDiff3.setHorizontalBias(R.id.differenceBtn3, diff3xCordinate);
        csDiff3.setVerticalBias(R.id.differenceBtn3, diff3yCordinate);
        csDiff3.applyTo(image1Container);
    }

    /**
     * funzioner per settare le cordinate della prima differenza sulla seconda immagine
     *
     * @param diff1xCordinate
     * @param diff1yCordinate
     */
    private void setCordinatesOfDifferenceButton1onImage2(float diff1xCordinate, float diff1yCordinate) {
        ConstraintSet csDiff1 = new ConstraintSet();
        csDiff1.clone(image2Container);
        csDiff1.setHorizontalBias(R.id.reflex_differenceBtn1, diff1xCordinate);
        csDiff1.setVerticalBias(R.id.reflex_differenceBtn1, diff1yCordinate);
        csDiff1.applyTo(image2Container);
    }


    /**
     * funzioner per settare le cordinate della seconda differenza sulla seconda immagine
     *
     * @param diff2xCordinate
     * @param diff2yCordinate
     */
    private void setCordinatesOfDifferenceButton2onImage2(float diff2xCordinate, float diff2yCordinate) {
        ConstraintSet csDiff2 = new ConstraintSet();
        csDiff2.clone(image2Container);
        csDiff2.setHorizontalBias(R.id.reflex_differenceBtn2, diff2xCordinate);
        csDiff2.setVerticalBias(R.id.reflex_differenceBtn2, diff2yCordinate);
        csDiff2.applyTo(image2Container);
    }


    /**
     * funzioner per settare le cordinate della terza differenza sulla seconda immagine
     *
     * @param diff3xCordinate
     * @param diff3yCordinate
     */
    private void setCordinatesOfDifferenceButton3onImage2(float diff3xCordinate, float diff3yCordinate) {
        ConstraintSet csDiff3 = new ConstraintSet();
        csDiff3.clone(image2Container);
        csDiff3.setHorizontalBias(R.id.reflex_differenceBtn3, diff3xCordinate);
        csDiff3.setVerticalBias(R.id.reflex_differenceBtn3, diff3yCordinate);
        csDiff3.applyTo(image2Container);
    }
}
