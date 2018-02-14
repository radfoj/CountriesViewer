package cz.fojtik.radek.countryviewer;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import cz.fojtik.radek.countryviewer.R;
import cz.fojtik.radek.countryviewer.api.ApiClient;
import cz.fojtik.radek.countryviewer.api.ApiInterface;
import cz.fojtik.radek.countryviewer.api.ResponseData;
import cz.fojtik.radek.countryviewer.sqlite.DbAdapter;
import cz.fojtik.radek.countryviewer.sqlite.Message;
import cz.fojtik.radek.countryviewer.sqlite.Tables;
import cz.fojtik.radek.countryviewer.svg.SvgDecoder;
import cz.fojtik.radek.countryviewer.svg.SvgDrawableTranscoder;
import cz.fojtik.radek.countryviewer.svg.SvgSoftwareLayerSetter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageHistory {

    private Activity mActivity;
    private Button mFind;
    private TableLayout mTable;
    private TextView mName;
    private TextView mCapital;
    private CheckBox mCheck;
    private DbAdapter db;

    public PageHistory(Activity activity)
    {
        mActivity = activity;
        mTable = (TableLayout)activity.findViewById(R.id.historyTab);
        mFind = (Button)activity.findViewById(R.id.button);
        mFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                ClearHistory();
            }
        });
        db = new DbAdapter(mActivity);
        if(mTable.getChildCount() == 0)
            this.LoadData();

    }

    public void ClearHistory(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mActivity);
        }
        builder.setTitle("Clear history")
                .setMessage("Are you sure you want to clear your history?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(Tables.history.dbName(), "_id>0");

                        RemoveHistoryFromGui();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void RemoveHistoryFromGui()
    {
        mTable.removeAllViews();
    }

    public  void LoadData()
    {
        for (final String itm : db.getData(Tables.history.dbName(),new String[]{"name","create_time", "found"}))
            AddNewRow(itm.split(Pattern.quote("|"))[0], itm.split(Pattern.quote("|"))[1],Integer.parseInt(itm.split(Pattern.quote("|"))[2]) == 1 ? false:true);
    }

    public void AddNewRow(String name, String sysdate, boolean notFound){

            TableRow row= new TableRow(mActivity);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            mName = new TextView(mActivity);
            mCapital  = new TextView(mActivity);
            mName.setText(sysdate);
            mCapital.setText(name);
            mName.setPadding(5,5,5,5);
            mCapital.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT));
            mName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT));
            mCheck = new CheckBox(mActivity);
            mCheck.setText(notFound ? "Not found" : "Found");
            mCheck.setChecked(notFound ? false : true);
            mCheck.setTextColor( notFound ? mActivity.getResources().getColor(R.color.redColor) : mActivity.getResources().getColor(R.color.colorAccent));
            mCheck.setEnabled(false);

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked} , // checked
                    },
                    new int[]{
                            Color.parseColor("#ff0000"),
                            Color.parseColor("#008000"),
                    }
            );

        CompoundButtonCompat.setButtonTintList(mCheck,colorStateList);

            mCheck.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT));
            mTable.setStretchAllColumns(true);
            row.setPadding(10,10,10,10);
            mCapital.setGravity(Gravity.CENTER_VERTICAL);
            mName.setGravity(Gravity.CENTER_VERTICAL);
            mCheck.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(mName);
            row.addView(mCapital);
            row.addView(mCheck);

            mTable.addView(row);


    }

    public void RemoveFirstRow()
    {
        mTable.removeViewAt(0);
    }

}
