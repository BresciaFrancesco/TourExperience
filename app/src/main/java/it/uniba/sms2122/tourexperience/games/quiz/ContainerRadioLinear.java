package it.uniba.sms2122.tourexperience.games.quiz;

import android.view.View;
import android.widget.RadioGroup;

public class ContainerRadioLinear {
    private final View view;
    private final boolean isRadioGroup;

    public ContainerRadioLinear(final View view) {
        this.view = view;
        this.isRadioGroup = view instanceof RadioGroup;
    }

    public View getView() {
        return view;
    }

    public boolean isRadioGroup() {
        return isRadioGroup;
    }
}
