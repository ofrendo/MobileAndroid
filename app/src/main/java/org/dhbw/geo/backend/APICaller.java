package org.dhbw.geo.backend;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Oliver on 18.06.2015.
 * Adapted from http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/
 */
public class APICaller extends AsyncTask<String, String, String> {

    private final Route route;
    private final BackendCallback callback;

    public APICaller(Route route, BackendCallback callback) {
        this.callback = callback;
        this.route = route;
    }

    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);

            if (n>0) out.append(new String(b, 0, n));
        }

        return out.toString();
    }


    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();

        String url = route.url;
        HttpHost httpHost;

        HttpUriRequest request;
        switch (route.method) {
            case POST:
                request = new HttpPost(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            case GET:
            default:
                request = new HttpGet(url);
                break;
        }

        String result = null;
        try {
            HttpResponse response = httpClient.execute(request, localContext);
            HttpEntity entity = response.getEntity();
            result = getASCIIContentFromEntity(entity);
        } catch (Exception e) {
            Log.i("APICaller", "Error: " + e.getMessage());
            return e.getLocalizedMessage();
        }

        return result;
    }

    protected void onPostExecute(String result) {
        callback.actionPerformed(result);
    }

}
