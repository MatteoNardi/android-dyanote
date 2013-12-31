package com.dyanote.android.utils;

import android.text.SpannableStringBuilder;

// DyanoteSpannableStringBuilder is a SpannableStringBuilder which allows to add special formatting.
// This is used to convert a Note xml to a representation compatible with a TextView.
public abstract class DyanoteSpannableStringBuilder extends SpannableStringBuilder {

    public abstract void setBold(int start, int end);

    public abstract void setItalic(int start, int end);

    public abstract void setHeader(int start, int end);

    public abstract void setLink(int start, int end, Long href);
}
