package com.example.loyalty;

import java.util.Arrays;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;

public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";
	SharedPreferences prefs;
	SharedPreferences.Editor edit;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.real_mainscreen, container, false);
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	    final Button login = (Button) view.findViewById(R.id.loginButton);
	    final Button signup = (Button) view.findViewById(R.id.signupButton);
	    prefs = getActivity().getSharedPreferences("com.example.loyalty", 0);
	    edit = prefs.edit();
	    String un = null;
	    String pw = null;
	    Handler h = new Handler(){
	    	
			@Override
			public void handleMessage(Message msg) //Handling the different server comms
			{
				if(msg.what == 0 || msg.what == 2)
				{
					Intent verified = new Intent(getActivity(), MainActivity.class);
					startActivity(verified);
					getActivity().finish();
				}
				else
				{
					edit.remove("username"); edit.remove("password"); edit.apply();
				}
			}
	    };
	    if(prefs.contains("username") && prefs.contains("password"))
	    {
	    	un = prefs.getString("username", ""); pw = prefs.getString("password", "");
	    	new LoginScreen.ServCon(LoginScreen.IP, LoginScreen.port, h, "login\n"+un+"\n"+pw).execute();
	    }
	    Log.d("Safesofar", "at pre loginlistener");
	    login.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), LoginScreen.class);
				startActivity(i);
				getActivity().finish();
			}
		});
	    signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), SignupScreen.class);
				startActivity(i);
				getActivity().finish();
			}
		});
	    return view;
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private UiLifecycleHelper uiHelper;
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if(user != null)
					{
						final String username = user.getId();
						final String password = user.getUsername();
						Handler h = new Handler(){
					    	
							@Override
							public void handleMessage(Message msg) //Handling the different server comms
							{
								Log.d(msg.what+"",msg.obj+"");
								if(msg.what == 0 || msg.what == 2)
								{
									Log.d("****", "tryna main act");
									Intent verified = new Intent(getActivity(), MainActivity.class);
									startActivity(verified);
									getActivity().finish();
								}
								else new LoginScreen.ServCon(LoginScreen.IP, LoginScreen.port, this, "login\n"+username+"\n"+password).execute();
							}
					    };
						if(!prefs.getString("username", "").equals(user.getId()) || !prefs.getString("password", "").equals(user.getUsername()))
						{
							edit.putString("username", user.getId()); edit.putString("password", user.getUsername());
							edit.apply();
							new LoginScreen.ServCon(LoginScreen.IP, LoginScreen.port, h, "signup\n"+user.getId()+"\n"+user.getUsername()+"\n\n\n").execute();
						}
						else new LoginScreen.ServCon(LoginScreen.IP, LoginScreen.port, h, "login\n"+username+"\n"+password).execute();
					}
				}
			});
	        Request.executeBatchAsync(request);

	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}

}