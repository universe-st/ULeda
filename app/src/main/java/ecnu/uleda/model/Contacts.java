package ecnu.uleda.model;

/**
 * Created by zhaoning on 2017/5/2.
 */

public class Contacts {
    private String name;
    private int imageId;

    public Contacts(String name,int imageId){
        this.name=name;
        this.imageId=imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }
}
