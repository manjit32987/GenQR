package com.example.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize the modeRadioGroup, buyCoffeeButton, and GitHub icon ImageView
        RadioGroup modeRadioGroup = view.findViewById(R.id.modeRadioGroup);
        Button buyCoffeeButton = view.findViewById(R.id.buyCoffeeButton);
        ImageView githubIcon = view.findViewById(R.id.githubIcon);

        // Set the theme mode based on the selected radio button
        modeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == R.id.radioLight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.radioDefault) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }

            // Save the selected mode in shared preferences (optional)
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ThemeMode", checkedId);
            editor.apply();
        });

        // Set up the "Buy a Coffee" button to open the UPI link directly
        buyCoffeeButton.setOnClickListener(v -> {
            // Define the UPI payment URI using Uri.Builder
            Uri uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", "manjitchakraborty9-1@okhdfcbank")
                    .appendQueryParameter("pn", "Manjit Chakraborty")
                    .appendQueryParameter("aid", "uGICAgICjt7ruag")
                    .build();

            // Create an intent to open the UPI payment link directly
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent); // Start the intent without any additional checks
        });

        // Set up the GitHub icon click event to open the GitHub profile/repository URL
        githubIcon.setOnClickListener(v -> {
            // Replace with your GitHub profile or repository URL
            String githubUrl = "https://github.com/manjit32987";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
            startActivity(intent); // Open the GitHub link
        });

        return view;  // Return the inflated view at the end
    }
}
