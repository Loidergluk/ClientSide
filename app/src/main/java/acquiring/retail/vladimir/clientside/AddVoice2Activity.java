package acquiring.retail.vladimir.clientside;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.Profile;

public class AddVoice2Activity extends AppCompatActivity {

    private AuthSession session;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voice2);
        session = new AuthSession(this.getIntent().getBundleExtra("session"));
        profile = new Profile(this.getIntent().getBundleExtra("profile"));

        getSupportActionBar().hide();

        final Button save_voice_button = (Button) findViewById(R.id.save_voice_button);
        ImageButton record_ImageButton = (ImageButton) findViewById(R.id.record__voice_imageButton);

        save_voice_button.setVisibility(View.GONE);

        record_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_voice_button.setVisibility(View.VISIBLE);
            }
        });

        save_voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddVoice2Activity.this, AddVoice3Activity.class);
                intent.putExtra("session",session.getBundle());
                intent.putExtra("profile", profile.getBundle());
                startActivity(intent);
            }
        });

    }
}
