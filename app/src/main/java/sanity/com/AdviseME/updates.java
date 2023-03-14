package sanity.com.AdviseME;

public class updates {

    private String Title, Description, Image, Author, Author_Email,Date_and_Time, Author_ID, New_Date_and_Time, Recipients;

    public updates() {

    }

    public updates(String title, String description, String image, String author, String author_Email, String date_and_Time, String author_ID, String new_Date_and_Time, String recipients) {
        Title = title;
        Description = description;
        Image = image;
        Author = author;
        Author_Email = author_Email;
        Date_and_Time = date_and_Time;
        Author_ID = author_ID;
        New_Date_and_Time = new_Date_and_Time;
        Recipients = recipients;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getAuthor_Email() {
        return Author_Email;
    }

    public void setAuthor_Email(String author_Email) {
        Author_Email = author_Email;
    }

    public String getDate_and_Time() {
        return Date_and_Time;
    }

    public void setDate_and_Time(String date_and_Time) {
        Date_and_Time = date_and_Time;
    }

    public String getAuthor_ID() {
        return Author_ID;
    }

    public void setAuthor_ID(String author_ID) {
        Author_ID = author_ID;
    }

    public String getNew_Date_and_Time() {
        return New_Date_and_Time;
    }

    public void setNew_Date_and_Time(String new_Date_and_Time) {
        New_Date_and_Time = new_Date_and_Time;
    }

    public String getRecipients() {
        return Recipients;
    }

    public void setRecipients(String recipients) {
        Recipients = recipients;
    }
}
