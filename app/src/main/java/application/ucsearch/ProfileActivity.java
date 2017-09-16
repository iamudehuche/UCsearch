package application.ucsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class ProfileActivity extends AppCompatActivity {

    String name_message;
    String url_message;
    String avatar_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        name_message = intent.getStringExtra("name");
        url_message = intent.getStringExtra("url");
        avatar_message = intent.getStringExtra("avatar");
        TextView techieNameView = (TextView)findViewById(R.id.username_view_profile);
        String at_name_message = "@" + name_message;
        techieNameView.setText(at_name_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {getSupportActionBar().setDisplayHomeAsUpEnabled(true);} catch (Exception e) {}


        SimpleDraweeView avatar_view = (SimpleDraweeView) findViewById(R.id.avatar_view_profile);
        Uri imageUri = Uri.parse(avatar_message);
        avatar_view.setImageURI(imageUri);

        TextView urlViewProfile = (TextView) findViewById(R.id.url_view_profile);
        urlViewProfile.setText(url_message);

        urlViewProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_message));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "Check out this awesome developer" + name_message + "," + url_message + ".");
                startActivity(Intent.createChooser(i, "Share"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}