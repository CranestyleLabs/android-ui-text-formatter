package com.example.charliez.myapplication;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_paragraph) {
            formatParagraph(1);
            return true;
        }

        switch(id) {
            case R.id.action_bold:
            case R.id.action_underline:
            case R.id.action_italics:
                formatText(id);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void formatText(int actionID) {
        EditText etx = (EditText) findViewById(R.id.editText);
        int startSelection=etx.getSelectionStart();
        int endSelection=etx.getSelectionEnd();

        Editable currentText = etx.getText();

        switch(actionID) {
            case R.id.action_bold:
                // Create a bold tag where the user's cursor is. (?!?)
                // Unfortunately this sets the typeface for the entire text block
                //editor.setTypeface(null, Typeface.BOLD);
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
                currentText.setSpan(bss, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                break;
            case R.id.action_italics:
                final StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC); // Span to make text italic
                currentText.setSpan(iss, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                break;
            case R.id.action_underline:
                // Not supported right now.  I mean, who underlines?
                break;
        }

        // Put the text back in the control
        etx.setText(currentText);

        // place the cursor at the end of the text selection
        etx.setSelection(endSelection);
    }

    private void formatParagraph(int indentation) {
        EditText etx = (EditText) findViewById(R.id.editText);
        // TODO: Make text size a setting, need to adjust here accordingly
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
        Editable currentText = etx.getText();
        int endSelection=etx.getSelectionEnd();

        // Remove current paragraph formatting
        int start =0;
        int end = currentText.length();
        LeadingMarginSpan.Standard[] spans = currentText.getSpans(start, end-1, LeadingMarginSpan.Standard.class);
        if (spans != null) {
            for (int i = 0; i < spans.length; i++) {
                currentText.removeSpan(spans[i]);
            }
        }

        // Set the style for the entire text
        ParagraphStyle para_style = new LeadingMarginSpan.Standard(indent, 0);
        currentText.setSpan(para_style, start, end-1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Need to set the text to redraw the control, otherwise text can be clipped at the end of the line
        etx.setText(currentText);

        // Put the cursor back where it was!
        etx.setSelection(endSelection);

        // TODO: Indent does not apply to new paragraphs, how to we fix this?

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private EditText etx;

        public PlaceholderFragment() {
        }

//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the view (why??)
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Find all of our controls here and set them to local variables for reference
            etx = (EditText) rootView.findViewById(R.id.editText);

            // Set text field
            etx.setText(
                    Html.fromHtml("<i>Hello there,</i> she thought, wishing she could format text from the UI.<br/>Second paragraph, let's see how we fare with wrapping.<br/>Third paragraph, we're really on a roll now.")
            );
            etx.setMovementMethod(LinkMovementMethod.getInstance());

            // Register button clicks
            Button boldButton = (Button)rootView.findViewById(R.id.btnBold);
            boldButton.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            formatText(R.id.action_bold);
                        }
                    }
            );
            Button italicsButton = (Button)rootView.findViewById(R.id.btnItalics);
            italicsButton.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            formatText(R.id.action_italics);
                        }
                    }
            );

            // Change paragraphs to match formatting
            etx.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO: Create a fragment reference to the span to save searching
                    // TODO: Figure out how to only format when the user hits Return
                    // Get the paragraph span
                    LeadingMarginSpan.Standard[] spans = s.getSpans(0, s.length(), LeadingMarginSpan.Standard.class);

                    if (spans != null && spans.length > 0) {
                        // Move the end
                        s.setSpan(spans[0], 0, s.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Nothing
                }
            });


            // TODO: Figure out how to format the buttons to indicate whether a text selection meets
            // the format criteria

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        private void formatText(int actionID) {
            switch(actionID) {
                case R.id.action_bold:
                    processStyleChange(Typeface.BOLD);
                    break;
                case R.id.action_italics:
                    processStyleChange(Typeface.ITALIC);
                    break;
                case R.id.action_underline:
                    // Not supported as a typeface.  I mean, who underlines, anyway?
                    break;
            }
        }

        /**
         * Process changes for the selected text and the given style.  Will add, remove or split
         * spans as necessary
         * @param style The style that should be filtered.
         */
        private void processStyleChange(int style) {
            int startSelection=etx.getSelectionStart();
            int endSelection=etx.getSelectionEnd();

            Editable currentText = etx.getText();

            StyleSpan[] spans = currentText.getSpans(startSelection, endSelection, StyleSpan.class);

            boolean isRemove = false;
            if (spans != null) {
                for (int i = 0; i < spans.length; i++) {
                    StyleSpan span = spans[i];
                    if (span.getStyle() == style) {
                        int spanStart = currentText.getSpanStart(span);
                        int spanEnd = currentText.getSpanEnd(span);

                        // If the span is within the selected text, remove it
                        if (spanStart >= startSelection && spanEnd <= endSelection) {
                            currentText.removeSpan(span);
                            isRemove = true;
                        }

                        // If the span extends past the beginning of the selection, but not beyond the end, resize the span
                        if (spanStart < startSelection && spanEnd <= endSelection) {
                            currentText.setSpan(span, spanStart, startSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            isRemove = true;
                        }

                        // If the span extends past the end of the selection, but not before the start, resize the span
                        if (spanStart >= startSelection && spanEnd > endSelection) {
                            currentText.setSpan(span, endSelection, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            isRemove = true;
                        }

                        // If the span extends past both ends of the selection, split it
                        if (spanStart < startSelection && spanEnd > endSelection) {
                            // Set the span to the left
                            currentText.setSpan(span, spanStart, startSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            // Create a new span to the right
                            span = new StyleSpan(style); // Span to make text bold
                            currentText.setSpan(span, endSelection, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            isRemove = true;
                        }
                    }
                }
            }

            if (!isRemove) {
                final StyleSpan bss = new StyleSpan(style); // Span to make text bold
                currentText.setSpan(bss, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

}
