package it.uniba.sms2122.tourexperience.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;

public class SearchAdapter extends ArrayAdapter implements Filterable {

    private Context context;
    private ArrayList<String> museumList;
    private ArrayList<String> museumListAux;


    public SearchAdapter(@NonNull Context context, @NonNull ArrayList<String> museumList) {
        super(context, android.R.layout.simple_list_item_1, museumList);
        this.context = context;
        this.museumList = museumList;
        museumListAux = (ArrayList<String>) museumList.clone();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return museumList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return museumList.get(position);
    }

    /**
     * Esegue la ricerca filtrando la lista. La ricerca avviene per nome, città e tipologia del museo.
     * @return oggetti Filter.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = 0;
                }
                else {
                    museumList = (ArrayList<String>) museumListAux.clone();
                    String searchChr = constraint.toString().toLowerCase();
                    List<String> resultData = new ArrayList<>();
                    for (String string : museumList) {
                        if (string.toLowerCase().contains(searchChr)) {
                            resultData.add(string);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                if(results.count == 0){
                    museumList.clear();
                    museumList.add(getContext().getResources().getString(R.string.no_result_1)
                        + "\n" + getContext().getResources().getString(R.string.no_result_2));
                } else {
                    museumList = (ArrayList<String>) results.values;
                }

                notifyDataSetChanged();
            }
        };
    }
}
