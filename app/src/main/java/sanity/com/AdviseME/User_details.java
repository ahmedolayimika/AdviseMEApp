package sanity.com.AdviseME;

public class User_details {
    private String first_name , last_name, address, age, phone, profile_Image, email, sex, uID, onlineStatus,
            typingTo, iD, level, status;

    public User_details() {
    }

    public User_details(String First_name, String Last_name, String Address, String Age, String Phone,
                        String Profile_Image, String Email, String Sex, String UID, String OnlineStatus,
                        String TypingTo, String ID, String Level, String Status) {
        first_name  = First_name;
        last_name = Last_name;
        address  = Address;
        age = Age ;
        phone = Phone;
        profile_Image = Profile_Image;
        email = Email;
        sex = Sex;
        uID = UID;
        onlineStatus = OnlineStatus;
        typingTo = TypingTo;
        iD = ID;
        level = Level;
        status = Status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile_Image() {
        return profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        this.profile_Image = profile_Image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
