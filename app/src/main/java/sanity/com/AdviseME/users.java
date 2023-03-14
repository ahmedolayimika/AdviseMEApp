package sanity.com.AdviseME;

public class users {
    private String First_name, Last_name, Address, Age, Phone, Profile_Image, Email, Sex, UID, OnlineStatus, TypingTo, ID, Level, Status;

    public users() {
    }

    public users(String first_name, String last_name, String address, String age, String phone, String profile_Image, String email, String sex, String uID, String onlineStatus, String typingTo, String ID, String level, String status) {
        First_name = first_name;
        Last_name = last_name;
        Address = address;
        Age = age;
        Phone = phone;
        Profile_Image = profile_Image;
        Email = email;
        Sex = sex;
        this.UID = uID;
        OnlineStatus = onlineStatus;
        TypingTo = typingTo;
        this.ID = ID;
        Level = level;
        Status = status;
    }

    public String getFirst_name() {
        return First_name;
    }

    public void setFirst_name(String first_name) {
        First_name = first_name;
    }

    public String getLast_name() {
        return Last_name;
    }

    public void setLast_name(String last_name) {
        Last_name = last_name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        Profile_Image = profile_Image;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return TypingTo;
    }

    public void setTypingTo(String typingTo) {
        TypingTo = typingTo;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}