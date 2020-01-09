package com.example.upita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upita.Adapter.BarangAdapter;
import com.example.upita.Model.BarangModel;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private List<BarangModel> data = new ArrayList<>();

    //Declare an Image URI, which will be in use later
    Uri mImageUri;
    Bitmap mImageBitmap;
    static int now = 0;
    private RequestQueue mQueue;
    private static final String URL = "http://192.168.0.195:5000/upload";
    public static int totalharga = 0;


    BarangAdapter mAdapter;
    RecyclerView rvBarang;
    TextView totalhrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBarang = findViewById(R.id.rv_barang);
        mQueue = Volley.newRequestQueue(this);
        totalhrg = findViewById(R.id.total_belanja);
        mAdapter = new BarangAdapter(MainActivity.this, data);
        mAdapter.setOnDataChangeListener(new BarangAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                totalhrg.setText(String.valueOf(totalharga));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setMode(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public void setMode(int selectedMode) {
        if (selectedMode == R.id.action_list) {
            CropImage.activity().start(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                // Convert URI ke Bitmap, karna harus bentuk Bitmap kalau mau disimpan ke Storage
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), mImageUri);
                    Log.d("TesGambar", (mImageBitmap == null ? "Kosong" : "Ada Gambarnya"));
                    jsonParse(mImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                Toast.makeText(this, "Possible error is : " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void jsonParse(final Bitmap gambar) {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s = response.trim();
                JSONObject jsonresponse = null;
                try {
                    jsonresponse = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!s.equalsIgnoreCase("Loi")) {
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();

                    try {
                        //Disini parsing JSONnya sesuai dengan bentuk JSON
                        String namabrg = jsonresponse.getString("nama");
                        String hargabrg = jsonresponse.getString("harga");
                        int jumlah = 1;

                        BarangModel barangModel = new BarangModel();
                        barangModel.setHarga(hargabrg); //Ini buat set ke textview atau layout
                        barangModel.setNama(namabrg);
                        barangModel.setJumlah(1);
                        totalharga += Integer.parseInt(hargabrg);
                        totalhrg.setText(String.valueOf(totalharga));


                        Log.d("Response JSON", namabrg + " : " + hargabrg);

                        data.add(barangModel);
                        if (now != 0) {
                            now = 1;
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }catch (Exception e){
                        System.out.println(e.toString());
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String image = getStringImage(gambar);
                Map<String, String> params = new HashMap<String, String>();
                Random random = new Random();
                int randomNumber = random.nextInt(999999 - 100000) + 100000;
                String fileName = "Image" + String.valueOf(randomNumber)+".png";
                params.put("FileName",fileName);
                params.put("IMG", image);
                return params;
            }
        };
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            rvBarang.setHasFixedSize(true);
            rvBarang.setAdapter(mAdapter);
            rvBarang.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            pdLoading.dismiss();
            requestQueue.add(stringRequest);
        }

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            //Disini parsing JSONnya sesuai dengan bentuk JSON
//                            String namabrg = response.getString("nama");
//                            String hargabrg = response.getString("harga");
//                            int jumlah = 1;
//
//                            BarangModel barangModel = new BarangModel();
//                            barangModel.setHarga(hargabrg); //Ini buat set ke textview atau layout
//                            barangModel.setNama(namabrg);
//                            barangModel.setJumlah(1);
//
//                            Log.d("Response JSON", namabrg + " : " + hargabrg);
//
//                            data.add(barangModel);
//                            if(now != 0){
//                                now = 1;
//                            }else{
//                                mAdapter.notifyDataSetChanged();
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        rvBarang.setHasFixedSize(true);
//        mAdapter = new BarangAdapter(MainActivity.this, data);
//        rvBarang.setAdapter(mAdapter);
//        rvBarang.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//
//        pdLoading.dismiss();
//
//        mQueue.add(request);
    }
    public String getStringImage(Bitmap bm) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
