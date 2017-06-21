package acquiring.retail.vladimir.clientside;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.DetectListener;
import acquiring.retail.vladimir.clientside.task.DetectTask;
import acquiring.retail.vladimir.clientside.task.DownloadPhotoTask;
import acquiring.retail.vladimir.clientside.task.PhotoListener;
import acquiring.retail.vladimir.clientside.task.Profile;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_PREFERENCE_NAME;

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
        boolean canRedirectFace2 = this.getIntent().getBooleanExtra("can_redirect_face2",false);
        getSupportActionBar().hide();

        View.OnClickListener onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path += "/"+SERVICE_PREFERENCE_NAME +"/";
                File file = new File( path );
                if (file.exists() || file.mkdirs()) {
                    //showToast("Prepare photo file: " + path + "photo.jpg");
                    file = new File(path + "photo.jpg");
                    if (file.exists()) {
                        file.delete();
                    }
                    Uri outputFileUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    intent.putExtra("return-data", true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        try {
                            startActivityForResult(intent, REQUEST_MAKE_PHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    showToast("Can't save file to path: "+path);
                }
            }
        };
        ImageButton photoButton = (ImageButton) this.findViewById(R.id.face_imageButton);
        photoButton.setOnClickListener(onClick);

        ImageView image = (ImageView) this.findViewById(R.id.face_image);
        image.setOnClickListener(onClick);

        if (canRedirectFace2 && profile!=null && profile.getPhotoId().trim().length()>0) {
            Intent intent = new Intent(AddFaceActivity.this, AddFace2Activity.class);
            intent.putExtra("session",session.getBundle());
            intent.putExtra("profile", profile.getBundle());
            startActivity(intent);
        }
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
                showToast("Ошибка: "+result);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).execute(image);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MAKE_PHOTO ) {
            if (resultCode == RESULT_OK) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path += "/"+SERVICE_PREFERENCE_NAME +"/";
                File file = new File(path + "photo.jpg");
                final Bitmap imageBitmap;
                if (file.exists() && file.length()>0) {
                    //showToast("Full photo file length: "+String.valueOf(file.length()));
                    imageBitmap = BitmapFactory.decodeFile(file.getPath());
                } else if (data!= null && data.getExtras()!=null) {
                    showToast("Thumbnail file only");
                    imageBitmap =  (Bitmap) data.getExtras().get("data");
                } else {
                    imageBitmap = null;
                }
                if (imageBitmap != null) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showToast("Обработка фото (" + String.valueOf(imageBitmap.getHeight()) + "x" + String.valueOf(imageBitmap.getWidth()) + ")...");
                    fillPhoto(imageBitmap);
                    detect(imageBitmap);
                } else {
                    showToast("Can't receive file from camera (1)");
                }
            } else if (resultCode == RESULT_FIRST_USER) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path += "/"+SERVICE_PREFERENCE_NAME +"/";
                File file = new File(path + "photo.jpg");
                if (file.length()>0) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(file.getPath());
                    if (imageBitmap != null) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        showToast("Обработка фото (" + String.valueOf(imageBitmap.getHeight()) + "x" + String.valueOf(imageBitmap.getWidth()) + ")...");
                        fillPhoto(imageBitmap);
                        detect(imageBitmap);
                    } else {
                        showToast("Can't receive file from camera (2)");
                    }
                } else {
                  showToast("Empty photo file");
                }
            } else {
                showToast("REQUEST_MAKE_PHOTO return "+String.valueOf(resultCode));
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
