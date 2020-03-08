package likemed.wonbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SettingActivity extends AppCompatActivity {
    //TextView cardlist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final EditText ettel_card1 = (EditText) findViewById(R.id.tel_card1);
        final EditText ettxt_card1 = (EditText) findViewById(R.id.txt_card1);
        final EditText etno_card1 = (EditText) findViewById(R.id.no_card1);
        final EditText ettel_card2 = (EditText) findViewById(R.id.tel_card2);
        final EditText ettxt_card2 = (EditText) findViewById(R.id.txt_card2);
        final EditText etno_card2 = (EditText) findViewById(R.id.no_card2);
        final EditText ettel_card3 = (EditText) findViewById(R.id.tel_card3);
        final EditText ettxt_card3 = (EditText) findViewById(R.id.txt_card3);
        final EditText etno_card3 = (EditText) findViewById(R.id.no_card3);
        //cardlist = (TextView) findViewById(R.id.cardlist);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        final String id = settings.getString("id", "");
        String tel_card1 = settings.getString("tel_card1", "");
        if (!tel_card1.isEmpty()) {
            String txt_card1 = settings.getString("txt_card1", "");
            String no_card1 = settings.getString("np_card1", "");
            ettel_card1.setText(tel_card1);
            ettxt_card1.setText(txt_card1);
            etno_card1.setText(no_card1);
        }
        String tel_card2 = settings.getString("tel_card2", "");
        if (!tel_card2.isEmpty()) {
            String txt_card2 = settings.getString("txt_card2", "");
            String no_card2 = settings.getString("np_card2", "");
            ettel_card2.setText(tel_card2);
            ettxt_card2.setText(txt_card2);
            etno_card2.setText(no_card2);
        }
        String tel_card3 = settings.getString("tel_card3", "");
        if (!tel_card3.isEmpty()) {
            String txt_card3 = settings.getString("txt_card3", "");
            String no_card3 = settings.getString("np_card3", "");
            ettel_card3.setText(tel_card3);
            ettxt_card3.setText(txt_card3);
            etno_card3.setText(no_card3);
        }

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("settings", 0).edit();
                String tel_card1 = ettel_card1.getText().toString().trim();
                String txt_card1 = ettxt_card1.getText().toString().trim();
                String no_card1 = etno_card1.getText().toString().trim();
                editor.putString("tel_card1", tel_card1);
                editor.putString("txt_card1", txt_card1);
                editor.putString("no_card1", no_card1);
                String tel_card2 = ettel_card2.getText().toString().trim();
                String txt_card2 = ettxt_card2.getText().toString().trim();
                String no_card2 = etno_card2.getText().toString().trim();
                editor.putString("tel_card2", tel_card2);
                editor.putString("txt_card2", txt_card2);
                editor.putString("no_card2", no_card2);
                String tel_card3 = ettel_card3.getText().toString().trim();
                String txt_card3 = ettxt_card3.getText().toString().trim();
                String no_card3 = etno_card3.getText().toString().trim();
                editor.putString("tel_card3", tel_card3);
                editor.putString("txt_card3", txt_card3);
                editor.putString("no_card3", no_card3);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent sendintent = getPackageManager().getLaunchIntentForPackage("likemed.wonokok");
                sendintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sendintent.putExtra("id", id);
                startActivity(intent);
                startActivity(sendintent);
                finish();
            }
        });

        ((Button) findViewById(R.id.resetbtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("settings", 0).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent sendintent = getPackageManager().getLaunchIntentForPackage("likemed.wonokok");
                sendintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sendintent.putExtra("id", "@reset");
                startActivity(intent);
                startActivity(sendintent);
                finish();
            }
        });
    }
}