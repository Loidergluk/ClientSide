package acquiring.retail.vladimir.clientside.task;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2017, eosan.ru. All rights reserved.
 * Created by a.feofanov on 17.04.2017.
 */

public class JsonParser {

    public AuthSession parseAuthSession(JsonReader reader) throws IOException {
        reader.beginObject();
        String sessionId = null;
        String clientId = null;
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "sessionId":
                    sessionId = reader.nextString();
                    break;
                case "clientId":
                    clientId = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (sessionId != null) {
            return new AuthSession(sessionId,clientId);
        } else {
            return null;
        }
    }

    public Profile parseProfile(JsonReader reader) throws IOException {
        reader.beginObject();
        String id = null;
        String phoneNumber = null;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        String photoId = null;
        while (reader.hasNext()) {
            String pname = reader.nextName();
            switch (pname) {
                case "id": {
                    id = reader.nextString();
                    break;
                }
                case "phoneNumber": {
                    phoneNumber = reader.nextString();
                    break;
                }
                case "firstName": {
                    firstName = reader.nextString();
                    break;
                }
                case "middleName": {
                    middleName = reader.nextString();
                    break;
                }
                case "lastName": {
                    lastName = reader.nextString();
                    break;
                }
                case "photoId": {
                    photoId = reader.nextString();
                    break;
                }
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        if (id!=null && id.trim().length()>0) {
            Profile p = new Profile(id);
            p.setPhoneNumber(phoneNumber);
            p.setFirstName(firstName);
            p.setMiddleName(middleName);
            p.setLastName(lastName);
            p.setPhotoId(photoId);
            return p;
        }
        return null;
    }

    public AsyncAction parseAsyncAction(JsonReader reader) throws IOException {
        reader.beginObject();
        String id = null;
        while (reader.hasNext()) {
            String pname = reader.nextName();
            switch (pname) {
                case "id": {
                    id = reader.nextString();
                    break;
                }
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        if (id!=null && id.trim().length()>0) {
            AsyncAction a = new AsyncAction(id);
            return a;
        }
        return null;
    }

    public Photo parsePhoto(JsonReader reader) throws IOException {
        reader.beginObject();
        String id = null;
        while (reader.hasNext()) {
            String pname = reader.nextName();
            switch (pname) {
                case "photoId": {
                    id = reader.nextString();
                    break;
                }
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        if (id!=null && id.trim().length()>0) {
            Photo a = new Photo(id);
            return a;
        }
        return null;
    }

    public UploadProfile[] parseUploadProfileArray(JsonReader reader) throws IOException {
        List<UploadProfile> rez = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            UploadProfile up = parseUploadProfile(reader);
            if (up!= null) {
                rez.add(up);
            }
        }
        reader.endArray();
        return rez.toArray(new UploadProfile[rez.size()]);
    }

    public UploadProfile parseUploadProfile(JsonReader reader) throws IOException {
        reader.beginObject();
        String id = null;
        String status = null;
        while (reader.hasNext()) {
            String pname = reader.nextName();
            switch (pname) {
                case "id": {
                    id = reader.nextString();
                    break;
                }
                case "status": {
                    status = reader.nextString();
                    break;
                }
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        if (id!=null && id.trim().length()>0) {
            UploadProfile up = new UploadProfile(id,status);
            return up;
        }
        return null;
    }

    public List<Profile> parseProfileList(JsonReader reader) throws IOException {
        List<Profile> list = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("items".equals(name)) {
                reader.beginArray();
                while (reader.hasNext()) {
                  Profile p = parseProfile(reader);
                  if (p!= null) {
                      list.add(p);
                  }
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return list;
    }
}
