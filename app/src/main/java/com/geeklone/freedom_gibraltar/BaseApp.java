package com.geeklone.freedom_gibraltar;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		setOrientation();
	}


	private void setOrientation() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			@Override
			public void onActivityStarted(@NonNull Activity activity) {

			}

			@Override
			public void onActivityResumed(@NonNull Activity activity) {

			}

			@Override
			public void onActivityPaused(@NonNull Activity activity) {

			}

			@Override
			public void onActivityStopped(@NonNull Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(@NonNull Activity activity) {

			}
		});
	}
}
