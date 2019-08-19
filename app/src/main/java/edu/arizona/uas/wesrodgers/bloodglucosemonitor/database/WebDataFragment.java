package edu.arizona.uas.wesrodgers.bloodglucosemonitor.database;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import edu.arizona.uas.wesrodgers.bloodglucosemonitor.R;
import edu.arizona.uas.wesrodgers.bloodglucosemonitor.VisibleFragment;

public class WebDataFragment extends VisibleFragment {
    private static final String ARG_URI = "web_data_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static WebDataFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        WebDataFragment fragment = new WebDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.web_browser_layout, container, false);

        mWebView = (WebView) v.findViewById(R.id.web_view);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUri.toString());

        return v;
    }
}
