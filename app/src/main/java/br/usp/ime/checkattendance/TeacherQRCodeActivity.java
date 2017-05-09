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
        this.id = intent.getStringExtra("id");
        this.name = intent.getStringExtra("name");
    }

    private void initializeUIComponents() {
        this.seminarNameTextView = (TextView) findViewById(R.id.tv_seminar_name_auth);
        this.seminarNameTextView.setText(this.name);
        this.qrCodeImageView = (ImageView) findViewById(R.id.iv_qr_code);
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Seminar Authentication");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createQRCode() {
        Bitmap bitmap = this.encodeAsBitmap(this.id);
        this.qrCodeImageView.setImageBitmap(bitmap);
    }

    private Bitmap encodeAsBitmap(String content) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, this.WIDTH, this.WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, this.WIDTH, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
