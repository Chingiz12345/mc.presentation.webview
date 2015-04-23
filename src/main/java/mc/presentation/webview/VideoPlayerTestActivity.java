/*
   Copyright 2015 Mikhail Chabanov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package mc.presentation.webview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class VideoPlayerTestActivity extends Activity {
    public static final String TITLE = VideoPlayerTestActivity.class.getSimpleName();

    private WebChromeClient.CustomViewCallback mFullscreenViewCallback;
    private FrameLayout mFullScreenContainer;
    private View mFullScreenView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setTitle(TITLE);

        mWebView = (WebView) findViewById(R.id.web_view);
        mFullScreenContainer = (FrameLayout) findViewById(R.id.fullscreen_container);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.loadUrl("file://" + App.getHtmlFolder() + "/" + TITLE + ".html");
    }

    private final WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        @SuppressWarnings("deprecation")
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mFullScreenView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mFullScreenView = view;
            mWebView.setVisibility(View.GONE);

            mFullScreenContainer.setVisibility(View.VISIBLE);
            mFullScreenContainer.addView(view);
            mFullscreenViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mFullScreenView == null) {
                return;
            }
            mWebView.setVisibility(View.VISIBLE);
            mFullScreenView.setVisibility(View.GONE);
            mFullScreenContainer.setVisibility(View.GONE);
            mFullScreenContainer.removeView(mFullScreenView);
            mFullscreenViewCallback.onCustomViewHidden();
            mFullScreenView = null;
        }
    };
}
