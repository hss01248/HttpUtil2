package com.hss01248.httpdemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(this,MainActivityNew.class));
                break;
            case R.id.button2:
                WanandroidActivity.launch(this);
                break;
        }
    }

    public boolean verifyHostname(String hostname, String pattern) {
        // Basic sanity checks
        // Check length == 0 instead of .isEmpty() to support Java 5.
        if ((hostname == null) || (hostname.length() == 0) || (hostname.startsWith("."))
                || (hostname.endsWith(".."))) {
            // Invalid domain name
            return false;
        }
        if ((pattern == null) || (pattern.length() == 0) || (pattern.startsWith("."))
                || (pattern.endsWith(".."))) {
            // Invalid pattern/domain name
            return false;
        }

        // Normalize hostname and pattern by turning them into absolute domain names if they are not
        // yet absolute. This is needed because server certificates do not normally contain absolute
        // names or patterns, but they should be treated as absolute. At the same time, any hostname
        // presented to this method should also be treated as absolute for the purposes of matching
        // to the server certificate.
        //   www.android.com  matches www.android.com
        //   www.android.com  matches www.android.com.
        //   www.android.com. matches www.android.com.
        //   www.android.com. matches www.android.com
        if (!hostname.endsWith(".")) {
            hostname += '.';
        }
        if (!pattern.endsWith(".")) {
            pattern += '.';
        }
        // hostname and pattern are now absolute domain names.

        pattern = pattern.toLowerCase(Locale.US);
        // hostname and pattern are now in lower case -- domain names are case-insensitive.

        if (!pattern.contains("*")) {
            // Not a wildcard pattern -- hostname and pattern must match exactly.
            return hostname.equals(pattern);
        }
        // Wildcard pattern

        // WILDCARD PATTERN RULES:
        // 1. Asterisk (*) is only permitted in the left-most domain name label and must be the
        //    only character in that label (i.e., must match the whole left-most label).
        //    For example, *.example.com is permitted, while *a.example.com, a*.example.com,
        //    a*b.example.com, a.*.example.com are not permitted.
        // 2. Asterisk (*) cannot match across domain name labels.
        //    For example, *.example.com matches test.example.com but does not match
        //    sub.test.example.com.
        // 3. Wildcard patterns for single-label domain names are not permitted.

        if ((!pattern.startsWith("*.")) || (pattern.indexOf('*', 1) != -1)) {
            // Asterisk (*) is only permitted in the left-most domain name label and must be the only
            // character in that label
            return false;
        }

        // Optimization: check whether hostname is too short to match the pattern. hostName must be at
        // least as long as the pattern because asterisk must match the whole left-most label and
        // hostname starts with a non-empty label. Thus, asterisk has to match one or more characters.
        if (hostname.length() < pattern.length()) {
            // hostname too short to match the pattern.
            return false;
        }

        if ("*.".equals(pattern)) {
            // Wildcard pattern for single-label domain name -- not permitted.
            return false;
        }

        // hostname must end with the region of pattern following the asterisk.
        String suffix = pattern.substring(1);
        if (!hostname.endsWith(suffix)) {
            // hostname does not end with the suffix
            return false;
        }

        // Check that asterisk did not match across domain name labels.
        int suffixStartIndexInHostname = hostname.length() - suffix.length();
        if ((suffixStartIndexInHostname > 0)
                && (hostname.lastIndexOf('.', suffixStartIndexInHostname - 1) != -1)) {
            // Asterisk is matching across domain name labels -- not permitted.
            return false;
        }

        // hostname matches pattern
        return true;
    }

    public void verfiyhost(View view) {
        Log.d("dd",verifyHostname("test.baidu.com","*.baidu")+"") ;
    }
}
