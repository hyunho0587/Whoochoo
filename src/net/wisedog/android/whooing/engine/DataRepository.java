/**
 * 
 */
package net.wisedog.android.whooing.engine;

import net.wisedog.android.whooing.Define;
import net.wisedog.android.whooing.activity.MainFragmentActivity;
import net.wisedog.android.whooing.network.ThreadRestAPI;
import net.wisedog.android.whooing.utils.WhooingCalendar;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * FragmentPager에서 Fragment가 DestroyView되기때문에 값을 가지고 있다. 
 * @author Wisedog(me@wisedog.net)
 */
public class DataRepository{
    static public final int BS_MODE = 0;
    static public final int PL_MODE = 1;
    static public final int MOUNTAIN_MODE = 2;
    static public final int EXP_BUDGET_MODE = 3;
    static public final int USER_MODE = 4;
    static public final int LATEST_TRANSACTION = 5;
    public static final int ACCOUNT_MODE = 6;
    public static final int DASHBOARD_MODE = 7;
    
    /**자산부채 - bs*/
    private JSONObject mBsValue = null;
    /**비용수익 - pl*/
    private JSONObject mPlValue = null;
    /**Mountain Value*/
    private JSONObject mMtValue = null;
    /** Expenses Budget Value */
    private JSONObject mExpBudgetValue = null;
    
    /** User Information value*/
    private JSONObject mUserValue = null;
    /** Frequent item info*/
    private static JSONObject mLastestItem = null;    
    
    private onLoadingMessage mLoadingMsgListener = null;
	private Context mContext;
	
	private int mRestApiNum = -1;
    
    // 대쉬보드 - Asset/Doubt: bs,pl / Monthly Budget : pl/ Credit Card : bs 
    
    public interface DataChangeListener{
        ;
    }
    
    public static interface onLoadingMessage{
        public void onMessage(int message);
    }
        
    /**
     * Refresh account infomation from server
     * @param		context		Context that is needed for accessing database
     * */
    public void refreshAccount(Context context) {
    	mContext = context;
    	ThreadRestAPI thread = new ThreadRestAPI(mHandler,
                Define.API_GET_ACCOUNTS);
        thread.start();
	}
        
	public void refreshUserInfo(Context context) {
		mContext = context;
		ThreadRestAPI thread = new ThreadRestAPI(mHandler, Define.API_GET_USER_INFO);
		thread.start();
	}
	
