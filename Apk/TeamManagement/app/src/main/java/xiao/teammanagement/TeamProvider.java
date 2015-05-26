package xiao.teammanagement;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

import xiao.teammanagement.TeamProviderMetaData.MemberTableMetaData;

/**
 * Created by Xiao on 2015/5/15.
 */
public class TeamProvider extends ContentProvider
{
    //Logging helper tag. No significance to providers.
    private static final String TAG = "TeamProvider";

    //Projection maps are similar to "as" construct
    //in an sql statement where by you can rename the
    //columns.
    private static HashMap<String, String> sMembersProjectionMap;
    static
    {
        sMembersProjectionMap = new HashMap<String, String>();
        sMembersProjectionMap.put(MemberTableMetaData._ID,
                MemberTableMetaData._ID);

        //name, alias, age, sex, photo, created date, modified date
        sMembersProjectionMap.put(MemberTableMetaData.MEMBER_NAME,
                MemberTableMetaData.MEMBER_NAME);
        sMembersProjectionMap.put(MemberTableMetaData.MEMBER_ALIAS,
                MemberTableMetaData.MEMBER_ALIAS);
        sMembersProjectionMap.put(MemberTableMetaData.MEMBER_AGE,
                MemberTableMetaData.MEMBER_AGE);
        sMembersProjectionMap.put(MemberTableMetaData.MEMBER_SEX,
                MemberTableMetaData.MEMBER_SEX);
        sMembersProjectionMap.put(MemberTableMetaData.MEMBER_PHOTO,
                                MemberTableMetaData.MEMBER_PHOTO);
        sMembersProjectionMap.put(MemberTableMetaData.CREATED_DATE,
                                    MemberTableMetaData.CREATED_DATE);
        sMembersProjectionMap.put(MemberTableMetaData.MODIFIED_DATE,
                                    MemberTableMetaData.MODIFIED_DATE);

    }

    //Provide a mechanism to identify
    //all the incoming uri patterns.
    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_MEMBER_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_MEMBER_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TeamProviderMetaData.AUTHORITY, "members",
                INCOMING_MEMBER_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(TeamProviderMetaData.AUTHORITY, "members/#",
                INCOMING_SINGLE_MEMBER_URI_INDICATOR);

    }

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context,
                    getMyDatabaseName(context),
                    null,
                    TeamProviderMetaData.DATABASE_VERSION);
        }


        private static String getMyDatabaseName(Context context){
            String databasename = TeamProviderMetaData.DATABASE_NAME;
            boolean isSdcardEnable = false;
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)){//SDCard是否插入
                isSdcardEnable = true;
            }
            String dbPath = null;
            if(isSdcardEnable){
                dbPath = Environment.getExternalStorageDirectory().getPath() + "/database/";
            }else{//未插入SDCard，建在内存中
                dbPath = context.getFilesDir().getPath() + "/databases/";
            }
            File dbp = new File(dbPath);
            if(!dbp.exists()){
                dbp.mkdirs();
            }
            databasename = dbPath + databasename;
            return databasename;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.d(TAG, "inner oncreate called");
            db.execSQL("CREATE TABLE " + MemberTableMetaData.TABLE_NAME + " ("
                    + MemberTableMetaData._ID + " INTEGER PRIMARY KEY,"
                    + MemberTableMetaData.MEMBER_NAME + " TEXT,"
                    + MemberTableMetaData.MEMBER_ALIAS + " TEXT,"
                    + MemberTableMetaData.MEMBER_SEX + " INTEGER,"
                    + MemberTableMetaData.MEMBER_AGE + " INTEGER,"
                    + MemberTableMetaData.CREATED_DATE + " INTEGER,"
                    + MemberTableMetaData.MODIFIED_DATE + " INTEGER,"
                    + MemberTableMetaData.MEMBER_PHOTO + " BLOB"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.d(TAG,"inner onupgrade called");
            Log.w(TAG, "Upgrading database from version "
                    + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " +
                    MemberTableMetaData.TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate()
    {
        Log.d(TAG,"main onCreate called");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs,  String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case INCOMING_MEMBER_COLLECTION_URI_INDICATOR:
                qb.setTables(MemberTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sMembersProjectionMap);
                break;

            case INCOMING_SINGLE_MEMBER_URI_INDICATOR:
                qb.setTables(MemberTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sMembersProjectionMap);
                qb.appendWhere(MemberTableMetaData._ID + "="
                        + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = MemberTableMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection,
                selectionArgs, null, null, orderBy);

        //example of getting a count
        int i = c.getCount();

        // Tell the cursor what uri to watch,
        // so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MEMBER_COLLECTION_URI_INDICATOR:
                return MemberTableMetaData.CONTENT_TYPE;

            case INCOMING_SINGLE_MEMBER_URI_INDICATOR:
                return MemberTableMetaData.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri)
                != INCOMING_MEMBER_COLLECTION_URI_INDICATOR)
        {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = System.currentTimeMillis();

        // Make sure that the fields are all set
        if (! values.containsKey(MemberTableMetaData.CREATED_DATE))
        {
            values.put(MemberTableMetaData.CREATED_DATE, now);
        }

        if (! values.containsKey(MemberTableMetaData.MODIFIED_DATE))
        {
            values.put(MemberTableMetaData.MODIFIED_DATE, now);
        }

        if (! values.containsKey(MemberTableMetaData.MEMBER_NAME))
        {
            throw new SQLException(
                    "Failed to insert row because Member Name is needed " + uri);
        }

        if (! values.containsKey(MemberTableMetaData.MEMBER_ALIAS)) {
            throw new SQLException(
                    "Failed to insert row because Member Alias is needed " + uri);
        }
        if (! values.containsKey(MemberTableMetaData.MEMBER_SEX)) {
            throw new SQLException(
                    "Failed to insert row because Member Sex is needed " + uri);
        }
        if (! values.containsKey(MemberTableMetaData.MEMBER_AGE)) {
            throw new SQLException(
                    "Failed to insert row because Member Age is needed " + uri);
        }


        if (! values.containsKey(MemberTableMetaData.MEMBER_PHOTO)) {
           throw new SQLException(
                    "Failed to insert row because Member Photo is needed " + uri);
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(MemberTableMetaData.TABLE_NAME,
                MemberTableMetaData.MEMBER_NAME, values);
        if (rowId > 0) {
            Uri insertedMemberUri =
                    ContentUris.withAppendedId(
                            MemberTableMetaData.CONTENT_URI, rowId);
            getContext()
                    .getContentResolver()
                    .notifyChange(insertedMemberUri, null);

            db.close();
            return insertedMemberUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MEMBER_COLLECTION_URI_INDICATOR:
                count = db.delete(MemberTableMetaData.TABLE_NAME,
                        where, whereArgs);
                break;

            case INCOMING_SINGLE_MEMBER_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(MemberTableMetaData.TABLE_NAME,
                        MemberTableMetaData._ID + "=" + rowId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String where, String[] whereArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MEMBER_COLLECTION_URI_INDICATOR:
                count = db.update(MemberTableMetaData.TABLE_NAME,
                        values, where, whereArgs);
                break;

            case INCOMING_SINGLE_MEMBER_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(MemberTableMetaData.TABLE_NAME,
                        values, MemberTableMetaData._ID + "=" + rowId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return count;
    }
}
