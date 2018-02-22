package com.webdefault.androiduniversalchardet;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.mozilla.universalchardet.UniversalDetector;

public class MainActivity extends AppCompatActivity
{
	private static final String LOG_TAG = "MainActivity";
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		AssetManager am = getAssets();
		
		try
		{
			InputStream is = am.open( "utf8.txt" );
			String charset = detectCharset( is );
			Log.v( LOG_TAG, "Charset: " + charset );
			
			InputStreamReader in = new InputStreamReader( is, Charset.forName( charset ) );
			
			StringBuilder stringBuilder = new StringBuilder();
			char[] buff = new char[500];
			for( int charsRead; ( charsRead = in.read( buff ) ) != -1; )
			{
				stringBuilder.append( buff, 0, charsRead );
			}
			
			in.close();
			String value = stringBuilder.toString();
			
			TextView textView = (TextView) findViewById( R.id.textview );
			textView.setText( value );
			textView.setMovementMethod(new ScrollingMovementMethod());
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	UniversalDetector detector = new UniversalDetector( null );
	
	private String detectCharset( InputStream fileInputStream )
	{
		String encoding = null;
		
		try
		{
			byte[] buf = new byte[4096];
			int nread;
			while( ( nread = fileInputStream.read( buf ) ) > 0 && !detector.isDone() )
			{
				detector.handleData( buf, 0, nread );
			}
			// (3)
			detector.dataEnd();
			
			// (4)
			encoding = detector.getDetectedCharset();
			
			// (5)
			detector.reset();
			fileInputStream.reset();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		
		return encoding;
	}
}
