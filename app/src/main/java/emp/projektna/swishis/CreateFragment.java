package emp.projektna.swishis;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class CreateFragment extends Fragment {

    private TextView status;
    private EditText name, email, date;

    private Button button;

    private RequestQueue requestQueue;
    private String url = "https://swish-is.azurewebsites.net/api/v1/User";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        date = view.findViewById(R.id.date);
        status = view.findViewById(R.id.status);
        requestQueue = Volley.newRequestQueue(getContext());
        button = view.findViewById(R.id.btCreate);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day, month, year;
                if (!date.getText().toString().equals("")) {
                    String[] datum = date.getText().toString().split("-");
                    day = Integer.parseInt(datum[2]);
                    month = Integer.parseInt(datum[1]) - 1;
                    year = Integer.parseInt(datum[0]);
                }
                else {
                    day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    month =  Calendar.getInstance().get(Calendar.MONTH) - 1;
                    year = Calendar.getInstance().get(Calendar.YEAR);;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(year + "-" + (month+1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("Posting to " + url);
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("name", name.getText());
                    jsonBody.put("email", email.getText());
                    jsonBody.put("enrollmentDate", date.getText());

                    final String mRequestBody = jsonBody.toString();
                    status.setText(mRequestBody);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG_VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG_VOLLEY", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get bytes of %s using %s");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                status.setText(responseString);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("ApiKey", "SecretKey");
                            params.put("Content-Type", "application/json");
                            return params;
                        }


                    };
                    requestQueue.add(stringRequest);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }
}