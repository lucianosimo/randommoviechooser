package com.lucianosimo.randommoviechooser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lucianosimo.randommoviechooser.helper.DBHelper;
import com.lucianosimo.randommoviechooser.helper.FuncHelper;
import com.lucianosimo.randommoviechooser.helper.Movie;
import com.lucianosimo.randommoviechooser.model.ResponseMovie;
import com.lucianosimo.topmoviepicker.R;

public class RandomMovies extends Activity{
	
	private static final int NUMBER_OF_MOVIES = 5;
	private static final int MIN = 1;
	private static final int MAX = 250;
	
	String url;
	private DBHelper db;
	private Movie[] movieOriginal = new Movie[NUMBER_OF_MOVIES];
	private String movie;
	private Random random = new Random();
	private int[] index = new int[NUMBER_OF_MOVIES];
	private final Context context = this;
	private ProgressDialog progressDialog = null;
    private boolean doubleBackToExitPressedOnce = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.randommovies);
	    
		db = new DBHelper(this.getApplicationContext());
		db.open();
		
		TextView swipeLeftBox = (TextView) findViewById(R.id.swipeLeftBox);
		final Intent intent = new Intent(this,SearchMovies.class);
		
		swipeLeftBox.setOnTouchListener(new OnSwipeTouchListener(){
			public void onSwipeLeft(){
				startActivity(intent);
			}
		});
		
		if (FuncHelper.isOnline(context)) {
		    
			this.progressDialog = ProgressDialog.show(this,"Please wait...","Sorting movies and retrieving info",true,false);
			new DownloadTask().execute("Start Download");
			
			ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
			refreshButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final ProgressDialog dialog = ProgressDialog.show(RandomMovies.this, "Please wait...", "Sorting movies and retrieving info", true);
					
					new AsyncTask<Void, Void, Void>() {
						
				        @Override
				        protected Void doInBackground(Void... params) {
				            sortMovies();
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
	
	
	
	private class DownloadTask extends AsyncTask<String, Void, Object> {

		@Override
		protected Object doInBackground(String... params) {
			sortMovies();
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			if (RandomMovies.this.progressDialog != null) {
	            RandomMovies.this.progressDialog.dismiss();
			}
		}
		
	}
	
	public void sortMovies() {
		
		final TextView[] movies = new TextView[NUMBER_OF_MOVIES];
		final TextView[] genres = new TextView[NUMBER_OF_MOVIES];
		final TextView[] runtimes = new TextView[NUMBER_OF_MOVIES];
		final RelativeLayout[] parent = new RelativeLayout[NUMBER_OF_MOVIES];
		final ImageView[] poster = new ImageView[NUMBER_OF_MOVIES];
		int[] movie_id = new int[NUMBER_OF_MOVIES];
		int[] genre_id = new int[NUMBER_OF_MOVIES];
		int[] runtime_id = new int[NUMBER_OF_MOVIES];
		int[] parent_id = new int[NUMBER_OF_MOVIES];
		int[] poster_id = new int[NUMBER_OF_MOVIES];
		String r_movie;
		String r_genre;
		String r_runtime;
		String r_parent;
		String r_poster;
		
		for (int i = 0; i < NUMBER_OF_MOVIES; i++) {
			
			final int aux_i = i;
			index[i]= 0;
			r_movie = "movie" + i;
			r_genre = "genre" + i;
			r_runtime = "runtime" + i;
			r_parent = "parentMovie" + i;
			r_poster = "poster" + i;
			
			movie_id[i] = getResources().getIdentifier(r_movie, "id", "com.lucianosimo.topmoviepicker");
			genre_id[i] = getResources().getIdentifier(r_genre, "id", "com.lucianosimo.topmoviepicker");
			runtime_id[i] = getResources().getIdentifier(r_runtime, "id", "com.lucianosimo.topmoviepicker");
			parent_id[i] = getResources().getIdentifier(r_parent, "id", "com.lucianosimo.topmoviepicker");
			poster_id[i] = getResources().getIdentifier(r_poster, "id", "com.lucianosimo.topmoviepicker");
			
			movies[i] = (TextView) findViewById(movie_id[i]);
			genres[i] = (TextView) findViewById(genre_id[i]);
			runtimes[i] = (TextView) findViewById(runtime_id[i]);
			parent[i] = (RelativeLayout) findViewById(parent_id[i]);
			poster[i] = (ImageView) findViewById(poster_id[i]);
			
			movieOriginal[i] = new Movie();
			index[i] = random.nextInt(MAX - MIN + 1) + MIN;
			movieOriginal[i] = db.getMovie(index[i]);	
			movie = movieOriginal[i].getTitle().replace(" ", "%20");
			url = "http://www.omdbapi.com/?t=" + movie;
			
			Gson gson = new Gson();
			InputStream source = FuncHelper.retrieveStream(url);
			Reader reader = new InputStreamReader(source);
			final ResponseMovie responseMovie = gson.fromJson(reader, ResponseMovie.class);
			final Drawable imageFromUrl = FuncHelper.loadImageFromUrl(responseMovie.getPoster());
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					movies[aux_i].setText(responseMovie.getTitle() + " (" + responseMovie.getYear() + ")");
					genres[aux_i].setText(responseMovie.getGenre());
					runtimes[aux_i].setText(responseMovie.getRuntime());
					poster[aux_i].setBackgroundDrawable(imageFromUrl);
					
					parent[aux_i].setOnClickListener(new OnClickListener() {

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
	
}
