package acquiring.retail.vladimir.clientside;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.DownloadPhotoTask;
import acquiring.retail.vladimir.clientside.task.PhotoListener;
import acquiring.retail.vladimir.clientside.task.Profile;

public class AddFace2Activity extends AppCompatActivity {

    private AuthSession session;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face2);

        getSupportActionBar().hide();

        session = new AuthSession(this.getIntent().getBundleExtra("session"));
        profile = new Profile(this.getIntent().getBundleExtra("profile"));

        if (profile.getPhotoId()!=null && profile.getPhotoId().trim().length()>0) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            TextView status = (TextView) findViewById(R.id.status);
            status.setVisibility(View.VISIBLE);
            showToast("Загрузка фото профиля ...");
            download(profile.getPhotoId());
        }

        Button profile_info_button = (Button) findViewById(R.id.profile_info_button);
        Button face_select_button = (Button) findViewById(R.id.face_select_button);
        Button voice_select_button = (Button) findViewById(R.id.voice_select_button);
        Button sms_select_button = (Button) findViewById(R.id.sms_select_button);

        profile_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFace2Activity.this, InfoActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
        face_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFace2Activity.this, AddFaceActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                intent.putExtra("can_redirect_face2",false);
                startActivity(intent);
            }
        });
        voice_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFace2Activity.this, AddVoiceActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
        sms_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFace2Activity.this, SMSConfirmActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
    }

    private void download(String photoId) {
        new DownloadPhotoTask(new PhotoListener() {
            @Override
            public void onSuccess(Bitmap image) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                TextView status = (TextView) findViewById(R.id.status);
                status.setVisibility(View.INVISIBLE);
                showToast("Фото загружено");
                fillPhoto(image);
            }

            @Override
            public void onError(String result) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                TextView status = (TextView) findViewById(R.id.status);
                status.setVisibility(View.INVISIBLE);
                showToast(result);
            }
        }).execute(photoId);
    }

    private void fillPhoto(Bitmap imageBitmap) {
        // устанавливаем фото
        ImageView face_imageView = (ImageView) findViewById(R.id.face_imageView);
        face_imageView.setImageBitmap(imageBitmap);
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
