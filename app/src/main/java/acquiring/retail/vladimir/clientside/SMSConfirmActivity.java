package acquiring.retail.vladimir.clientside;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_PREFERENCE_NAME;

public class SMSConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsconfirm);
        getSupportActionBar().hide();
        Switch s = (Switch) findViewById(R.id.switch1);
        SharedPreferences settings = getSharedPreferences(SERVICE_PREFERENCE_NAME, MODE_PRIVATE);
        s.setChecked(settings.getBoolean("pay.confirm.sms",false));
    }

    public void check(View v) {
        // сохраняем настройку
        Switch s = (Switch) findViewById(R.id.switch1);
        SharedPreferences settings = getSharedPreferences(SERVICE_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor e = settings.edit();
        e.putBoolean("pay.confirm.sms", s.isChecked());
        e.apply();
    }

}
