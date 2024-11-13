package com.example.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.List;

public class ScanFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private TextView textScanResult;
    private Button buttonOpenLink, buttonCopy, buttonFlash;
    private SeekBar zoomSlider;
    private ImageView galleryIcon;
    private boolean isFlashOn = false;

    // Launcher to pick image from gallery
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                        scanBarcodeFromBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        barcodeView = view.findViewById(R.id.barcode_scanner);
        textScanResult = view.findViewById(R.id.text_scan_result);
        buttonOpenLink = view.findViewById(R.id.button_open_link);
        buttonCopy = view.findViewById(R.id.button_copy);
        buttonFlash = view.findViewById(R.id.button_flash);
        galleryIcon = view.findViewById(R.id.gallery_icon);

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
            if (!url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } else {
                Toast.makeText(getContext(), "No URL to open", Toast.LENGTH_SHORT).show();
            }
        });

        // Copy action
        buttonCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Scanned QR Code", textScanResult.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Zoom control using SeekBar

        // Gallery icon click listener to pick an image
        galleryIcon.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        return view;
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
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

    private void scanBarcodeFromBitmap(Bitmap bitmap) {
        try {
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(binaryBitmap);

            // Display result
            textScanResult.setText(result.getText());
            buttonOpenLink.setVisibility(View.VISIBLE);
            buttonCopy.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(getContext(), "No QR code found in the image", Toast.LENGTH_SHORT).show();
        }
    }
}
