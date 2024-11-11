package com.example.qrscanner;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class GenerateFragment extends Fragment {

    private EditText editTextInput;
    private Button buttonGenerate;
    private ImageView imageViewQRCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate, container, false);

        editTextInput = view.findViewById(R.id.edit_text_input);
        buttonGenerate = view.findViewById(R.id.button_generate);
        imageViewQRCode = view.findViewById(R.id.image_view_qr_code);

        buttonGenerate.setOnClickListener(v -> generateQRCode());

        // Long-click listener for sharing the QR code
        imageViewQRCode.setOnLongClickListener(v -> {
            shareQRCode();
            return true; // Indicates the long click event was handled
        });

        return view;
    }

    private void generateQRCode() {
        String inputText = editTextInput.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter text or URL", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(inputText, BarcodeFormat.QR_CODE, 400, 400);
            imageViewQRCode.setImageBitmap(bitmap);
            imageViewQRCode.setVisibility(View.VISIBLE); // Show the QR code image
        } catch (WriterException e) {
            Toast.makeText(getActivity(), "Error generating QR Code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void shareQRCode() {
        imageViewQRCode.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable) imageViewQRCode.getDrawable()).getBitmap();

        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "QRCode", null);
        Uri uri = Uri.parse(path);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this QR Code!");
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }
}
