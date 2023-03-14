package sanity.com.AdviseME;

public class post_comments {
    private String User, User_Image, Comment, Comment_time,User_ID,Edited_time;

    public post_comments() {
    }

    public post_comments(String user, String user_Image, String comment, String comment_time, String user_ID, String edited_time) {
        User = user;
        User_Image = user_Image;
        Comment = comment;
        Comment_time = comment_time;
        User_ID = user_ID;
        Edited_time = edited_time;
    }

    public String getEdited_time() {
        return Edited_time;
    }

    public void setEdited_time(String edited_time) {
        Edited_time = edited_time;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getUser_Image() {
        return User_Image;
    }

    public void setUser_Image(String user_Image) {
        User_Image = user_Image;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getComment_time() {
        return Comment_time;
    }

    public void setComment_time(String comment_time) {
        Comment_time = comment_time;
    }
}
