package xiao.teammanagement;

import java.util.List;

/**
 * Created by Xiao on 2015/5/11.
 */
public class DataSet {

    public final static int USING_LOCAL_ARRAY_RESOURCE =   0x001;
    public final static int USING_LOCAL_SQLITE_RESOURCE =  0x002;
    public final static int USING_REMOTE_SERVER_RESOURCE = 0x003;

    public static String SERVER = "www.amituofo.cc";
    //public static String SERVER = "192.168.1.104";  // for local debug

    public static String SITE_URL = "http://" + SERVER + "/index.php/Team/";
    //public static String SITE_URL = "http://" + SERVER + "/Xiao/Team/"; // for local debug

    public static String ACTION_FETCHALL_URL = SITE_URL + "fetchall";
    public static String ACTION_GET_LATEST_TIMESTAMP_URL = SITE_URL + "getLastUpdatedTimeStamp";
    public static String ACTION_UPLOAD_MATCH_RESULT = SITE_URL + "upLoadMatchResult";
    public static String ACTION_ADD_URL = SITE_URL + "add";
    public static String ACTION_DELETE_URL = SITE_URL + "delete";
    public static String ACTION_UPDATE_URL = SITE_URL + "update";
    public static String ACTION_DOWNLOAD_DATA_URL = SITE_URL + "downloadData";
    public static String ACTION_FETCH_PHOTO_URL = SITE_URL + "fetchPhoto";

    public static String SERVER_IMAGE_PATH = "http://" + SERVER + "/Public/img/team/";
    public static String FIELD_SEPERATOR_DASH = "--";
    public static String FIELD_SEPERATOR_COMMA = ",";

    public static boolean USING_CLOUD_DATA = true; //Control using local sqlite db or remote mysql db

    public static String PREFERENCE_SCORE_DB = "score"; // shared preference db name for match result
    public static String PREFERENCE_TEAM_DB = "team";  // shared preference db name for member info
    public static String PREFERENCE_TEAM_PHOTO_DB = "team_photo"; // shared preference for member photo binary
    public static String PREFERENCE_SYNC_UP_TIMESTAMP = "syncup"; // shared preference to store sync up timestamp


    public static final String[] names = new String[] {
            "白黠",
            "波波",
            "陈昱伟",
            "钻石",
            "李辉军",
            "龙明宣",
            "罗剑民",
            "马军龙",
            "梅小鱼",
            "陌陌",
            "司思",
            "孙晓光",
            "王欣",
            "周霄",
            "小草",
            "谢军",
            "尹衍锋",
            "平凡",
    };
    public static final String[] descriptions = new String[] {
            "BX-白黠",
            "BB-波波",
            "YW-陈昱伟",
            "ZS-钻石",
            "HJ-李辉军",
            "MX-龙明宣",
            "JM-罗剑民",
            "LM-马军龙",
            "XY-梅小鱼",
            "MM-陌陌",
            "SS-司思",
            "XG-孙晓光",
            "WX-王欣",
            "ZX-周霄",
            "XC-小草",
            "XJ-谢军",
            "YF-尹衍锋",
            "PF-平凡",
    };

    public static final Integer[] images = {
            R.drawable.bai,
            R.drawable.bobo,
            R.drawable.cyw,
            R.drawable.diamond,
            R.drawable.li,
            R.drawable.laolong,
            R.drawable.luo,
            R.drawable.ma,
            R.drawable.mei,
            R.drawable.mo,
            R.drawable.sisi,
            R.drawable.sun,
            R.drawable.wang,
            R.drawable.xiao,
            R.drawable.xiaocao,
            R.drawable.xie,
            R.drawable.yin,
            R.drawable.pinfan
    };

    public static List<RowItem> rowItems;


}
