package cz.fojtik.radek.countryviewer;

/**
 * Created by Radek on 21. 1. 2018.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DrawableUtils;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cz.fojtik.radek.countryviewer.api.ApiClient;
import cz.fojtik.radek.countryviewer.api.ResponseData;
import cz.fojtik.radek.countryviewer.api.ApiInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.fojtik.radek.countryviewer.sqlite.Tables;
import cz.fojtik.radek.countryviewer.svg.SvgDecoder;
import cz.fojtik.radek.countryviewer.svg.SvgDrawableTranscoder;
import cz.fojtik.radek.countryviewer.svg.SvgSoftwareLayerSetter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import org.w3c.dom.Text;

import cz.fojtik.radek.countryviewer.sqlite.DbAdapter;
import cz.fojtik.radek.countryviewer.sqlite.Message;





public class PageHome implements View.OnClickListener{
    private Activity mActivity;
    private EditText mText;
    private Button mFind;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;
    private CardView eResult;
    private DbAdapter db;
    private PageHistory ph;
    private PageSettings ps;
    private CardView countryContent;
    private RelativeLayout ph1 ;

    public PageHome(Activity activity)
    {
        mActivity = activity;
        mText = (EditText)activity.findViewById(R.id.lCountry);
        mFind = (Button)activity.findViewById(R.id.btnFind);
        mFind.setOnClickListener(this);
        eResult = (CardView)activity.findViewById(R.id.eResult);
        ph1 = (RelativeLayout)activity.findViewById(R.id.pageHomeLayout) ;


        db = new DbAdapter(mActivity);
        ph = new PageHistory(mActivity);
        ps = new PageSettings(mActivity, ph);



        requestBuilder = Glide.with(mActivity)
                .using(Glide.buildStreamModelLoader(Uri.class, mActivity), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
    }

    @Override
    public void onClick(final View view)
    {
        eResult.setVisibility(View.INVISIBLE);
        final String searchedName = mText.getText().toString();
        if(!searchedName.isEmpty())
        {
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<List<ResponseData>> call = apiService.getData(searchedName);
            call.enqueue(new Callback<List<ResponseData>>() {
                @Override
                public void onResponse(Call<List<ResponseData>> call, Response<List<ResponseData>> response) {

                    List<ResponseData> rs = response.body();
                    RelativeLayout layout = (RelativeLayout) mActivity.findViewById(R.id.pageHomeLayout);
                    View view2 = (View)mActivity.findViewById(R.id.header);
                    List<View> viewList= new ArrayList<View>(){};
                    for(int idx = 0; idx<layout.getChildCount();idx++)
                    {
                       View vw = layout.getChildAt(idx);
                      if(vw instanceof CardView  && vw.getId() != view2.getId() && vw.getId() != eResult.getId()) {
                          viewList.add(vw);
                      }
                    }
                    for (View deleteViews : viewList)
                        layout.removeView(deleteViews);

                    if (rs != null && !rs.isEmpty()) {
                        CardView createdCard = null;
                        int z = 1;
                        for (ResponseData responseCountry : rs) {

                           createdCard = CreateCountryCardView(responseCountry,z,createdCard,layout);

                            if(ps.isSaveHistory()) {
                                if(ps.getMaxRecordsHistory() < db.GetCountRecords(Tables.history.dbName()) + 1 )
                                {
                                    ph.RemoveFirstRow();
                                    db.delete(Tables.history.dbName(),"_id in (select _id from history order by create_time asc LIMIT 1)");
                                }
                                db.insertData(Tables.history.dbName(), new String[]{"create_time", "name", "found"}
                                        , new String[]{getSysDatetime(), responseCountry.getName(), "1"});

                                ph.AddNewRow(responseCountry.getName(), getSysDatetime(), false);
                            }

                            z++;
                            if(z>4)
                                break;

                        }
                        HideKeyword();
                    } else {
                        if(ps.isSaveHistory() && !ps.isOnlyFoundRes()) {
                            if(ps.getMaxRecordsHistory() < db.GetCountRecords(Tables.history.dbName()) + 1 )
                            {
                                ph.RemoveFirstRow();
                                db.delete(Tables.history.dbName(),"_id in (select _id from history order by create_time asc LIMIT 1)");
                            }

                            db.insertData(Tables.history.dbName(), new String[]{"create_time", "name", "found"}
                                    , new String[]{getSysDatetime(), searchedName, "0"});

                            ph.AddNewRow(searchedName, getSysDatetime(), true);
                        }

                        eResult.setVisibility(View.VISIBLE);
                        HideKeyword();
                    }
                }

                @Override
                public void onFailure(Call<List<ResponseData>> call, Throwable t) {
                    System.out.println("onFailure");
                    t.printStackTrace();

                }
            });
        }
    }


    public static String getSysDatetime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public void HideKeyword()
    {
        View view = mActivity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(mActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public CardView CreateCountryCardView(ResponseData responseCountry, int cardID, CardView bellowCard, RelativeLayout layout)
    {
        String urlFlag = responseCountry.getFlag();


        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        countryContent = (CardView) inflater.inflate(R.layout.country_info, null);

        ImageView img = (ImageView) countryContent.findViewById(R.id.img);

        Uri uri = Uri.parse(urlFlag);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(img);

        TextView n = (TextView) countryContent.findViewById(R.id.nameC);

        n.setText(responseCountry.getName());

        TableLayout t = (TableLayout) countryContent.findViewById(R.id.table1);

        String[] values = responseCountry.getValues();
        String[] headers = responseCountry.getHeader();

        for (int i = 0; i < 2; i++) {
            TableRow parent = (TableRow) inflater.inflate(R.layout.tmpl_row_ph, null);
            for (int j = 0; j < 3; j++) {
                int increment = 0;
                if (i == 1)
                    increment = 3;
                View custom = inflater.inflate(R.layout.tmpl_cell_ph, null);
                TextView t1 = (TextView) custom.findViewById(R.id.phDesc);
                TextView t2 = (TextView) custom.findViewById(R.id.phValue);
                t1.setText(headers[j + increment]);
                t2.setText(values[j + increment]);
                custom.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));
                parent.addView(custom);
            }
            t.setStretchAllColumns(true);
            t.addView(parent);
        }

        countryContent.setId(cardID);


        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(cardID == 1)
            params1.addRule(RelativeLayout.BELOW, R.id.header);
        else
            params1.addRule(RelativeLayout.BELOW, bellowCard.getId());

        countryContent.setVisibility(View.VISIBLE);

        layout.addView(countryContent, params1);

        return countryContent;
    }
}

