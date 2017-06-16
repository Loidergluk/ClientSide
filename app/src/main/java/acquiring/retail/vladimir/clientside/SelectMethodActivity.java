package acquiring.retail.vladimir.clientside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.Profile;

public class SelectMethodActivity extends AppCompatActivity {

    private AuthSession session;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_method);
        session = new AuthSession(this.getIntent().getBundleExtra("session"));
        profile = new Profile(this.getIntent().getBundleExtra("profile"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Бесконтактная оплата");
        //getActionBar().setIcon(R.drawable.back);

        Button profile_info_button = (Button) findViewById(R.id.profile_info_button);
        Button face_select_button = (Button) findViewById(R.id.face_select_button);
        Button voice_select_button = (Button) findViewById(R.id.voice_select_button);
        Button sms_select_button = (Button) findViewById(R.id.sms_select_button);

        profile_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethodActivity.this, InfoActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
        face_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethodActivity.this, AddFaceActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
        voice_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethodActivity.this, AddVoiceActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });
        sms_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethodActivity.this, SMSConfirmActivity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });

    }

}
