package emp.projektna.swishis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetFragment extends Fragment {

    private RequestQueue requestQueue;
    private TextView users;
    private String url = "https://swish-is.azurewebsites.net/api/v1/User";
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        users = view.findViewById(R.id.tvUsers);

        button = view.findViewById(R.id.btDisplay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("ApiKey", "SecretKey");
                        return params;
                    }


                };
                requestQueue.add(request);

            }
        });
        return view;
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    String name = obj.getString("name");
                    String email = obj.getString("email");
                    data.add(name + ", " + email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            users.setText("");

            for (String row : data) {
                String currentText = users.getText().toString();
                users.setText(currentText + "\n\n" + row);
            }
        }
    };


    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST ERROR", error.toString());
        }
    };
}



