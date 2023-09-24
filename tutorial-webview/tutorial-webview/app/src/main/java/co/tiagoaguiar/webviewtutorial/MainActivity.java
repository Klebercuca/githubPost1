package co.tiagoaguiar.webviewtutorial;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

  private WebView webView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final ProgressBar progressBar = findViewById(R.id.progress);
    progressBar.setVisibility(View.INVISIBLE);

    webView = findViewById(R.id.webview);
    webView.getSettings().setJavaScriptEnabled(true);

    webView.loadUrl("http://criativoburger.com.br/front");
    webView.setWebViewClient(new WebViewClient() {

      // Handle API until level 21
      @SuppressWarnings("deprecation")
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        return getNewResponse(url);
      }

      // Handle API 21+
      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        return getNewResponse(url);
      }

      private WebResourceResponse getNewResponse(String url) {
        try {
          OkHttpClient httpClient = new OkHttpClient();

          Request request = new Request.Builder()
                  .url(url.trim())
                  .build();

          Response response = httpClient.newCall(request).execute();

          return new WebResourceResponse(
                  getMimeType(url),
                  response.header("content-encoding", "utf-8"),
                  response.body().byteStream()
          );

        } catch (Exception e) {
          return null;
        }
      }

      private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {

          switch (extension) {
            case "js":
              return "text/javascript";
            case "woff":
              return "application/font-woff";
            case "woff2":
              return "application/font-woff2";
            case "ttf":
              return "application/x-font-ttf";
            case "eot":
              return "application/vnd.ms-fontobject";
            case "svg":
              return "image/svg+xml";
          }

          type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
      }
    });

    /*
//        webView.addJavascriptInterface(this, "Android");

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.INVISIBLE);
      }

      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                if (request.getUrl().getHost().equals("tiagoaguiar.co")) {
//                    return false;
//                }

        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
        startActivity(intent);
        return true;
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().equals("tiagoaguiar.co")) {
          return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        return true;
      }
    });
    */

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
      webView.goBack();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  @JavascriptInterface
  public void jsShowToast(String toast) {
    new AlertDialog.Builder(this)
            .setTitle("Dialog")
            .setMessage(toast)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show();
  }

}
