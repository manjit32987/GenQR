package com.example.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class ScanFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private TextView textScanResult;
    private Button buttonOpenLink, buttonCopy, buttonFlash;
    private boolean isFlashOn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        barcodeView = view.findViewById(R.id.barcode_scanner);
        textScanResult = view.findViewById(R.id.text_scan_result);
        buttonOpenLink = view.findViewById(R.id.button_open_link);
        buttonCopy = view.findViewById(R.id.button_copy);
        buttonFlash = view.findViewById(R.id.button_flash);

        // Start continuous scanning
        barcodeView.decodeContinuous(callback);

        // Flashlight toggle
        buttonFlash.setOnClickListener(v -> {
            isFlashOn = !isFlashOn;
            if (isFlashOn) {
                barcodeView.setTorchOn();
                buttonFlash.setText("Flash Off");
            } else {
                barcodeView.setTorchOff();
                buttonFlash.setText("Flash On");
            }
        });

        // Open link action
        buttonOpenLink.setOnClickListener(v -> {
            String url = textScanResult.getText().toString();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        // Copy action
        buttonCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Scanned QR Code", textScanResult.getText());
            clipboard.setPrimaryClip(clip);
        });

        return view;
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.pause(); // Pause the scanner after reading the code
                textScanResult.setText(result.getText());

                // Make the buttons visible
                buttonOpenLink.setVisibility(View.VISIBLE);
                buttonCopy.setVisibility(View.VISIBLE);
            }
        }

        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
