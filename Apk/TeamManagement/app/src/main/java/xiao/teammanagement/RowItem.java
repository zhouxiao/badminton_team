package xiao.teammanagement;

import android.graphics.Bitmap;

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

    public RowItem(int imageId, String name, String alias){
        this.imageId = imageId;
        this.name = name;
        this.alias = alias;
        age = 0;
        sex = 0;
        bitmap = null;
    }

    public RowItem(int id, String name, String alias, int sex, int age, Bitmap bitmap){
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.sex = sex;
        this.age = age;
        this.bitmap = bitmap;
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
}
