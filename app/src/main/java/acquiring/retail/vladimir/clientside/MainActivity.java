package acquiring.retail.vladimir.clientside;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acquiring.retail.vladimir.clientside.task.AuthListener;
import acquiring.retail.vladimir.clientside.task.AuthSession;
import acquiring.retail.vladimir.clientside.task.AuthTask;

import static acquiring.retail.vladimir.clientside.task.Service.QR_CODE;
import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_PREFERENCE_NAME;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSIONS = 1;
    private Map<String,Boolean> permissions = new HashMap<>();
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    public MainActivity() {
        super();
        permissions.put(Manifest.permission.READ_PHONE_STATE,false);
        permissions.put(Manifest.permission.READ_SMS,false);
        permissions.put(Manifest.permission.READ_EXTERNAL_STORAGE,false);
        permissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,false);
        permissions.put(Manifest.permission.CAMERA,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (checkAndRequestPermissions()) {
                    new AuthTask(new AuthListener() {
                        @Override
                        public void onSuccess(final AuthSession session) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            if (session!=null) {
                                showToast("session opened");

                                final SharedPreferences settings = getSharedPreferences(SERVICE_PREFERENCE_NAME, MODE_PRIVATE);
                                String phone = settings.getString("profile.info.phone","");
                                if (phone.trim().length()==0)
                                    phone = getPhoneNumber();
                                if (phone.trim().length()==0) {
                                    showPhoneAlert(new alertOk() {
                                        @Override
                                        public void ok(String phone) {
                                            if (phone.trim().length()>0) {
                                                SharedPreferences.Editor settingsEditor = settings.edit();
                                                settingsEditor.putString("profile.info.phone",phone);
                                                settingsEditor.apply();
                                                Intent intent = new Intent(MainActivity.this, LoadProfileActivity.class);
                                                intent.putExtra("session", session.getBundle());
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                } else {
                                    Intent intent = new Intent(MainActivity.this, LoadProfileActivity.class);
                                    intent.putExtra("session", session.getBundle());
                                    startActivity(intent);
                                }
                            } else {
                                showToast("Невозможно инициализировать сессию");
                            }
                        }
                        @Override
                        public void onError(String result) {
                            showToast(result);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }).execute(QR_CODE);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});
        checkAndRequestPermissions();
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
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkAndRequestPermissions() {
        List<String> request = new ArrayList<>();
        for (String permission : permissions.keySet()) {
            try {
                int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    request.add(permission);
                } else {
                    permissions.put(permission, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (request.size()>0) {
            ActivityCompat.requestPermissions(this, request.toArray(new String[request.size()]), REQUEST_PERMISSIONS);
        }
        return isPermissionOk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_PERMISSIONS) {
            if (permissions.length>0 && permissions.length==grantResults.length) {
                for (int j=0;j<permissions.length;j++) {
                    this.permissions.put(permissions[j],grantResults[j]==PackageManager.PERMISSION_GRANTED);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (!isPermissionOk()) {
            showToast("Недостаточно прав для запуска приложения");
        }
    }

    private Boolean isPermissionOk() {
        Boolean rez = true;
        for (Boolean v:permissions.values())
            rez = rez && v;
        return rez;
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

    private interface alertOk {
        void ok(String phone);
    }

    private AlertDialog createPhoneAlertDialog(final alertOk event) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText phoneEdit = new EditText(this);
        phoneEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        alert.setMessage("Личные данные");
        alert.setTitle("Введите номер телефона");
        alert.setView(phoneEdit);
        alert.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String phone = phoneEdit.getText().toString();
                if (event!=null) event.ok(phone);
            }
        });
        return alert.create();
    }
    private void showPhoneAlert(final alertOk event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createPhoneAlertDialog(event).show();
            }
        });
    }
}
