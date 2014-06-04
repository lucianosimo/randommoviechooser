package com.lucianosimo.randommoviechooser.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String DB_PATH;
	private static String DB_NAME = "topmovies.sqlite";
	private SQLiteDatabase top_movies;
	private final Context context;
	public static final String KEY_ID = "_id";
	public static final String KEY_COL1 = "title";
	public static final String KEY_COL2 = "seen";
	private static final String[] columns = new String[] { KEY_ID, KEY_COL1, KEY_COL2 };
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		DB_PATH = context.getDatabasePath(DB_NAME).toString();
	}
	
	public void createDatabase() throws IOException {
		
		if(checkDatabase()){
			
		} else {
			this.getReadableDatabase();
			try {
				copyDatabase();
			} catch (IOException e) {
				throw new Error(e.toString() + "metodo create");
			}
		}
	}
	
	private boolean checkDatabase() {
		SQLiteDatabase checkDB = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			}
		if(checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}
	
	private void copyDatabase() throws IOException{

		InputStream inputStream = context.getAssets().open(DB_NAME);
		 
		String outFileName = DB_PATH + DB_NAME;
		 
		OutputStream outputStream = new FileOutputStream(outFileName);
		 
		byte[] buffer = new byte[1024];
		int length;
		
		while ((length = inputStream.read(buffer))>0){
			outputStream.write(buffer, 0, length);
		}
		 
		outputStream.flush();
		outputStream.close();
		inputStream.close();
		
		}
	
	public void open() throws SQLException{
		 
		try {
			createDatabase();
		} catch (IOException e) {
			throw new Error(e.toString());
		}
		String path = DB_PATH + DB_NAME;
		top_movies = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
	}
		 
		@Override
		public synchronized void close() {
		if(top_movies != null)
		top_movies.close();
		super.close();
		}
		 
		@Override
		public void onCreate(SQLiteDatabase db) {
		}
		 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
		public Movie getMovie (Integer row) {
			Movie movie = new Movie();
			Cursor result = top_movies.query(true, "movies", columns, KEY_ID + "=" + row, null, null, null, null, null);
			if (result.getCount() == 0 || !result.moveToFirst()) {
				movie = new Movie(-1,"No movie",-1);
			} else {
				if (result.moveToFirst()) {
					movie = new Movie(result.getInt(result.getColumnIndex(KEY_ID)),
							result.getString(result.getColumnIndex(KEY_COL1)),
							result.getInt(result.getColumnIndex(KEY_COL2)));
				}
			}
			return movie;
		}
}
