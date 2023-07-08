package com.roy.turbo.launcher;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ThemePreference extends Preference {

    private CharSequence themeName;
    private CharSequence themePackageName;
    private CharSequence themeDescription;
    private Drawable themeIcon;
    private Drawable themePreview;

    public ThemePreference(Context context) {
        super(context);
    }

    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private CallBackFlAd callBackFlAd;

    interface CallBackFlAd {
        void onFlReady(FrameLayout flAd);
    }

    public void setCallBackFlAd(CallBackFlAd callBackFlAd) {
        this.callBackFlAd = callBackFlAd;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (themePackageName != null && themePackageName.toString().length() > 0) {
            TextView vThemeTitle = view.findViewById(R.id.ThemeTitle);
            FrameLayout flAd = view.findViewById(R.id.flAd);
            callBackFlAd.onFlReady(flAd);

            vThemeTitle.setText(themeName);
            TextView vThemeDescription = view.findViewById(R.id.ThemeDescription);
            vThemeDescription.setMovementMethod(LinkMovementMethod.getInstance());
            vThemeDescription.setText(Html.fromHtml(themeDescription.toString()));
            ImageView vThemePreview = view.findViewById(R.id.ivThemeIcon);
            if (themePreview != null)
                vThemePreview.setImageDrawable(themePreview);
            else
                vThemePreview.setImageResource(R.drawable.ic_theme);
            vThemeTitle.setText(themeName);

            Button applyButton = view.findViewById(R.id.btThemeApply);
            applyButton.setEnabled(true);
        } else {
            Button applyButton = view.findViewById(R.id.btThemeApply);
            applyButton.setEnabled(false);
        }
    }

    public void setTheme(CharSequence packageName) {
        themePackageName = packageName;
        themeName = null;
        themeDescription = null;
        if (themePreview != null) themePreview.setCallback(null);
        themePreview = null;
        if (!packageName.equals(Launcher.THEME_DEFAULT)) {
            Resources themeResources = null;
            try {
                themeResources = getContext().getPackageManager().getResourcesForApplication(packageName.toString());
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (themeResources != null) {
                int themeNameId = themeResources.getIdentifier("theme_title", "string", packageName.toString());
                if (themeNameId != 0) {
                    themeName = themeResources.getString(themeNameId);
                }
                int themeDescriptionId = themeResources.getIdentifier("theme_description", "string", packageName.toString());
                if (themeDescriptionId != 0) {
                    themeDescription = themeResources.getString(themeDescriptionId);
                }
                int themePreviewId = themeResources.getIdentifier("theme_preview", "drawable", packageName.toString());
                if (themePreviewId != 0) {
                    themePreview = themeResources.getDrawable(themePreviewId);
                }
            }
        }
        if (themeName == null)
            themeName = getContext().getResources().getString(R.string.pref_title_theme_preview);
        if (themeDescription == null)
            themeDescription = getContext().getResources().getString(R.string.pref_summary_theme_preview);

        notifyChanged();
    }

    public CharSequence getValue() {
        return themePackageName;
    }
}
