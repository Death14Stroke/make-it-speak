package com.andruid.magic.makeitspeak.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "ttsHistory")
public class AudioText implements Parcelable {
    @PrimaryKey()
    @NonNull
    private String name;
    private String message;
    private long created;

    public AudioText(@NonNull String name, String message, long created) {
        this.name = name;
        this.message = message;
        this.created = created;
    }

    protected AudioText(Parcel in) {
        name = Objects.requireNonNull(in.readString());
        message = in.readString();
        created = in.readLong();
    }

    public static final Creator<AudioText> CREATOR = new Creator<AudioText>() {
        @Override
        public AudioText createFromParcel(Parcel in) {
            return new AudioText(in);
        }

        @Override
        public AudioText[] newArray(int size) {
            return new AudioText[size];
        }
    };

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null || obj.getClass() != AudioText.class)
            return false;
        AudioText a = (AudioText) obj;
        return created == a.created && name.equals(a.name) && message.equals(a.message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(message);
        dest.writeLong(created);
    }
}