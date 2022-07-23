package com.example.youtubeapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.youtubeapp.R;
import com.example.youtubeapp.activitys.MainActivity;
import com.example.youtubeapp.adapter.HintAdapter;
import com.example.youtubeapp.my_interface.IItemOnClickHintListener;
import com.example.youtubeapp.my_interface.IItemOnClickSearchListener;
import com.example.youtubeapp.utiliti.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    public static final String TAG = SearchFragment.class.getName();
    public static int REQUEST_CODE = 1233;
    private ImageButton ibBack;
    String how;
    EditText etSearch;
    RecyclerView rvListHint;
    ArrayList<String> listHint;
    HintAdapter adapter;
    MainActivity mainActivity;
    Toolbar tbSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        backHome();
        getBundle();
        etSearch.setText(how);
        etSearch.requestFocus();
        mainActivity = (MainActivity) getActivity();

        listHint = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        rvListHint.setLayoutManager(linearLayoutManager);
        adapter = new HintAdapter(new IItemOnClickHintListener() {
            @Override
            public void onClickListener(String s) {
                etSearch.setText(s);
            }
        }, // khi click tìm kiếm
                new IItemOnClickSearchListener() {
            @Override
            public void onClickSearchListener(String q) {
                mainActivity.addFragmentSearchResults(q);
                etSearch.setText(q);
            }
        });
        rvListHint.setAdapter(adapter);
        adapter.setData(listHint);

        PackageManager pm = requireContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
//            speak.setEnabled(false);
//            speak.setText("Recognizer not present");
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = etSearch.getText().toString();
                tbSearch.getMenu().findItem(R.id.mn_voice_search).setVisible(false);
                tbSearch.getMenu().findItem(R.id.mn_close_search).setVisible(true);
                callApiHintSearch(ss);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = etSearch.getText().toString();
                if (ss.equals("")) {
                    tbSearch.getMenu().findItem(R.id.mn_voice_search).setVisible(true);
                    tbSearch.getMenu().findItem(R.id.mn_close_search).setVisible(false);
                    listHint.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String s = etSearch.getText().toString();
                    mainActivity.addFragmentSearchResults(s);
                    etSearch.setText(s);
                    return true;
                }
                return false;
            }
        });

//        callApiHintSearch("thích");

        // sự kiện nút xóa edittext
        tbSearch.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_close_search:
                        etSearch.setText("");
                        break;
                    case R.id.mn_voice_search:
                        startVoiceRecognitionActivity();
                        break;
                }
                return false;
            }
        });

        return view;
    }
//  voice search
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == -1)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList < String > matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty())
            {
                String Query = matches.get(0);
                etSearch.setText(Query);
                mainActivity.addFragmentSearchResults(Query);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("duc1", "onAttackSearch");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("duc1", "onResumeSearch");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("duc1", "onStartSearch");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("duc1", "onPauseSearch");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("duc1", "onStopSearch");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("duc1", "onDestroySearch");
        Util.FRAGMENT_CURRENT = 1;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("duc1", "onDetachSearch");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("duc1", "onDestroyviewSearch");
    }

    private void initView(View view) {
        ibBack = view.findViewById(R.id.ib_back_search);
        etSearch = view.findViewById(R.id.et_search);
        rvListHint = view.findViewById(R.id.rv_list_hint_search);
        tbSearch = view.findViewById(R.id.tb_nav_search);
    }
    // Quay trở lại trước đó
    private void backHome() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getParentFragmentManager().popBackStack();
                    if (Util.FRAGMENT_CURRENT == 1) {
                        mainActivity.setToolBarMainVisible();
                    }
                    mainActivity.setBnvVisible();
            }
        });
    }

    private void callApiHintSearch(String hint) {
        listHint.clear();
        String url = "http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&client=firefox&q="  + hint + "";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Object string = response.get(1);
                    String s = string.toString();
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String string1 = (String) jsonArray.get(i);
                        listHint.add(string1);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getBundle() {
        Bundle bundleRe = getArguments();
        how = bundleRe.getString(Util.BUNDLE_EXTRA_TEXT_EDITTEXT);
    }
}