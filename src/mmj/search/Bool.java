// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Bool.java

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsComboBox, SearchOptionsConstants, CompiledSearchArgs, SearchOptionsFieldAttr, 
//            SearchMgr, SearchOutput

public class Bool extends SearchArgsComboBox {

    public Bool(final int i) {
        super(SearchOptionsConstants.BOOL_FIELD_ID[i]);
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        csa.searchBoolChoice[SearchOptionsConstants.FIELD_ATTR[fieldId].rowIndex] = getAndErrorIfBadChoice(
            searchOutput, SearchOptionsConstants.BOOL_VALUES);
    }
}