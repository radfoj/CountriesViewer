package cz.fojtik.radek.countryviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import java.util.Arrays;
import java.util.regex.Pattern;

import cz.fojtik.radek.countryviewer.sqlite.DbAdapter;
import cz.fojtik.radek.countryviewer.sqlite.Tables;


public class PageSettings {

    private Activity mActivity;
    private Button mSaveBtn;
    private Button mResetBtn;
    private Switch mSaveHistory;
    private Switch mSaveFound;
    private Spinner mMaxHistorySpinner;
    private DbAdapter db;
    private String[] par_mHist;
    private  PageHistory ph;
    private boolean onlyFoundRes;
    private int maxRecordsHistory;
    private boolean saveHistory;

    public boolean isOnlyFoundRes() {
        return onlyFoundRes;
    }

    public void setOnlyFoundRes(boolean onlyFoundRes) {
        this.onlyFoundRes = onlyFoundRes;
    }

    public int getMaxRecordsHistory() {
        return maxRecordsHistory;
    }

    public void setMaxRecordsHistory(int maxRecordsHistory) {
        this.maxRecordsHistory = maxRecordsHistory;
    }

    public boolean isSaveHistory() {
        return saveHistory;
    }

    public void setSaveHistory(boolean saveHistory) {
        this.saveHistory = saveHistory;
    }

    public PageSettings(Activity activity, PageHistory ph1)
    {
        ph = ph1;
        mActivity = activity;
        mSaveBtn = (Button) activity.findViewById(R.id.applySettings);
        mSaveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                SaveSettings();
            }
        });
        mResetBtn = (Button) activity.findViewById(R.id.resetBtn);
        mResetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                ResetSettings();
            }
        });

        mSaveHistory = (Switch)activity.findViewById(R.id.saveHistory);
        mSaveFound = (Switch)activity.findViewById(R.id.saveOnlyFound);
        mMaxHistorySpinner = (Spinner)activity.findViewById(R.id.inputSpinner);
        db = new DbAdapter(activity);
        par_mHist =  activity.getResources().getStringArray(R.array.maxHistory);
        LoadFromDB();

    }

    public void LoadFromDB()
    {
        String[] values =db.getData(Tables.settings.dbName(), new String[]{"save_history", "save_found", "max_history"}).get(0).split(Pattern.quote("|"));
        mSaveHistory.setChecked(Integer.parseInt(values[0]) == 1 ? true : false);
        mSaveFound.setChecked(Integer.parseInt(values[1]) == 1 ? true : false);
        mMaxHistorySpinner.setSelection(Arrays.asList(par_mHist).indexOf(values[2].trim()));
        Initialize();
    }

    public void ResetSettings()
    {
        db.delete(Tables.settings.dbName(), "_id>0");
        db.insertData(Tables.settings.dbName(),new String[]{"save_history", "save_found", "max_history"}, new String[]{"1", "1", "15"});
        LoadFromDB();
    }

    public void SaveSettings()
    {
        db.delete(Tables.settings.dbName(), "_id>0");
        Initialize();
        db.insertData(Tables.settings.dbName(),new String[]{"save_history", "save_found", "max_history"}, new String[]{saveHistory ? "1" : "0"
                , onlyFoundRes ? "1" : "0", Integer.toString(maxRecordsHistory)});

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mActivity);
        }
        builder.setTitle("The settings was saved!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}})
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        if(onlyFoundRes)
        {
            db.delete(Tables.history.dbName(),"found='0'");
            ph.RemoveHistoryFromGui();
            ph.LoadData();
        }
        if(maxRecordsHistory < db.GetCountRecords(Tables.history.dbName()))
        {
            int num = db.GetCountRecords(Tables.history.dbName()) - maxRecordsHistory;

            db.delete(Tables.history.dbName(),"_id not in (select _id from history order by create_time desc LIMIT " + Integer.toString(maxRecordsHistory)+ " )");
            ph.RemoveHistoryFromGui();
            ph.LoadData();

        }
    }

    public void Initialize()
    {
        setMaxRecordsHistory(Integer.parseInt(par_mHist[mMaxHistorySpinner.getSelectedItemPosition()]));
        setSaveHistory(mSaveHistory.isChecked());
        setOnlyFoundRes(mSaveFound.isChecked());
    }

}