	public void refreshLastestItems(Context context){
        ThreadRestAPI thread = new ThreadRestAPI(mHandler, Define.API_GET_ENTRIES_LATEST_ITEMS);
        thread.start();
	}
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Define.MSG_API_OK) {
                JSONObject obj = (JSONObject)msg.obj;
                if(obj != null){
                    int returnCode = 200;
                    try{
                        mRestApiNum = obj.getInt("rest_of_api");
                        setRestApi(mContext, mRestApiNum);
                        returnCode = obj.getInt("code");
                    }catch(JSONException e){
                        setRestApi(mContext, 0);
                    }
                    
                    if(setCodeHandling(returnCode) == false){
                        return;
                    }
                }
                
                if (msg.arg1 == Define.API_GET_ACCOUNTS) {
                    if (mContext != null) {
                        GeneralProcessor general = new GeneralProcessor(mContext);
                        try {
                            JSONObject objResult = obj.getJSONObject("results");
                            general.fillAccountsTable(objResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    if (mLoadingMsgListener != null) {
                        mLoadingMsgListener.onMessage(ACCOUNT_MODE);
                    }
                }
                else if(msg.arg1 == Define.API_GET_USER_INFO){
                	if(mContext != null){
                		mUserValue = obj;
                		try {
							JSONObject objResults = obj.getJSONObject("results");
							Define.CURRENCY_CODE = objResults.getString("currency");
							//TODO preference update
						} catch (JSONException e) {
							e.printStackTrace();
							return;
						}
                		
                        if(mLoadingMsgListener != null){
                            mLoadingMsgListener.onMessage(USER_MODE);
                        }
                	}
                }
                else if(msg.arg1 == Define.API_GET_ENTRIES_LATEST_ITEMS){
                    mLastestItem = obj;
                    if(mLoadingMsgListener != null){
                        mLoadingMsgListener.onMessage(LATEST_TRANSACTION);
                    }
                }
            }
        }
    };
    
    protected void setRestApi(Context context, final int apiNum){
        if(context != null){
            TextView textView = (TextView) ((Activity)context).findViewById(MainFragmentActivity.API_MENUITEM_ID);
            if(textView != null){
                if(apiNum == 0){
                    textView.setText("Api\r\n -");
                }else{
                    textView.setText("Api\r\n "+String.valueOf(apiNum));
                }
            }
        }
    }
    
    public void refreshRestApi(Context context){
        setRestApi(context, mRestApiNum);
    }
    
    /** @return	return Rest of API num*/
    public int getRestApi(){
    	return mRestApiNum;
    }
    
    /**
     * @param returnCode
     * @return
     */
    protected boolean setCodeHandling(int returnCode) {
        // TODO Auto-generated method stub
        return true;
    }
 
    /**
     * @return  Return saved mountain value
     * */
    public synchronized JSONObject getMtValue(){
        return mMtValue;
    }
    
    /**
     * @return  Return saved budget value
     * */
    public synchronized JSONObject getExpBudgetValue(){
        return mExpBudgetValue;
    }
    
    /**
     * @return	return Balance info
     * */
    public synchronized JSONObject getBsValue(){
        return mBsValue;
    }
    
    /**
     * @return	return Profit/Loss info
     * */
    public synchronized JSONObject getPlValue(){
        return mPlValue;
    }
    
    /**
     * @return	return user info
     * */
    public synchronized JSONObject getUserValue(){
    	return mUserValue;
    }
    
    /**
     * @return	return lastest items for suggestion account selection in adding transaction
     * */
    public JSONObject getLastestItems(){
        return mLastestItem;
    }
    
    /**
     * Set loading message listener for splash activity
     * @param	listener	Listener to register
     * */
    public void setLoadingMsgListener(onLoadingMessage listener){
        mLoadingMsgListener = listener;
    }

    /**
     * Clear cached data for changing data
     * */
	public void clearCachedData() {
		mPlValue = null;
		mMtValue = null;
		mBsValue = null;
		mExpBudgetValue = null;
	}

    public void refreshMtValue(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("end_date", WhooingCalendar.getTodayYYYYMM());
        bundle.putString("start_date", WhooingCalendar.getPreMonthYYYYMM(6));
        ThreadRestAPI thread = new ThreadRestAPI(mHandler,
                Define.API_GET_MOUNTAIN, bundle);
        thread.start();        
    }
    
    /**
     * Store User information
     * @param	obj		JSONObject to store
     * */
	public synchronized void setUserInfo(JSONObject obj) {
		this.mUserValue = obj;
	}

	/**
	 * Store Profit/Loss value
	 * @param	obj		JSONObject to store
	 * */
	public synchronized void setPLValue(JSONObject obj) {
		this.mPlValue = obj;
	}

	/**
	 * Store Mountain value
	 * @param	obj		JSONObject to store
	 * */
	public synchronized void setMtValue(JSONObject obj) {
		this.mMtValue = obj;
	}

	/**
	 * Store Balance value
	 * @param	obj		JSONObject to store
	 * */
	public synchronized void setBsValue(JSONObject obj) {
		this.mBsValue = obj;
	}

	/**
	 * Store Expenses Budget value
	 * @param	obj		JSONObject to store
	 * */
	public synchronized void setExpBudgetValue(JSONObject obj) {
		this.mExpBudgetValue = obj;
	}
}
