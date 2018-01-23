package com.yalematta.android.bakingproject.entities;

import android.arch.persistence.room.ColumnInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yalematta on 1/5/18.
 */

@Entity(tableName= "Ingredients")
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int ingredientId;

    @SerializedName("ingredient")
    public String ingredientName;

    public double quantity;

    public String measure;

    public Ingredient(int ingredientId, double quantity, String measure, String ingredientName) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredientName = ingredientName;
    }

    public int getIngredientId() { return ingredientId; }

    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ingredientId);
        dest.writeString(this.ingredientName);
        dest.writeDouble(this.quantity);
        dest.writeString(this.measure);
    }

    protected Ingredient(Parcel in) {
        this.ingredientId = in.readInt();
        this.ingredientName = in.readString();
        this.quantity = in.readDouble();
        this.measure = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
