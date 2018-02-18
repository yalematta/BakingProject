package com.yalematta.android.bakingproject.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yalematta on 2/18/18.
 */

public class Attribution implements Parcelable {

    public String libraryName;
    public String githubLink;
    public String copyright;

    public Attribution(String libraryName, String githubLink, String copyright) {
        this.libraryName = libraryName;
        this.githubLink = githubLink;
        this.copyright = copyright;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.libraryName);
        dest.writeString(this.githubLink);
        dest.writeString(this.copyright);
    }

    protected Attribution(Parcel in) {
        this.libraryName = in.readString();
        this.githubLink = in.readString();
        this.copyright = in.readString();
    }

    public static final Parcelable.Creator<Attribution> CREATOR = new Parcelable.Creator<Attribution>() {
        @Override
        public Attribution createFromParcel(Parcel source) {
            return new Attribution(source);
        }

        @Override
        public Attribution[] newArray(int size) {
            return new Attribution[size];
        }
    };
}
