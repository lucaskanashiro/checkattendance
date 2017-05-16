package br.usp.ime.checkattendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class TeacherQRCodeActivity extends AppCompatActivity {
    private String id;
    private String name;
    private TextView seminarNameTextView;
    private ImageView qrCodeImageView;
    private final int WIDTH = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_qrcode);

        this.getSentData();
        this.initializeUIComponents();
        this.setupActionBar();
        this.createQRCode();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.id = intent.getStringExtra(getString(R.string.seminar_id));
        this.name = intent.getStringExtra(getString(R.string.seminar_name));
    }

    private void initializeUIComponents() {
        this.seminarNameTextView = (TextView) findViewById(R.id.tv_seminar_name_auth);
        this.seminarNameTextView.setText(this.name);
        this.qrCodeImageView = (ImageView) findViewById(R.id.iv_qr_code);
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.seminar_auth));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createQRCode() {
        Bitmap bitmap = this.encodeAsBitmap(this.id);
        this.qrCodeImageView.setImageBitmap(bitmap);
    }

    private BitMatrix getResult(String content) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, this.WIDTH, this.WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int[] getPixels(BitMatrix result, int width, int height) {
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        return pixels;
    }

    private Bitmap encodeAsBitmap(String content) {
        BitMatrix result = getResult(content);
        int width = result.getWidth();
        int heigth = result.getHeight();

        int[] pixels = getPixels(result, width, heigth);

        Bitmap bitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, this.WIDTH, 0, 0, width, heigth);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
