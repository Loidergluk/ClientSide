package acquiring.retail.vladimir.clientside.task;

import android.os.Bundle;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class Profile {
    private final String id;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String photoId;

    public Profile(String id) {
        this.id = id;
    }

    public Profile(Bundle bundle) {
        this.id = bundle.getString("id");
        this.phoneNumber = bundle.getString("phoneNumber");
        this.lastName = bundle.getString("lastName");
        this.firstName = bundle.getString("firstName");
        this.middleName = bundle.getString("middleName");
        this.photoId = bundle.getString("photoId");
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("lastName",lastName);
        bundle.putString("firstName",firstName);
        bundle.putString("middleName",middleName);
        bundle.putString("photoId",photoId);
        return bundle;
    }
}
