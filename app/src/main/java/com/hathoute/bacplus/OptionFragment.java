package com.hathoute.bacplus;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OptionFragment extends Fragment {

    private Context mContext;

    OnCallbackReceived mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mCallback = (OnCallbackReceived) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int subject = getArguments().getInt("year");
        ListView lvOptions = view.findViewById(R.id.lvOptions);
        String[] Options = getResources()
                .getStringArray(subject == MainActivity.YEAR_FIRST ?
                        R.array.options_firstyear_array : R.array.options_secondyear_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext
                , R.layout.simple_list_item_arabic, android.R.id.text1, Options);
        lvOptions.setAdapter(adapter);
        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onOptionChoice(position);
            }
        });
    }

    public interface OnCallbackReceived {
        public void onOptionChoice(int position);
    }
}
