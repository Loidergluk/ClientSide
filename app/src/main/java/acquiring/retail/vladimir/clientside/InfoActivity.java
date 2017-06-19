package acquiring.retail.vladimir.clientside;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.Profile;
import acquiring.retail.vladimir.clientside.task.ProfileListener;
import acquiring.retail.vladimir.clientside.task.UpdateProfileTask;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_PREFERENCE_NAME;

public class InfoActivity extends AppCompatActivity {

    private AuthSession session;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        session = new AuthSession(this.getIntent().getBundleExtra("session"));
        profile = new Profile(this.getIntent().getBundleExtra("profile"));
        getSupportActionBar().hide();

        EditText phone = (EditText) findViewById(R.id.phoneNumber);
        EditText firstName = (EditText) findViewById(R.id.firstName);
        EditText lastName = (EditText) findViewById(R.id.lastName);
        EditText middleName = (EditText) findViewById(R.id.middleName);
        SharedPreferences settings = getSharedPreferences(SERVICE_PREFERENCE_NAME, MODE_PRIVATE);
        phone.setText(settings.getString("profile.info.phone",""));
        firstName.setText(settings.getString("profile.info.firstName",""));
        lastName.setText(settings.getString("profile.info.lastName",""));
        middleName.setText(settings.getString("profile.info.middleName",""));
    }

    public void next(View view) {
        final EditText phone = (EditText) findViewById(R.id.phoneNumber);
        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final EditText lastName = (EditText) findViewById(R.id.lastName);
        final EditText middleName = (EditText) findViewById(R.id.middleName);

        if (phone.getText().length()<=8) {
            showToast("Заполните номер телефона");
            return;
        }
        if (phone.getText().length()<=0) {
            showToast("Заполните имя");
            return;
        }
        if (phone.getText().length()<=0) {
            showToast("Заполните фамилию");
            return;
        }

        profile.setFirstName(firstName.getText().toString());
        profile.setLastName(lastName.getText().toString());
        profile.setMiddleName(middleName.getText().toString());
        profile.setPhoneNumber(phone.getText().toString());
        new UpdateProfileTask(session, profile, new ProfileListener() {
            @Override
            public void onSuccess(Profile profile) {
                SharedPreferences settings = getSharedPreferences(SERVICE_PREFERENCE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor e = settings.edit();
                e.putString("profile.info.phone", phone.getText().toString());
                e.putString("profile.info.firstName", firstName.getText().toString());
                e.putString("profile.info.lastName", lastName.getText().toString());
                e.putString("profile.info.middleName", middleName.getText().toString());
                e.apply();
                finish();
            }

            @Override
            public void onError(String result) {
                showToast(result);
            }
        }).execute();
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
