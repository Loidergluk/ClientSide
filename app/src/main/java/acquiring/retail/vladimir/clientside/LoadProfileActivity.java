package acquiring.retail.vladimir.clientside;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.CreateProfileTask;
import acquiring.retail.vladimir.clientside.task.LoadProfileTask;
import acquiring.retail.vladimir.clientside.task.Profile;
import acquiring.retail.vladimir.clientside.task.ProfileListener;

import static acquiring.retail.vladimir.clientside.task.LoadProfileTask.PROFILE_NOT_FOUND_ERROR;
import static acquiring.retail.vladimir.clientside.task.Service.PHONE_NUMBER;

public class LoadProfileActivity extends AppCompatActivity {


    public LoadProfileActivity() {
        super();
    }

    private void loadProfile(final AuthSession session, final String firstName, final String lastName, final String middleName, final String phone) {
        // сессия открыта, теперь загрузим(создадим) профиль
        new LoadProfileTask(session,new ProfileListener() {
            @Override
            public void onSuccess(Profile profile) {
                showToast("Load profile");
                SharedPreferences settings = getSharedPreferences("eosan.biometricpay", MODE_PRIVATE);
                SharedPreferences.Editor e = settings.edit();
                e.putString("profile.info.phone", profile.getPhoneNumber());
                e.putString("profile.info.firstName", profile.getFirstName());
                e.putString("profile.info.lastName", profile.getLastName());
                e.putString("profile.info.middleName", profile.getMiddleName());
                e.apply();
                nextActivity(session,profile);
            }

            @Override
            public void onError(String error) {
                if (PROFILE_NOT_FOUND_ERROR.equals(error))
                    createProfile(session,firstName,lastName,middleName,phone);
                else {
                    showToast(error);
                    prevActivity();
                }
            }
        }).execute(phone);
    }

    private void  createProfile(final AuthSession session, String firstName, String lastName, String middleName, String phone) {
        // сессия открыта, теперь загрузим(создадим) профиль
        new CreateProfileTask(session,firstName,lastName,middleName,phone,new ProfileListener() {
            @Override
            public void onSuccess(Profile profile) {
                showToast("Create new profile");
                SharedPreferences settings = getSharedPreferences("eosan.biometricpay", MODE_PRIVATE);
                SharedPreferences.Editor e = settings.edit();
                e.putString("profile.info.phone", profile.getPhoneNumber());
                e.putString("profile.info.firstName", profile.getFirstName());
                e.putString("profile.info.lastName", profile.getLastName());
                e.putString("profile.info.middleName", profile.getMiddleName());
                e.apply();
                nextActivity(session,profile);
            }

            @Override
            public void onError(String result) {
                showToast(result);
                prevActivity();
            }
        }).execute(phone);
    }

    private String getPhoneNumber() {
        try {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phone = tMgr.getLine1Number();
            if (phone!=null && phone.trim().length()>0)
                return phone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PHONE_NUMBER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile);

        getSupportActionBar().hide();

        final AuthSession session = new AuthSession(this.getIntent().getBundleExtra("session"));
        if (session.getSessionId() == null || session.getSessionId().length() == 0)
            prevActivity();
        SharedPreferences settings = getSharedPreferences("eosan.biometricpay", MODE_PRIVATE);
        String phone = settings.getString("profile.info.phone","");
        final String firstName = settings.getString("profile.info.firstName","");
        final String lastName = settings.getString("profile.info.lastName","");
        final String middleName = settings.getString("profile.info.middleName","");
        if (phone.trim().length()==0)
            phone = getPhoneNumber();
        if (phone.trim().length()==0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText phoneEdit = new EditText(getApplicationContext());
            phoneEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            alert.setMessage("Личные данные");
            alert.setTitle("Введите номер телефона");
            alert.setView(phoneEdit);
            alert.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String phone = phoneEdit.getText().toString();
                    if (phone.trim().length()>0)
                        loadProfile(session, firstName, lastName, middleName, phone);
                    else
                        prevActivity();
                }
            });

            alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    prevActivity();
                }
            });
        } else {
            loadProfile(session, firstName, lastName, middleName, phone);
        }
    }

    private void prevActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void nextActivity(AuthSession session,Profile profile) {
        Intent intent = new Intent(getApplicationContext(), SelectMethodActivity.class);
        intent.putExtra("session", session.getBundle());
        intent.putExtra("profile", profile.getBundle());
        startActivity(intent);
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
