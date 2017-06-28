//Sqlite.java
//Created by sealy on 2012-12-01.  
//Copyright 2012 Sealy, Inc. All rights reserved.

package inroids.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Sqlite manage
 * @author Sealy
 */
public abstract class Sqlite {
	private static final String sTag="IRLibrary";
	/**
	 * execute SQL
	 * @param context using a context for this function.
	 * @param sSQL An SQL statement. 
	 * @param sDatabase The string of database path. 
	 * @return Whether the operation succeeded.
	 */
    public static boolean execSql(Context context,String sSQL,String sDatabase){
    	try {
	    	SQLiteDatabase db = context.openOrCreateDatabase(sDatabase, Context.MODE_PRIVATE, null);
	    	db.execSQL(sSQL);
	    	db.close();
	    	return true;
    	}catch (Exception e) {
    		MyLog.e(sTag, "Sqlite.execSql:"+e.toString());
	    }
    	return false;
    }
}
