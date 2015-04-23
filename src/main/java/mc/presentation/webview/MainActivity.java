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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends Activity {
    public static final String TITLE = MainActivity.class.getSimpleName();

    private static final Class[] ACTIVITIES = new Class[]
    {
        TrackPageLoadingStateActivity.class,
        JavaScriptInterfaceTestActivity.class,
        DataSizeTestActivity.class,
        RetainWebViewStateActivity.class,
        VideoPlayerTestActivity.class,
        InputFileTestActivity.class,
        TrackNetworkStateActivity.class,
        MultipleWebViewTestActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(TITLE);

        LinearLayout container = (LinearLayout) findViewById(R.id.main_activity);
        for (int i = 0; i < ACTIVITIES.length; i++) {
            Class c = ACTIVITIES[i];
            Button b = new Button(this);
            b.setText(c.getSimpleName());
            b.setTag(i);
            b.setOnClickListener(mOnClickListener);
            container.addView(b, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, ACTIVITIES[(Integer) view.getTag()]));
        }
    };
}
