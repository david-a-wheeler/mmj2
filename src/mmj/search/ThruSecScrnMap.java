// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ThruSecScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, FromChapScrnMap, ThruChapScrnMap, FromSecScrnMap

public class ThruSecScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public ThruSecScrnMap(
        final DefaultComboBoxModel<String> defaultcomboboxmodel,
        final String[][] as)
    {
        super(46, defaultcomboboxmodel);
        thruChapScrnMap = null;
        secValues = as;
        addActionListener(this);
    }

    public void setThruChapScrnMap(final ThruChapScrnMap thruChapScrnMap) {
        this.thruChapScrnMap = thruChapScrnMap;
    }

    public void chapIdUpdated(final int i) {
        updateComboBoxList(secValues[i]);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null)
            fixFromSecIfNecessary(s);
    }

    private void updateComboBoxList(final String[] as) {
        removeAllItems();
        for (final String element : as)
            addItem(element);

        setSelectedItem(as[0]);
    }

    private void fixFromSecIfNecessary(final String s) {
        if (s.equals(secValues[0][0]) || thruChapScrnMap == null
            || thruChapScrnMap.chap.equals(secValues[0][0]))
            return;
        final FromChapScrnMap fromChapScrnMap = thruChapScrnMap.fromChapScrnMap;
        if (fromChapScrnMap == null
            || fromChapScrnMap.chap.equals(secValues[0][0])
            || !thruChapScrnMap.chap.equals(fromChapScrnMap.chap))
            return;
        final int i = getSelectedIndex();
        final int j = fromChapScrnMap.fromSecScrnMap.getSelectedIndex();
        if (j > i && i != -1)
            fromChapScrnMap.fromSecScrnMap.setSelectedItem(secValues[0][0]);
    }

    ThruChapScrnMap thruChapScrnMap;
    private final String[][] secValues;
}
