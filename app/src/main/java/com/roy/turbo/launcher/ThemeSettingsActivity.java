package com.roy.turbo.launcher;

import static com.roy.ext.ActivityKt.chooseLauncher;
import static com.roy.ext.ActivityKt.searchIconPack;
import static com.roy.ext.ContextKt.isDefaultLauncher;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Toast;

import com.roy.ext.ActivityKt;
import com.roy.turbo.launcher.settings.SettingsProvider;
import com.roy.turbo.launcher.ui.AFakeLauncher;

import java.util.List;

public class ThemeSettingsActivity  extends PreferenceActivity implements OnPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(SettingsProvider.SETTINGS_KEY);
		addPreferencesFromResource(R.xml.theme_settings);

		SharedPreferences sp = getPreferenceManager().getSharedPreferences();
		final String themePackage = sp.getString("themePackageName", Launcher.THEME_DEFAULT);
		ListPreference lp = (ListPreference) findPreference("themePackageName");
		lp.setOnPreferenceChangeListener(this);
		Intent intent = new Intent("com.roy.turbo.launcher.THEMES");
		intent.addCategory("android.intent.category.DEFAULT");
		PackageManager pm = getPackageManager();
		List<ResolveInfo> themes = pm.queryIntentActivities(intent, 0);
		String[] entries = new String[themes.size() + 1];
		String[] values = new String[themes.size() + 1];
		entries[0] = Launcher.THEME_DEFAULT;
		values[0] = Launcher.THEME_DEFAULT;
		for (int i = 0; i < themes.size(); i++) {
			String appPackageName = (themes.get(i)).activityInfo.packageName;
			String themeName = (themes.get(i)).loadLabel(pm).toString();
			entries[i + 1] = themeName;
			values[i + 1] = appPackageName;
		}
		lp.setEntries(entries);
		lp.setEntryValues(values);
		ThemePreference themePreview = (ThemePreference) findPreference("themePreview");
		themePreview.setTheme(themePackage);
	}
    
	public void applyTheme(View v) {
		ThemePreference themePreview = (ThemePreference) findPreference("themePreview");
		String packageName = themePreview.getValue().toString();
		SharedPreferences sp = getPreferenceManager().getSharedPreferences();
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("themePackageName", packageName);

		if (!packageName.equals(Launcher.THEME_DEFAULT)) {
			Resources themeResources = null;
			try {
				themeResources = getPackageManager().getResourcesForApplication(packageName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (themeResources != null) {
				int config_dockBgId = themeResources.getIdentifier("config_dockBg", "bool", packageName);
				if (config_dockBgId != 0) {
					boolean config_dockBg = themeResources.getBoolean(config_dockBgId);
					editor.putBoolean("dockBg", config_dockBg);
				}
				int config_new_selectorsId = themeResources.getIdentifier("config_new_selectors", "bool", packageName);
				if (config_new_selectorsId != 0) {
					boolean config_new_selectors = themeResources.getBoolean(config_new_selectorsId);
					editor.putBoolean("uiNewSelectors", config_new_selectors);
				}
				int config_drawerLabelsId = themeResources.getIdentifier("config_drawerLabels", "bool", packageName);
				if (config_drawerLabelsId != 0) {
					boolean config_drawerLabels = themeResources.getBoolean(config_drawerLabelsId);
					editor.putBoolean("drawerLabels", config_drawerLabels);
				}
				int config_fadeDrawerLabelsId = themeResources.getIdentifier("config_fadeDrawerLabels", "bool", packageName);
				if (config_fadeDrawerLabelsId != 0) {
					boolean config_fadeDrawerLabels = themeResources.getBoolean(config_fadeDrawerLabelsId);
					editor.putBoolean("fadeDrawerLabels", config_fadeDrawerLabels);
				}
				int config_page_indicatorId = themeResources.getIdentifier("config_page_indicator", "bool", packageName);
				if (config_page_indicatorId != 0) {
					boolean config_page_indicator = themeResources.getBoolean(config_page_indicatorId);
					editor.putBoolean("pageIndicator", config_page_indicator);
				}
				int config_selectors_colorId = themeResources.getIdentifier("config_selectors_color", "integer", packageName);
				if (config_selectors_colorId != 0) {
					int config_selectors_color = themeResources.getInteger(config_selectors_colorId);
					editor.putInt("selectors_color", config_selectors_color);
				}
				int config_selectors_color_focusId = themeResources.getIdentifier("config_selectors_color_focus", "integer", packageName);
				if (config_selectors_color_focusId != 0) {
					int config_selectors_color_focus = themeResources.getInteger(config_selectors_color_focusId);
					editor.putInt("selectors_color_focus", config_selectors_color_focus);
				}
				int config_drawer_colorId = themeResources.getIdentifier(
						"config_drawer_color", "integer",
						packageName);
				if (config_drawer_colorId != 0) {
					int config_drawer_color = themeResources.getInteger(config_drawer_colorId);
					editor.putInt("drawer_color", config_drawer_color);
				}
				int config_page_indicator_typeId = themeResources.getIdentifier("config_page_indicator_type", "string", packageName);
				if (config_page_indicator_typeId != 0) {
					String config_page_indicator_type = themeResources
							.getString(config_page_indicator_typeId);
					editor.putString("pageIndicatorType",
							config_page_indicator_type);
				}
				int config_ab_scale_factorId = themeResources.getIdentifier(
						"config_ab_scale_factor", "integer",
						packageName);
				if (config_ab_scale_factorId != 0) {
					int config_ab_scale_factor = themeResources
							.getInteger(config_ab_scale_factorId);
					editor.putInt("iconScale", config_ab_scale_factor);
				}
				int dock_styleId = themeResources.getIdentifier(
						"main_dock_style", "string", packageName);
				if (dock_styleId != 0) {
					String dock_style = themeResources.getString(dock_styleId);
					editor.putString("main_dock_style", dock_style);

				}
				// TODO:TURBO We set the theme wallpaper. We should add this as
				// optional...
				int wallpaperId = themeResources.getIdentifier(
						"theme_wallpaper", "drawable", packageName);
				if (wallpaperId != 0) {
					Options mOptions = new BitmapFactory.Options();
					mOptions.inDither = false;
					mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap wallpaper = null;
					try {
						wallpaper = BitmapFactory.decodeResource(themeResources, wallpaperId, mOptions);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					if (wallpaper != null) {
						try {
							WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
							// wpm.setResource(mImages.get(position));
							wpm.setBitmap(wallpaper);
							wallpaper.recycle();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		}

		editor.apply();
		finish();
	}
	
	public void getThemes(View v) {
		searchIconPack(this);
//		Uri marketUri = Uri.parse("market://search?q=Turbo Launcher Theme");
//		Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
//		try {
//			startActivity(marketIntent);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		finish();
	}

	public void setDefaultLauncher(View v) {
		if (isDefaultLauncher(this)) {
			Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
		} else {
			chooseLauncher(this, AFakeLauncher.class);
		}
	}

	public void rateApp(View v) {
		ActivityKt.rateApp(this, getPackageName());
	}

	public void moreApp(View v) {
		ActivityKt.moreApp(this, "Roy93Group");
	}

	public void shareApp(View v) {
		ActivityKt.shareApp(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("themePackageName")) {
			ThemePreference themePreview = (ThemePreference) findPreference("themePreview");
			themePreview.setTheme(newValue.toString());
			return false;
		}
		return true;
	}

}
