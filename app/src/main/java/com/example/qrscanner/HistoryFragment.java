package com.example.qrscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class HistoryFragment extends Fragment {

    private ListView listViewHistory;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> scannedHistory;
    private static final String PREFS_NAME = "qr_history_prefs";
    private static final String HISTORY_KEY = "scanned_history";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize ListView and scanned history list
        listViewHistory = view.findViewById(R.id.list_view_history);
        scannedHistory = new ArrayList<>();

        // Load history from SharedPreferences
        loadHistory();

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, scannedHistory);
        listViewHistory.setAdapter(adapter);

        return view;
    }

    // Method to add a scanned result to the history
    public void addScanResult(String result) {
        if (result != null && !result.isEmpty()) {
            scannedHistory.add(result);
            adapter.notifyDataSetChanged(); // Notify adapter of data change
            saveHistory(); // Save history after adding a new result
        }
    }

    private void loadHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String historyString = prefs.getString(HISTORY_KEY, null);
        if (historyString != null) {
            String[] historyArray = historyString.split(",");
            scannedHistory.addAll(Arrays.asList(historyArray));
        }
    }

    private void saveHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // Convert ArrayList to a comma-separated string
        String historyString = String.join(",", scannedHistory);
        editor.putString(HISTORY_KEY, historyString);
        editor.apply(); // Apply the changes
        Toast.makeText(getContext(), "History saved!", Toast.LENGTH_SHORT).show();
    }
}
