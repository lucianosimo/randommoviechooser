package com.lucianosimo.randommoviechooser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lucianosimo.randommoviechooser.helper.FuncHelper;
import com.lucianosimo.randommoviechooser.model.ResponseMovie;
import com.lucianosimo.topmoviepicker.R;

public class SearchMovies extends Activity{
	
	private boolean doubleBackToExitPressedOnce = false;
	String url;
	String movie;
	private final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchmovies);
		
		TextView swipeRightBox = (TextView) findViewById(R.id.swipeRightBox);
		final Intent intent = new Intent(this,RandomMovies.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setClassName(this,"com.lucianosimo.randommoviechooser.RandomMovies");
		
		swipeRightBox.setOnTouchListener(new OnSwipeTouchListener(){
			public void onSwipeRight(){
				startActivity(intent);
			}
		});
		
		if (FuncHelper.isOnline(context)) {
			
			ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
			
			searchButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					final ProgressDialog dialog = ProgressDialog.show(SearchMovies.this, "Please wait...", "Sorting movies and retrieving info", true);
					
					new AsyncTask<Void, Void, Void>() {
						
				        @Override
				        protected Void doInBackground(Void... params) {
				            searchMovie();
				            return null;
				        }

				        @Override
				        protected void onPostExecute(Void result) {
				            dialog.dismiss();
				        }
				    }.execute();
					
				}
			});
		} else {
			
			Toast toast = Toast.makeText(getApplicationContext(), "You don't have internet access in this moment. Try again later", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	public void searchMovie() {
		
		EditText searchText = (EditText) findViewById(R.id.searchMovieBox);
		String searchMovie = null;
		
		searchMovie = searchText.getText().toString();
		movie = searchMovie.replace(" ", "%20");
		url = "http://www.omdbapi.com/?s=" + movie;
		InputStream source = FuncHelper.retrieveStream(url);
		Reader reader = new InputStreamReader(source);
		
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
		JsonArray jsonArray = jsonObject.getAsJsonArray("Search");
		
		final ArrayList<String> imdbList = new ArrayList<String>();
		if (jsonArray != null) { 
			for (int i = 0;i < jsonArray.size(); i++){ 
				String string = null;
				string = jsonArray.get(i).toString();
				string = "[" + string + "]";
				Type listType = new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType();
				ArrayList<Map<String,String>> arrayList = gson.fromJson(string, listType);
				for (Map<String,String> s : arrayList) {
					if (s.get("Type").equals("movie")) {
						String aux = null;
						aux = s.get("imdbID");
						imdbList.add(aux);
					}					
			    }
			} 
		}
		
		final LinearLayout resultParent = (LinearLayout) findViewById(R.id.resultMovieParent);
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (imdbList.size() == 0) {
					Toast toast = Toast.makeText(getApplicationContext(), "No movies matched!!", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				resultParent.removeAllViews();
			}
		});
		
		final float scale = context.getResources().getDisplayMetrics().density;
		final int posterSizepixelsWidth = (int) (48 * scale + 0.5f);
		final int posterSizepixelsHeight = (int) (68 * scale + 0.5f);
		final int marginSizePixels = (int) (8 * scale + 0.5f);
		final int paddingSizePixels = (int) (8 * scale + 0.5f);
		final int separator_id = 100;
		
		for (int i = 0; i < imdbList.size(); i++) {
			
			final int title_id = i + 1;
			url = "http://www.omdbapi.com/?i=" + imdbList.get(i);
			InputStream sourceAux = FuncHelper.retrieveStream(url);
			Reader readerAux = new InputStreamReader(sourceAux);
			
			final ResponseMovie responseMovie = gson.fromJson(readerAux, ResponseMovie.class);
			final RelativeLayout relativeMovie = new RelativeLayout(context);
			final TextView textViewTitle = new TextView(context);
	        final TextView textViewGenre = new TextView(context);
	        final TextView textViewRuntime = new TextView(context);
	        final FrameLayout separator = new FrameLayout(context);
	        final Drawable imageFromUrl = FuncHelper.loadImageFromUrl(responseMovie.getPoster());
	        final ImageView poster = new ImageView(context);
	        
	        separator.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,1));
	        separator.setBackgroundColor(getResources().getColor(R.color.black));
	        separator.setPadding(0, paddingSizePixels, 0, paddingSizePixels);
	        separator.setId(separator_id);
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					int genre_id = title_id + 10;
					int runtime_id = title_id + 20;
					int poster_id = title_id + 30;
					
					textViewTitle.setId(title_id);
					textViewGenre.setId(genre_id);
					textViewRuntime.setId(runtime_id);
					poster.setId(poster_id);
					
					RelativeLayout.LayoutParams relativeMovieParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT);
					RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT);
					RelativeLayout.LayoutParams genreParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT);
					RelativeLayout.LayoutParams runtimeParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT);
					RelativeLayout.LayoutParams posterParams = new RelativeLayout.LayoutParams(posterSizepixelsWidth, posterSizepixelsHeight);
					
					posterParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					posterParams.setMargins(0, marginSizePixels, 0, marginSizePixels);
					
					genreParams.addRule(RelativeLayout.BELOW,title_id);
					runtimeParams.addRule(RelativeLayout.BELOW,genre_id);
					
			        relativeMovie.setLayoutParams(relativeMovieParams);
			        textViewTitle.setLayoutParams(titleParams);
			        textViewGenre.setLayoutParams(genreParams);
			        textViewRuntime.setLayoutParams(runtimeParams);
			        poster.setLayoutParams(posterParams);
			        
			        textViewTitle.setTextColor(getResources().getColor(R.color.black));
			        textViewTitle.setText(responseMovie.getTitle() + " (" + responseMovie.getYear() + ")");		        
			        textViewGenre.setTextColor(getResources().getColor(R.color.black));
			        textViewGenre.setText(responseMovie.getGenre());
			        textViewRuntime.setTextColor(getResources().getColor(R.color.black));
			        textViewRuntime.setText(responseMovie.getRuntime());
			        poster.setBackgroundDrawable(imageFromUrl);
			        
			        relativeMovie.addView(textViewTitle);
			        relativeMovie.addView(textViewGenre);
			        relativeMovie.addView(textViewRuntime);
			        relativeMovie.addView(poster);
			        relativeMovie.addView(separator);
			        resultParent.addView(relativeMovie);
			        
			        relativeMovie.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							final Dialog dialog = new Dialog(context);
							dialog.setContentView(R.layout.dialog);
							dialog.setTitle(responseMovie.getTitle() + "(" + responseMovie.getYear() + ")");
							final Drawable imageFromUrl = FuncHelper.loadImageFromUrl(responseMovie.getPoster());
							
							ImageView selMoviePoster = (ImageView) dialog.findViewById(R.id.selMoviePoster);
							TextView selMovieActors = (TextView) dialog.findViewById(R.id.selMovieActors);
							TextView selMovieDirector = (TextView) dialog.findViewById(R.id.selMovieDirector);
							TextView selMovieRuntime = (TextView) dialog.findViewById(R.id.selMovieRuntime);
							TextView selMovieGenre = (TextView) dialog.findViewById(R.id.selMovieGenre);
							TextView selMovieRated = (TextView) dialog.findViewById(R.id.selMovieRated);
							TextView selMoviePlot = (TextView) dialog.findViewById(R.id.selMoviePlot);
							RatingBar selMovieRating = (RatingBar) dialog.findViewById(R.id.selMovieRating);
							
							selMoviePoster.setBackgroundDrawable(imageFromUrl);
							selMovieActors.setText("Actors: " + responseMovie.getActors());
							selMovieDirector.setText("Director: " + responseMovie.getDirector());
							selMovieRuntime.setText("Duration: " + responseMovie.getRuntime());
							selMovieGenre.setText("Genre: " + responseMovie.getGenre());
							selMovieRated.setText("Rated: " + responseMovie.getRated());
							selMoviePlot.setText("Plot: " + responseMovie.getPlot());
							selMovieRating.setRating((float)responseMovie.getImdbRating());
							
							Button buttonDialog = (Button) dialog.findViewById(R.id.buttonDialog);
							buttonDialog.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									dialog.dismiss();
									
								}
							});
							
							dialog.show();
						}
					});
				}
			});
		}
	}
	
	@Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
        	Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to minimize", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
             doubleBackToExitPressedOnce=false;   

            }
        }, 2000);
    }
	
}
