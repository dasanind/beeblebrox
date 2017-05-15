package com.msreport.beeblebox;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;
 
public class TextDownloader {
 
	static public String downloadText(String url) {
//	    final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 
		            SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();

		SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

		HttpClient client = new DefaultHttpClient(mgr, params);
	    final HttpGet getRequest = new HttpGet(url);
	    
	    try {
	        HttpResponse response = client.execute(getRequest);
	        final int statusCode = response.getStatusLine().getStatusCode();
	        
	        if (statusCode != HttpStatus.SC_OK) { 
	            Log.w("TextDownloader", "Error " + statusCode + " while retrieving bitmap from " + url); 
	            return null;
	        }
 
	        final HttpEntity entity = response.getEntity();
	        
	        if (entity != null) {
	        	BufferedHttpEntity buf = new BufferedHttpEntity(entity);
	        	Log.d("TextDownloader","TextDownloader: " + url + " statusCode "+ statusCode);
	            InputStream inputStream = null;
	            try {
	                inputStream = buf.getContent(); 
	                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
//	                Log.d("TextDownloader","in.readLine(): " + in.readLine());
	                String ipaddress = null;
	                String line;
	       	     	while ((line = in.readLine()) != null) {
	       	     		ipaddress = line;
	       	     	}
	       	     Log.d("TextDownloader","ipaddress: " + ipaddress);
	                return ipaddress;
	            } finally {
	                if (inputStream != null) {
	                    inputStream.close();  
	                }
	                entity.consumeContent();
	            }
	        }
	    } catch (Exception e) {
	        // Could provide a more explicit error message for IOException or IllegalStateException
	        getRequest.abort();
	        Log.w("ImageDownloader", "Error while retrieving bitmap from " + url + e.toString());
	    } finally {
	        if (client != null) {
	        	//TODO
//	            client.close();
	        }
	    }
	    return null;
	}
 
	static class FlushedInputStream extends FilterInputStream {
	    public FlushedInputStream(InputStream inputStream) {
	        super(inputStream);
	    }
 
	    @Override
	    public long skip(long n) throws IOException {
	        long totalBytesSkipped = 0L;
	        while (totalBytesSkipped < n) {
	            long bytesSkipped = in.skip(n - totalBytesSkipped);
	            if (bytesSkipped == 0L) {
	                  int num_byte = read();
	                  if (num_byte < 0) {
	                      break;  // we reached EOF
	                  } else {
	                      bytesSkipped = 1; // we read one byte
	                  }
	           }
	            totalBytesSkipped += bytesSkipped;
	        }
	        return totalBytesSkipped;
	    }
	}
}