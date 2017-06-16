package acquiring.retail.vladimir.clientside;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.DetectListener;
import acquiring.retail.vladimir.clientside.task.DetectTask;
import acquiring.retail.vladimir.clientside.task.DownloadPhotoTask;
import acquiring.retail.vladimir.clientside.task.PhotoListener;
import acquiring.retail.vladimir.clientside.task.Profile;

public class AddFaceActivity extends AppCompatActivity {

    private static final int REQUEST_MAKE_PHOTO = 1;

    private AuthSession session;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face);
        session = new AuthSession(this.getIntent().getBundleExtra("session"));
        profile = new Profile(this.getIntent().getBundleExtra("profile"));

        getSupportActionBar().hide();

        View.OnClickListener onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivityForResult(intent, REQUEST_MAKE_PHOTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ImageButton photoButton = (ImageButton) this.findViewById(R.id.face_imageButton);
        photoButton.setOnClickListener(onClick);

        ImageView image = (ImageView) this.findViewById(R.id.face_image);
        image.setOnClickListener(onClick);

        if (profile!=null && profile.getPhotoId().trim().length()>0) {
            Intent intent = new Intent(AddFaceActivity.this, AddFace2Activity.class);
            intent.putExtra("session",session.getBundle());
            intent.putExtra("profile", profile.getBundle());
            startActivity(intent);
        }
    }

    private void download(String photoId) {
        new DownloadPhotoTask(new PhotoListener() {
            @Override
            public void onSuccess(Bitmap image) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showToast("Фото загружено");
                fillPhoto(image);
            }

            @Override
            public void onError(String result) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showToast(result);
            }
        }).execute(photoId);
    }

    private void detect(Bitmap image) {
        new DetectTask(session, profile, new DetectListener() {
            @Override
            public void onSuccess(Profile profile) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showToast("Фото успешно обработано");
                Intent intent = new Intent(AddFaceActivity.this, AddFace2Activity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }

            @Override
            public void onError(String result) {
                showToast(result);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).execute(image);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MAKE_PHOTO && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showToast("Обработка фото ...");
                fillPhoto(imageBitmap);
                detect(imageBitmap);
            }
        }
    }

    private void fillPhoto(Bitmap imageBitmap) {
        // устанавливаем фото
        ImageButton imageBtn = (ImageButton) findViewById(R.id.face_imageButton);
        imageBtn.setVisibility(View.INVISIBLE);
        ImageView image = (ImageView) findViewById(R.id.face_image);
        image.setVisibility(View.VISIBLE);
        image.setImageBitmap(imageBitmap);
    }

    public void showToast(final String text) {
        if (text != null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
