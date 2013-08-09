// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsJIntegerTextField.java

package mmj.search;

import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

// Referenced classes of package mmj.search:
//            SearchJTextFieldPopupMenuListener, SearchJTextFieldPopupMenu, SearchOptionsScrnMapField, SearchOptionsConstants,
//            SearchOptionsFieldAttr, SearchArgs, SearchArgsField, SearchMgr

public class SearchOptionsJIntegerTextField extends JFormattedTextField
    implements SearchOptionsScrnMapField
{

    public static NumberFormat createIntegerFormat(final int i) {
        final NumberFormat numberformat = NumberFormat.getIntegerInstance();
        numberformat.setGroupingUsed(false);
        numberformat.setParseIntegerOnly(true);
        numberformat.setMinimumIntegerDigits(1);
        numberformat
            .setMaximumIntegerDigits(SearchOptionsConstants.FIELD_ATTR[i].columns);
        return numberformat;
    }

    public SearchOptionsJIntegerTextField(final int i) {
        this(createIntegerFormat(i), i,
            SearchOptionsConstants.FIELD_ATTR[i].defaultText,
            SearchOptionsConstants.FIELD_ATTR[i].columns);
    }

    public SearchOptionsJIntegerTextField(final NumberFormat numberformat,
        final int i, final String s, final int j)
    {
        super(numberformat);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setColumns(j);
        set(s);
        setDefaultToCurrentValue();
        addMouseListener(new SearchJTextFieldPopupMenuListener(
            new SearchJTextFieldPopupMenu()));
        jLabel = new JLabel(SearchOptionsConstants.FIELD_ATTR[i].label);
        jLabel.setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        jLabel.setLabelFor(this);
    }

    @Override
    public void setEnabled(final boolean flag) {
        super.setEnabled(flag);
        jLabel.setEnabled(flag);
    }

    public int getFieldId() {
        return fieldId;
    }

    public void positionCursor(final int i) {
        setCaretPosition(i);
    }

    public void setSearchOptionsFont(final Font font) {
        setFont(font);
    }

    public void uploadFromScrnMap(final SearchArgs args) {
        args.arg[fieldId].set(get());
    }

    public void downloadToScrnMap(final SearchArgs args,
        final SearchMgr searchMgr)
    {
        set(args.arg[fieldId].get());
    }

    public void setDefaultToCurrentValue() {
        defaultValue = get();
    }

    public void resetToDefaultValue() {
        set(defaultValue);
    }

    public void set(final String s) {
        try {
            setValue(new Integer(s));
        } catch (final NumberFormatException numberformatexception) {
            throw new IllegalArgumentException(
                SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_1 + s
                    + SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_2
                    + fieldId
                    + SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_3
                    + SearchOptionsConstants.FIELD_ATTR[fieldId].label);
        }
    }

    public String get() {
        return getValue().toString();
    }

    public JLabel createJLabel() {
        return jLabel;
    }

    @Override
    public boolean requestFocusInWindow(final boolean flag) {
        return super.requestFocusInWindow(flag);
    }

    protected final int fieldId;
    protected final JLabel jLabel;
    protected String defaultValue;
}