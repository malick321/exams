package pk.examprep.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Thin native shell around the offline Exam Prep HTML file.
 *
 * The whole app — all 5,274 questions, every screen, all the logic — lives in
 * assets/index.html. This class only does the three things a web page cannot do
 * for itself inside a WebView: keep localStorage alive, open the file picker,
 * and write exported files to the Downloads folder.
 */
public class MainActivity extends AppCompatActivity {

    private WebView web;
    private ValueCallback<Uri[]> filePicker;
    private static final int REQ_FILE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        setContentView(web, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // localStorage — all progress depends on this
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);   // lets the 25%-time beep play
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setTextZoom(100);                    // ignore system font scaling so layout stays put
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);

        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "AndroidBridge");

        // <input type="file"> — needed by Bulk upload and Import backup
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> cb,
                                             FileChooserParams params) {
                if (filePicker != null) filePicker.onReceiveValue(null);
                filePicker = cb;
                try {
                    Intent i = params.createIntent();
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(i, "Choose a file"), REQ_FILE);
                    return true;
                } catch (Exception e) {
                    filePicker = null;
                    return false;
                }
            }
        });

        // Hardware / gesture back: the page pushes history entries for every screen,
        // so goBack() walks the app's own navigation before the activity closes.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (web.canGoBack()) {
                    web.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != REQ_FILE) return;
        if (filePicker == null) return;
        filePicker.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data));
        filePicker = null;
    }

    private void toPage(String msg) {
        final String js = "onNativeSaved(" + JSONObject.quote(msg) + ")";
        runOnUiThread(new Runnable() {
            @Override public void run() { web.evaluateJavascript(js, null); }
        });
    }

    private static String mimeFor(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".json")) return "application/json";
        if (n.endsWith(".csv"))  return "text/csv";
        if (n.endsWith(".xlsx")) return
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        return "application/octet-stream";
    }

    /** Exposed to the page as window.AndroidBridge */
    public class Bridge {

        /** Writes an exported backup or question template into the phone's Downloads. */
        @JavascriptInterface
        public void saveBase64(final String name, final String b64) {
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        byte[] bytes = Base64.decode(b64, Base64.DEFAULT);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContentValues cv = new ContentValues();
                            cv.put(MediaStore.Downloads.DISPLAY_NAME, name);
                            cv.put(MediaStore.Downloads.MIME_TYPE, mimeFor(name));
                            cv.put(MediaStore.Downloads.IS_PENDING, 1);
                            Uri uri = getContentResolver()
                                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
                            if (uri == null) { toPage("Could not save the file"); return; }
                            OutputStream os = getContentResolver().openOutputStream(uri);
                            os.write(bytes);
                            os.close();
                            cv.clear();
                            cv.put(MediaStore.Downloads.IS_PENDING, 0);
                            getContentResolver().update(uri, cv, null, null);
                            toPage("Saved to Downloads — " + name);
                        } else {
                            // No storage permission needed for the app's own external folder
                            File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            if (dir != null && !dir.exists()) dir.mkdirs();
                            File f = new File(dir, name);
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bytes);
                            fos.close();
                            toPage("Saved to " + f.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        toPage("Could not save the file");
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onPause() {
        // give the page a chance to flush progress to localStorage before we lose focus
        if (web != null) web.evaluateJavascript("try{tickTime();save(true);}catch(e){}", null);
        super.onPause();
    }
}
