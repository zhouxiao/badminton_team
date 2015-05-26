package xiao.teammanagement;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Xiao on 2015/5/15.
 */
public class TeamProviderMetaData {
    public static final String AUTHORITY = "xiao.provider.TeamProvider";

    public static final String DATABASE_NAME = "team.db";
    public static final int DATABASE_VERSION = 2;
    public static final String MEMBERS_TABLE_NAME = "members";

    private TeamProviderMetaData(){}

    //inner class describing columns and their types
    public static final class MemberTableMetaData implements BaseColumns{
        private MemberTableMetaData(){}
        public static final String TABLE_NAME = "members";

        // uri and mime type definition
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/members");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xiao.member";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xiao.member";

        public static final String DEFAULT_SORT_ORDER = "alias ASC";

        //Additional Columns start here.

        public static final String MEMBER_NAME = "name";    // Text
        public static final String MEMBER_ALIAS = "alias"; // Text
        public static final String  MEMBER_AGE = "age"; // Integer 0 - Female 1 - Male
        public static final String MEMBER_SEX = "sex"; // Integer
        //Integer from System.currentTimeMillis()
        public static final String CREATED_DATE = "created";
        //Integer from System.currentTimeMillis()
        public static final String MODIFIED_DATE = "modified";
        // Photo
        public static final String MEMBER_PHOTO = "photo";
    }
}
