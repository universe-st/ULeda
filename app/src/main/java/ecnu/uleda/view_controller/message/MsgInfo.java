package ecnu.uleda.view_controller.message;

/**
 * Created by zhaoning on 2017/10/15.
 */

public class MsgInfo {
    public String getLeft_text() {
        return left_text;
    }

    public String getRight_text() {
        return right_text;
    }

    private String left_text;
    private String right_text;

    public MsgInfo(String left_text, String right_text) {
        this.left_text = left_text;
        this.right_text = right_text;
    }
    public MsgInfo() {

    }
}
