package application.ucsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String NAME_MESSAGE = "name";
    public static final String URL_MESSAGE = "url";
    public static final String AVATAR_MESSAGE = "avatar";

    private static final String ACCESS_TOKEN = "4feb9dcac58f614eebe66355565ddd6ac43f1f85";

    public NamePicsUrl namePicsUrl;
    RequestQueue rQueue;
    ListView nameAvatarListView;
    TextView errorView;

    int pageToDownload;
    boolean pageIsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        rQueue = Volley.newRequestQueue(getApplicationContext());

        namePicsUrl = new NamePicsUrl();

        nameAvatarListView = (ListView) findViewById(R.id.list);
        nameAvatarListView.setDivider(null); //to remove dividers from the list view
        errorView = (TextView) findViewById(R.id.message_n_error);

        pageToDownload = 1;
        pageIsRemaining = true;
        downloadNameAvatar();

    }

    private static class ViewHolder {
        private TextView nameTextView;
        private SimpleDraweeView avatarDraweeView;
    }


    private class MyAdapter extends BaseAdapter {
        ArrayList<String> name, avatar, url;

        private MyAdapter(ArrayList<String> i, ArrayList<String> j, ArrayList<String> k) {
            name = i;
            avatar = j;
            url = k;
        }

        public int getCount() {
            return name.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_view_format, parent, false);
                holder = new ViewHolder();
                holder.nameTextView = (TextView) convertView.findViewById(R.id.username_view_main);
                holder.avatarDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.avatar_view_main);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String cross_over_name = name.get(position);
            final String cross_over_url = url.get(position);
            final String cross_over_avatar = avatar.get(position);

            holder.nameTextView.setText(cross_over_name);
            Uri imageUri = Uri.parse(cross_over_avatar);
            holder.avatarDraweeView.setImageURI(imageUri);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToProfile(cross_over_name, cross_over_url, cross_over_avatar);
                }
            });

            return convertView;

        }
    }

    public void goToProfile(String extra_name, String extra_url, String extra_avatar) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(NAME_MESSAGE, extra_name);
        intent.putExtra(URL_MESSAGE, extra_url);
        intent.putExtra(AVATAR_MESSAGE, extra_avatar);
        startActivity(intent);
    }

    public void downloadNameAvatar() {
        String url_page = "https://api.github.com/search/users?per_page=100&page=" + pageToDownload;
        String url_parameters_n_token = "&q=location:lagos+type:user+language:java&sort=repositories?access_token=" + ACCESS_TOKEN;
        String url = url_page + url_parameters_n_token;

        JsonObjectRequest requestNameAvatar = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_array = response.getJSONArray("items");
                            if(response_array.length() < 100) pageIsRemaining = false; //where 100 is length for a full page.
                            for (int i = 0; i < response_array.length(); i++) {
                                JSONObject person = response_array.getJSONObject(i);
                                namePicsUrl.add(person.getString("login"), person.getString("avatar_url"), person.getString("html_url"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("Fail 1", e.toString());
                            Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_LONG).show();
                        }

                        pageToDownload++;

                        if(!pageIsRemaining){
                            nameAvatarListView.setAdapter(new MyAdapter(namePicsUrl.nameArrayList, namePicsUrl.avatarArrayList, namePicsUrl.urlArrayList));
                            errorView.setVisibility(View.INVISIBLE);
                        } else {
                            downloadNameAvatar();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fail volley: ", error.toString());
                errorView.setText("[Java Developers list loading.]\n\nError retrieving data. Check connection and tap anywhere on your screen to continue");
                errorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorView.setText("[Java Developers list loading.]\n\nLoading profiles...");
                        downloadNameAvatar();
                    }
                });
            }



        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String basicLogin = new String(Base64.encode((ACCESS_TOKEN).getBytes(), Base64.NO_WRAP));
                headers.put("Authorization", "Basic " + basicLogin);
                headers.put("Content-type", "application/json");
                return headers;
            }
        };

        rQueue.add(requestNameAvatar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rQueue.cancelAll(this);
    }


}
