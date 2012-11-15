package net.wisedog.android.whooing.db;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountsEntity implements Parcelable{
    public String accountType = null;
    public String account_id = null;
    public String type = null;
    public String title = null;
    public String memo = null;
    public int open_date = 0;
    public int close_date = 0;
    public String category = null;
    
    //only for liabilities
    public String opt_use_date = null;
    public int opt_pay_date = 0;
    public String opt_pay_account_id = null;
    
    public AccountsEntity(String account_type, JSONObject jsonObj) throws org.json.JSONException{
        if(jsonObj == null || account_type == null){
            return;
        }
        accountType = account_type;
        account_id = jsonObj.getString("account_id");
        type = jsonObj.getString("type");
        title = jsonObj.getString("title");
        memo = jsonObj.getString("memo");
        open_date = jsonObj.getInt("open_date");
        close_date = jsonObj.getInt("close_date");
        category = jsonObj.getString("category");


        if(account_type.equals("liabilities")){
            opt_use_date = jsonObj.getString("opt_use_date");
            opt_pay_account_id = jsonObj.getString("opt_pay_account_id");
            opt_pay_date = jsonObj.optInt("opt_pay_date", 0);
        }
    }
    
    public AccountsEntity(Parcel in){
        readFromParcel(in);
    }
    
    /**
     * 
     */
    public AccountsEntity() {
        ;//Do nothing
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountType);
        dest.writeString(account_id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(memo);
        dest.writeInt(open_date);
        dest.writeInt(close_date);
        dest.writeString(category);
        if(accountType.equals("liabilities")){
            dest.writeString(opt_use_date);
            dest.writeString(opt_pay_account_id);
            dest.writeInt(opt_pay_date);
        }
    }
    
    public void readFromParcel(Parcel in){
        accountType = in.readString();
        account_id = in.readString();
        type = in.readString();
        title = in.readString();
        memo = in.readString();
        open_date = in.readInt();
        close_date = in.readInt();
        category = in.readString();
        if(accountType.equals("liabilities")){
            opt_use_date = in.readString();
            opt_pay_account_id = in.readString();
            opt_pay_date = in.readInt();
        }else{
            opt_use_date = null;
            opt_pay_date = 0;
            opt_pay_account_id = null;
        }
        
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    public int describeContents() {
        // do nothing
        return 0;
    }
    

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public AccountsEntity createFromParcel(Parcel in) {
                 return new AccountsEntity(in);
           }

           public AccountsEntity[] newArray(int size) {
                return new AccountsEntity[size];
           }
       };

   
}
