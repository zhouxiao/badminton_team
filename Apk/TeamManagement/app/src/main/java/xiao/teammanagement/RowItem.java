package xiao.teammanagement;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Xiao on 2015/5/11.
 */
public class RowItem {
    private int id;
    private int imageId;
    private String name;
    private String alias;
    private int age;
    private int sex;
    private Bitmap bitmap;

    private Date created;
    private Date modified;
    private String photo;   // url for photo saved on Server

    private int resourceLocation; // assign resource location 1 - local memory array 2 -- local sqlite db 3 -- remote server mysql


    // Constructor for source from local array source -- USING_LOCAL_ARRAY_RESOURCE
    public RowItem(int imageId, String name, String alias){
        this.imageId = imageId;
        this.name = name;
        this.alias = alias;
        age = 0;
        sex = 0;
        bitmap = null;

        resourceLocation = DataSet.USING_LOCAL_ARRAY_RESOURCE;
    }

    // Constructor for source from local sqlite database -- USING_LOCAL_SQLITE_RESOURCE
    public RowItem(int id, String name, String alias, int sex, int age, Bitmap bitmap){
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.sex = sex;
        this.age = age;
        this.bitmap = bitmap;

        resourceLocation = DataSet.USING_LOCAL_SQLITE_RESOURCE;
    }

    // Constructor for source from remote mysql database -- USING_REMOTE_SERVER_RESOURCE
    public RowItem(int id, String name, String alias, int age, int sex, Date created, Date modified, String photo){
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.sex = sex;
        this.age = age;
        this.created = created;
        this.modified = modified;
        this.photo = photo;

        resourceLocation = DataSet.USING_REMOTE_SERVER_RESOURCE;
    }

    public int getImageId(){
        return imageId;
    }

    public void setImageId(int imageId){
        this.imageId = imageId;
    }


    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name + "\n" + alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(int resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
