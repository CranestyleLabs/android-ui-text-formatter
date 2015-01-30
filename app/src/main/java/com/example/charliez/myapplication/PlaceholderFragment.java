package com.example.charliez.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
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
    private Section mSection;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view (why??)
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Grab the section from our repository
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        mSection = SectionRepository.getInstance().get(sectionNumber);

        // TODO: set the text of the control to the contents of the section

        // Find all of our controls here and set them to local variables for reference
        etx = (EditText) rootView.findViewById(R.id.editText);

        // Set text field
        etx.setText(
                Html.fromHtml("<i>Hello there,</i> she thought, wishing she could format text from the UI.<br/>Second paragraph, let's see how we fare with wrapping.<br/>Third paragraph, we're really on a roll now.")
        );

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

                if (s.length() > 0) {
                    LeadingMarginSpan.Standard[] spans = s.getSpans(0, s.length(), LeadingMarginSpan.Standard.class);
                    if (spans != null && spans.length > 0) {
                        // Move the end
                        s.setSpan(spans[0], 0, s.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
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

        if (startSelection > -1 && endSelection > -1) {
            Editable currentText = etx.getText();

            StyleSpan[] spans = currentText.getSpans(startSelection, endSelection, StyleSpan.class);

            if (spans != null) {
                boolean isRemove = false;

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

                if (!isRemove) {
                    final StyleSpan bss = new StyleSpan(style); // Span to make text bold
                    currentText.setSpan(bss, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }
}
