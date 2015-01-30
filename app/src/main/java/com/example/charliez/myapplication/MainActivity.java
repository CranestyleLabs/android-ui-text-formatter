package com.example.charliez.myapplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;

import java.util.Hashtable;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize our section repository
        Hashtable<String, Section> sections = SectionRepository.getInstance().getSections();
        Section sec1 = new Section();
        sec1.setTitle("Section 1");
        sec1.setContents("This is the contents of section 1");
        sections.put(sec1.getTitle(), sec1);
        Section sec2 = new Section();
        sec2.setTitle("Section 2");
        sec2.setContents("This is the contents of section 2");
        sections.put(sec2.getTitle(), sec2);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_paragraph) {
            // TODO: Make this a setting
            formatParagraph(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void formatParagraph(int indentation) {
        EditText etx = (EditText) findViewById(R.id.editText);
        // TODO: Make text size a setting, need to adjust here accordingly
        // Figure out how wide a character is based on the style.  This style was set
        // on the control.  In the future, this will be user-configurable.
        TextAppearanceSpan style_char = new TextAppearanceSpan(etx.getContext(),
                android.R.style.TextAppearance_DeviceDefault_Widget_EditText);
        float textSize = style_char.getTextSize();

        // indentF roughly corresponds to ems in dp after accounting for
        // system/base font scaling, you'll need to tweak it

        float indentF = (float)indentation;
        int indent = (int) indentF;
        if (textSize > 0) {
            indent = (int) indentF * (int) textSize;
        }

        // Get current text and cursor position
        Editable currentText = etx.getText();
        int endSelection=etx.getSelectionEnd();

        // Remove current paragraph formatting
        int start =0;
        int end = currentText.length();
        int textLength = end - start;
        if (textLength > 0) {
            LeadingMarginSpan.Standard[] spans = currentText.getSpans(start, end - 1, LeadingMarginSpan.Standard.class);
            if (spans != null) {
                for (int i = 0; i < spans.length; i++) {
                    currentText.removeSpan(spans[i]);
                }
            }

            // Set the style for the entire text
            ParagraphStyle para_style = new LeadingMarginSpan.Standard(indent, 0);
            currentText.setSpan(para_style, start, end - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            // Need to set the text to redraw the control, otherwise text can be clipped at the end of the line
            etx.setText(currentText);

            // Put the cursor back where it was!
            etx.setSelection(endSelection);
        }
    }

}
