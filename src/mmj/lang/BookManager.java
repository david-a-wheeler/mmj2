//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * BookManager.java  0.01 08/01/2008
 *
 * Aug-1-2008:
 *     --> new!
 */

package mmj.lang;

import java.util.*;

import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.MMIOConstants;
import mmj.tl.*;

/**
 * BookManager is a "helper" class that is used to keep track of Chapter and
 * Section definitions for an input Metamath database and its objects (called
 * "MObj"s herein.)
 * <p>
 * BookManager keeps track of input Chapters and Sections from a .mm database as
 * the data is input, while LogicalSystem controls assigning Section and MObjNbr
 * data items to BookManager's Chapters and Sections. This is an important
 * point: BookManager is called during the FileLoad process as each MObj is
 * created so that the BookManager can assign SectionMObjNbrs in the correct
 * order. <p . By "Chapter" we refer to a portion of a Metamath .mm file
 * beginning with a Metamath Comment statement ("$(") whose first token (after
 * "$(") begins with "#*#*". The "title" is extracted from the Metamath comment
 * by stringing together the non-whitespace, non-"#*#*"-prefixed tokens. (Norm
 * refers to these Chapters as "Sections" and our Sections and "Sub-Sections").
 * <p>
 * A "Section" is similar to Chapter except that it is contained within a
 * Chapter and the identifying token prefix string is "=-=-".
 * <p>
 * In some cases there may not be a Section header within a Chapter, and in that
 * case the Chapter title is used for the Section title and a default-Section
 * must be automatically generated by the system.
 * <p>
 * Likewise, if a valid Chapter comment statement has not been found before
 * Metamath objects or a Section comment statement are input, then a default
 * Chapter with a default title is automatically generated by the system.
 * <p>
 * It is possible that neither Chapters or Sections are used by an input .mm
 * database. In this case everything is loaded into a single default Chapter
 * with four default Sections.
 * <p>
 * <code> Note: Input Sections are physically split and assigned sequential
 * numbers based on the MObj content type as shown below. The Section numbers
 * are multiples of 1, 2, 3, or 4 as follows:<br>
 * <br>
 * 1 = Cnst or Var symbols<br>
 * 2 = VarHyp<br>
 * 3 = Syntax Axioms<br>
 * 4 = Theorems, Logic Axioms and LogHyps.<br>
 * <br>
 * Thus, the first input Section is assigned Section numbers 1, 2, 3, and 4. The
 * 2nd input Section is assigned Section numbers 5, 6, 7 and 8. And so on. These
 * numbers are assigned across Chapter boundaries -- meaning that Section
 * numbers do not reset to 1 at the beginning of each chapter.
 */
public class BookManager implements TheoremLoaderCommitListener {

    private final boolean enabled;
    private final String provableLogicStmtTypeParm;

    private List<Chapter> chapterList;
    private List<Section> sectionList;

    private Chapter currChapter;

    private Section currSymSection;
    private Section currVarHypSection;
    private Section currSyntaxSection;
    private Section currLogicSection;

    private int inputSectionCounter;

    private int totalNbrMObjs;

    private String nextChapterTitle = MMIOConstants.DEFAULT_TITLE;
    private String nextSectionTitle = MMIOConstants.DEFAULT_TITLE;

    private String[] chapterValues = null;
    private final String[][] sectionValues = null;
    private BitSet[] sectionDependencies = null;
    private BitSet[] chapterDependencies = null;
    private BitSet[] directSectionDependencies = null;
    private BitSet[] directChapterDependencies = null;

    /**
     * Sole constructor for BookManager.
     *
     * @param enabled Book Manager enabled? If not enabled then zero Chapter and
     *            Section numbers are assigned and no data is retained.
     * @param provableLogicStmtTypeParm String identifying theorems, logic
     *            axioms and logical hypotheses (normally = "|-" in Metamath,
     *            matched against the first symbol of the object's formula).
     */
    public BookManager(final boolean enabled,
        final String provableLogicStmtTypeParm)
    {
        this.enabled = enabled;

        this.provableLogicStmtTypeParm = provableLogicStmtTypeParm;

        if (enabled) {
            chapterList = new ArrayList<>(
                LangConstants.ALLOC_NBR_BOOK_CHAPTERS_INITIAL);
            sectionList = new ArrayList<>(
                LangConstants.ALLOC_NBR_BOOK_SECTIONS_INITIAL);
        }
        else {
            chapterList = new ArrayList<>(1);
            sectionList = new ArrayList<>(1);
        }
    }

    public static int getOrigSectionNbr(final int i) {
        return (i - 1) / LangConstants.SECTION_NBR_CATEGORIES + 1;
    }

    public static int convertOrigSectionNbr(final int i) {
        return (i - 1) * LangConstants.SECTION_NBR_CATEGORIES + 1;
    }

    /**
     * Stores new MObj's from the TheoremLoader as part of a load operation.
     * <p>
     * BookManager is called to perform its updates en masse at the end of the
     * TheoremLoader update. A failure of BookManager to complete the updates is
     * deemed irreversible and severe, warranting a message to the user to
     * manually restart mmj2.
     *
     * @param mmtTheoremSet the set of MMTTheoremFile object added or updated by
     *            TheoremLoader.
     */
    public void commit(final MMTTheoremSet mmtTheoremSet) {
        if (!enabled)
            return;

        final List<TheoremStmtGroup> addList = mmtTheoremSet
            .buildSortedListOfAdds(TheoremStmtGroup.SEQ);

        if (addList.isEmpty())
            return;

        for (final TheoremStmtGroup t : addList) {

            /* ok, if object inserted then section is the
               section of the thing it was inserted after
               (converted for stmt type appropriate section nbr).
               otherwise, if appended add to final section.
             */
            final int insertSectionNbr = t.getInsertSectionNbr();
            if (insertSectionNbr > 0)
                commitInsertTheoremStmtGroup(t, insertSectionNbr);
            else
                commitAppendTheoremStmtGroup(t);
        }
    }

    /**
     * Returns BookManager enabled flag, which indicates whether or not the
     * BookManager is in use within the currently system.
     *
     * @return BookManager enabled flag.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the Chapter corresponding to a given Chapter Nbr.
     *
     * @param chapterNbr Chapter number.
     * @return Chapter or null if no such chapter exists.
     */
    public Chapter getChapter(final int chapterNbr) {
        if (!enabled)
            return null;
        Chapter chapter = null;
        try {
            chapter = chapterList.get(chapterNbr - 1);
        } catch (final IndexOutOfBoundsException e) {}
        return chapter;
    }

    /**
     * Returns the Chapter corresponding to a given Section Nbr.
     *
     * @param sectionNbr Section number.
     * @return Chapter or null if no such section exists.
     */
    public Chapter getChapterForSectionNbr(final int sectionNbr) {
        if (!enabled)
            return null;
        Chapter chapter = null;
        final Section section = getSection(sectionNbr);
        if (section != null)
            chapter = section.getSectionChapter();
        return chapter;
    }

    /**
     * Returns the Section corresponding to a given Section Nbr.
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param sectionNbr Section number.
     * @return Chapter or null if no such section exists or BookManager is not
     *         enabled.
     */
    public Section getSection(final int sectionNbr) {
        if (!enabled)
            return null;
        Section section = null;
        try {
            section = sectionList.get(sectionNbr - 1);
        } catch (final IndexOutOfBoundsException e) {}
        return section;
    }

    /**
     * Adds a new Chapter to the BookManager's collection.
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param chapterTitle Chapter Title or descriptive String.
     */
    public void addNewChapter(final String chapterTitle) {
        if (!enabled)
            return;
        nextChapterTitle = chapterTitle;
        nextSectionTitle = null;
    }

    /**
     * Adds a new Section to the current Chapter in the BookManager's collection
     * <p>
     * If no prior Chapter has been input a default Chapter is automatically
     * created.
     * <p>
     * Note that one call to addNewSection() actually creates the 4 Sections
     * (Symbols, VarHyps, Syntax and Logic) that correspond to one input .mm
     * database section.
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param sectionTitle or descriptive String.
     */
    public void addNewSection(final String sectionTitle) {
        if (!enabled)
            return;
        nextSectionTitle = sectionTitle;
    }

    /**
     * Assigns Chapter and SectionNbrs to an axiom.
     * <p>
     * If the Type Code of the Axiom is equal to the Provable Logic Statement
     * Type code parameter then the input Axiom is assigned to the current
     * "Logic" Section. Otherwise it is considered to be "Syntax".
     * <p>
     * This function is provided for use by the LogicalSystem during initial
     * load of an input .mm database. If the MObj has already been assigned
     * SectionMObjNbr then no update is performed (this is significant normally
     * only for re-declared Vars because only Metamath Vars can be validly
     * re-declared -- this happens with in-scope local Var declarations.) no
     * update
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param axiom newly created Axiom.
     */
    public void assignChapterSectionNbrs(final Axiom axiom) {
        if (enabled) {

            prepareChapterSectionForMObj();

            if (axiom.getTyp().getId().equals(provableLogicStmtTypeParm)) {

                if (currLogicSection.assignChapterSectionNbrs(axiom))
                    totalNbrMObjs++;
            }
            else if (currSyntaxSection.assignChapterSectionNbrs(axiom))
                totalNbrMObjs++;
        }
    }

    /**
     * Assigns Chapter and SectionNbrs to a theorem.
     * <p>
     * Note: "syntax theorems" are assigned to the current "Logic" section, not
     * "Syntax".
     * <p>
     * This function is provided for use by the LogicalSystem during initial
     * load of an input .mm database. If the MObj has already been assigned
     * SectionMObjNbr then no update is performed (this is significant normally
     * only for re-declared Vars because only Metamath Vars can be validly
     * re-declared -- this happens with in-scope local Var declarations.) no
     * update
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param theorem newly created Theorem.
     */
    public void assignChapterSectionNbrs(final Theorem theorem) {
        if (enabled) {

            prepareChapterSectionForMObj();

            if (currLogicSection.assignChapterSectionNbrs(theorem))
                totalNbrMObjs++;
        }
    }

    /**
     * Assigns Chapter and SectionNbrs to a logical hypothesis.
     * <p>
     * This function is provided for use by the LogicalSystem during initial
     * load of an input .mm database. If the MObj has already been assigned
     * SectionMObjNbr then no update is performed (this is significant normally
     * only for re-declared Vars because only Metamath Vars can be validly
     * re-declared -- this happens with in-scope local Var declarations.) no
     * update
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param logHyp newly created LogHyp.
     */
    public void assignChapterSectionNbrs(final LogHyp logHyp) {
        if (enabled) {

            prepareChapterSectionForMObj();

            if (currLogicSection.assignChapterSectionNbrs(logHyp))
                totalNbrMObjs++;
        }
    }

    /**
     * Assigns Chapter and SectionNbrs to a VarHyp.
     * <p>
     * This function is provided for use by the LogicalSystem during initial
     * load of an input .mm database. If the MObj has already been assigned
     * SectionMObjNbr then no update is performed (this is significant normally
     * only for re-declared Vars because only Metamath Vars can be validly
     * re-declared -- this happens with in-scope local Var declarations.) no
     * update
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param varHyp newly created VarHyp
     */
    public void assignChapterSectionNbrs(final VarHyp varHyp) {
        if (enabled) {

            prepareChapterSectionForMObj();

            if (currVarHypSection.assignChapterSectionNbrs(varHyp))
                totalNbrMObjs++;
        }
    }

    /**
     * Assigns Chapter and SectionNbrs to either a Cnst or a Var.
     * <p>
     * This function is provided for use by the LogicalSystem during initial
     * load of an input .mm database. If the MObj has already been assigned
     * SectionMObjNbr then no update is performed (this is significant normally
     * only for re-declared Vars because only Metamath Vars can be validly
     * re-declared -- this happens with in-scope local Var declarations.) no
     * update
     * <p>
     * Note: no processing occurs if BookManager is not enabled.
     *
     * @param sym newly created Sym.
     */
    public void assignChapterSectionNbrs(final Sym sym) {
        if (enabled) {

            prepareChapterSectionForMObj();

            if (currSymSection.assignChapterSectionNbrs(sym))
                totalNbrMObjs++;
        }
    }

    /**
     * Returns the count of all MObj objects assigned to Sections within the
     * BookManager.
     *
     * @return total number of MObjs added so far.
     */
    public int getTotalNbrMObjs() {
        return totalNbrMObjs;
    }

    /**
     * Returns the List of Chapters in the BookManager.
     * <p>
     * Note: if BookManager is not enabled, the List returned will not be null,
     * it will be empty.
     *
     * @return List of Chapters.
     */
    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public Chapter lookupChapterByTitle(final String s) {
        for (final Object element : chapterList) {
            final Chapter chapter = (Chapter)element;
            if (s.equals(chapter.getChapterTitle()))
                return chapter;
        }

        return null;
    }

    public Section lookupSectionByChapterAndTitle(final Chapter chapter,
        final String s, final int i)
    {
        if (chapter == null)
            return null;
        Section section = chapter.getFirstSection();
        int j = section.getSectionNbr();
        final int k = chapter.getLastSection().getSectionNbr();
        do {
            if (s.equals(section.getSectionTitle())
                && section.getSectionCategoryCd() == i)
                return section;
            if (++j > k)
                return null;
            section = getSection(j);
        } while (true);
    }

    public String[] getChapterValuesForSearch() {
        if (chapterValues != null)
            return chapterValues;
        chapterValues = new String[chapterList.size() + 1];
        int i = 0;
        chapterValues[i++] = "";
        for (final Object element : chapterList)
            chapterValues[i++] = ((Chapter)element).getChapterTitle();

        return chapterValues;
    }

    // TODO auto-generated from class files
    public String[][] getSectionValuesForSearch() {
        String[][] as;
        if (sectionValues != null)
            return sectionValues;
        as = new String[chapterList.size() + 1][];
        int i = 0;
        final int j = 0;
        as[i] = new String[1];
        as[i][j] = "";
        for (final Chapter chapter : chapterList) {
            i++;
            Section section = chapter.getFirstSection();
            int l = section.getSectionNbr();
            final int i1 = chapter.getLastSection().getSectionNbr();
            final int j1 = (i1 - l) / LangConstants.SECTION_NBR_CATEGORIES + 1;
            as[i] = new String[j1 + 1];
            int k = 0;
            as[i][k] = "";
            do {
                as[i][++k] = section.getSectionTitle();
                if ((l += LangConstants.SECTION_NBR_CATEGORIES) > i1)
                    break;
                section = getSection(l);
            } while (true);
        }
        return as;
    }

    public void invalidateChapterSectionDependencies() {
        chapterDependencies = null;
        sectionDependencies = null;
        directChapterDependencies = null;
        directSectionDependencies = null;
    }

    public void recomputeAssrtNbrProofRefs(final LogicalSystem logicalSystem) {
        if (enabled && directSectionDependencies == null) {
            for (final Stmt s : logicalSystem.getStmtTbl().values())
                s.initNbrProofRefs();
            getDirectSectionDependencies(logicalSystem);
        }
    }

    public BitSet[] getChapterDependencies(final LogicalSystem logicalSystem) {
        if (!enabled)
            return null;
        if (chapterDependencies == null)
            chapterDependencies = buildChapterDependencies(
                getSectionDependencies(logicalSystem));
        return chapterDependencies;
    }

    public BitSet[] getDirectChapterDependencies(
        final LogicalSystem logicalsystem)
    {
        if (!enabled)
            return null;
        if (directChapterDependencies == null)
            directChapterDependencies = buildChapterDependencies(
                getDirectSectionDependencies(logicalsystem));
        return directChapterDependencies;
    }

    public BitSet[] buildChapterDependencies(final BitSet[] bs) {
        final BitSet[] bs2 = new BitSet[chapterList.size() + 1];
        for (int i = 0; i < bs2.length; i++)
            bs2[i] = new BitSet(i);

        for (int j1 = 1; j1 < bs2.length; j1++) {
            final Chapter chapter = getChapter(j1);
            final int j = getOrigSectionNbr(
                chapter.getFirstSection().getSectionNbr());
            final int k = getOrigSectionNbr(
                chapter.getLastSection().getSectionNbr());
            for (int k1 = j1; k1 < bs2.length; k1++) {
                final Chapter chapter1 = getChapter(k1);
                final int l = getOrigSectionNbr(
                    chapter1.getFirstSection().getSectionNbr());
                final int i1 = getOrigSectionNbr(
                    chapter1.getLastSection().getSectionNbr());
                boolean flag = false;
                for (int l1 = l; l1 <= i1; l1++)
                    if (bs[l1].nextSetBit(j) <= k) {
                        flag = true;
                        break;
                    }
                if (flag)
                    bs2[k1].set(j1);
            }

        }

        return bs2;
    }

    public int getChapterMinMObjSeq(final Chapter chapter) {
        return chapter.getMinMObjSeq();
    }

    public int getChapterMaxMObjSeq(final Chapter chapter) {
        return chapter.getMaxMObjSeq();
    }

    public int getSectionMinMObjSeq(final Section section) {
        int min = 0;
        int j = convertOrigSectionNbr(
            getOrigSectionNbr(section.getSectionNbr()));
        for (int l = 0; l < LangConstants.SECTION_NBR_CATEGORIES; l++) {
            final int len = getSection(j++).getMinMObjSeq();
            if (len != 0 && len < min)
                min = len;
        }

        return min;
    }

    public int getSectionMaxMObjSeq(final Section section) {
        int max = 0;
        int j = convertOrigSectionNbr(
            getOrigSectionNbr(section.getSectionNbr()));
        for (int l = 0; l < LangConstants.SECTION_NBR_CATEGORIES; l++) {
            final int len = getSection(j++).getMaxMObjSeq();
            if (len != 0 && len > max)
                max = len;
        }

        return max;
    }

    public BitSet[] getSectionDependencies(final LogicalSystem logicalsystem) {
        if (!enabled)
            return null;
        if (sectionDependencies != null)
            return sectionDependencies;
        final BitSet[] abitset = getDirectSectionDependencies(logicalsystem);
        sectionDependencies = new BitSet[abitset.length];
        sectionDependencies[0] = new BitSet(0);
        for (int i = 1; i < sectionDependencies.length; i++) {
            sectionDependencies[i] = (BitSet)abitset[i].clone();
            for (int j = abitset[i].nextSetBit(0); j >= 0; j = abitset[i]
                .nextSetBit(j + 1))
                sectionDependencies[i].or(sectionDependencies[j]);

        }

        return sectionDependencies;
    }

    public BitSet[] getDirectSectionDependencies(
        final LogicalSystem logicalsystem)
    {
        if (!enabled)
            return null;
        if (directSectionDependencies != null)
            return directSectionDependencies;
        directSectionDependencies = new BitSet[sectionList.size()
            / LangConstants.SECTION_NBR_CATEGORIES + 1];
        for (int i = 0; i < directSectionDependencies.length; i++) {
            directSectionDependencies[i] = new BitSet(i);
            directSectionDependencies[i].set(i);
        }

        for (final Stmt stmt : logicalsystem.getStmtTbl().values()) {
            final BitSet directDeps = directSectionDependencies[stmt
                .getOrigSectionNbr()];
            if (stmt instanceof Theorem) {
                final RPNStep[] proof = ((Theorem)stmt).getProof();
                int j = 0;
                while (j < proof.length) {
                    if (proof[j] != null && proof[j].stmt != null) {
                        final Stmt proofStep = proof[j].stmt;
                        proofStep.incrementNbrProofRefs();
                        if (proofStep.getTyp().getId()
                            .equals(provableLogicStmtTypeParm)
                            && proofStep instanceof Assrt)
                            directDeps.set(proofStep.getOrigSectionNbr());
                    }
                    j++;
                }
            }
        }
        return directSectionDependencies;
    }

    /**
     * Returns the List of Sections in the BookManager.
     * <p>
     * Note: if BookManager is not enabled, the List returned will not be null,
     * it will be empty.
     *
     * @return List of Sections.
     */
    public List<Section> getSectionList() {
        return sectionList;
    }

    /**
     * Returns an Iterable over all of the MObjs assigned to Sections.
     * <p>
     * Note: if BookManager is not enabled, the Iterable returned will not be
     * null, it will be empty.
     *
     * @param logicalSystem the mmj2 LogicalSystem object.
     * @return List of Sections.
     */
    public Iterable<MObj> getSectionMObjIterable(
        final LogicalSystem logicalSystem)
    {
        final MObj[][] sectionArray = getSectionMObjArray(logicalSystem);
        return new Iterable<MObj>() {
            public Iterator<MObj> iterator() {
                return new SectionMObjIterator(sectionArray);
            }
        };
    }

    /**
     * Returns a two-dimensional array of MObjs by Section and MObjNbr within
     * Section.
     * <p>
     * Note: if BookManager is not enabled, the array is empty and is allocated
     * as {@code new MObj[0][]}.
     *
     * @param logicalSystem the mmj2 LogicalSystem object.
     * @return two-dimensional array of MObjs by Section and MObjNbr within
     *         Section.
     */
    public MObj[][] getSectionMObjArray(final LogicalSystem logicalSystem) {

        MObj[][] sectionArray;

        if (!enabled) {
            sectionArray = new MObj[0][];
            return sectionArray;
        }

        sectionArray = new MObj[sectionList.size()][];

        int i = 0;
        for (final Section section : sectionList)
            sectionArray[i++] = new MObj[section.getLastMObjNbr()];

        loadSectionArrayEntry(sectionArray, logicalSystem.getSymTbl().values());
        loadSectionArrayEntry(sectionArray,
            logicalSystem.getStmtTbl().values());
        return sectionArray;
    }

    /**
     * Nested class which implements Iterable for a two-dimensional array of
     * MObjs by Section and MObjNbr within Section.
     */
    public class SectionMObjIterator implements Iterator<MObj> {
        private int prevI;
        private int prevJ;
        private int nextI;
        private int nextJ;
        private final MObj[][] mArray;

        /**
         * Sole Constructor.
         * <p>
         * Note: the input array must not contain any null (empty) array
         * entries. It is assumed to be completely full (though the idea of
         * arrays with padding was considered, it was rejected ... for now.)
         *
         * @param s two-dimensional array of MObjs by Section and MObjNbr within
         *            Section.
         */
        public SectionMObjIterator(final MObj[][] s) {
            mArray = s;
            prevI = 0;
            prevJ = -1;
        }

        /**
         * Returns the next MObj within the two-dimensional array.
         *
         * @return the next MObj within the two-dimensional array.
         * @throws NoSuchElementException if there are no more MObjs to return.
         */
        public MObj next() {
            if (!hasNext())
                throw new NoSuchElementException();
            prevI = nextI;
            prevJ = nextJ;
            return mArray[prevI][prevJ];
        }

        /**
         * Returns true if there is another MObj to return within the
         * two-dimensional array.
         *
         * @return true if there is a next() MObj within the two-dimensional
         *         array.
         */
        public boolean hasNext() {
            nextI = prevI;
            nextJ = prevJ + 1;
            while (nextI < mArray.length) {
                if (nextJ < mArray[nextI].length)
                    return true;
                nextI++;
                nextJ = 0;
            }
            return false;
        }

        /**
         * Not supported.
         *
         * @throws UnsupportedOperationException if called.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void prepareChapterSectionForMObj() {
        if (nextChapterTitle != null) {

            if (nextSectionTitle == null)
                nextSectionTitle = nextChapterTitle;

            constructNewChapter();
            nextChapterTitle = null;
            nextSectionTitle = null;
        }
        else if (nextSectionTitle != null) {

            constructNewSection();
            nextSectionTitle = null;
        }
    }

    private void constructNewChapter() {

        currChapter = new Chapter(nextChapterNbr(), nextChapterTitle);
        chapterList.add(currChapter);

        constructNewSection();
    }

    private void constructNewSection() {

        final int n = inputSectionCounter++
            * LangConstants.SECTION_NBR_CATEGORIES;

        currSymSection = new Section(currChapter,
            n + LangConstants.SECTION_SYM_CD, nextSectionTitle);
        sectionList.add(currSymSection);

        currVarHypSection = new Section(currChapter,
            n + LangConstants.SECTION_VAR_HYP_CD, nextSectionTitle);
        sectionList.add(currVarHypSection);

        currSyntaxSection = new Section(currChapter,
            n + LangConstants.SECTION_SYNTAX_CD, nextSectionTitle);
        sectionList.add(currSyntaxSection);

        currLogicSection = new Section(currChapter,
            n + LangConstants.SECTION_LOGIC_CD, nextSectionTitle);
        sectionList.add(currLogicSection);
    }

    private int nextChapterNbr() {
        int chapterNbr;
        if (currChapter == null)
            chapterNbr = 1;
        else
            chapterNbr = currChapter.getChapterNbr() + 1;
        return chapterNbr;
    }

    private void loadSectionArrayEntry(final MObj[][] sectionArray,
        final Iterable<? extends MObj> iterable)
    {
        for (final MObj mObj : iterable)
            sectionArray[mObj.sectionNbr - 1][mObj.sectionMObjNbr - 1] = mObj;
    }

    private void commitAppendTheoremStmtGroup(final TheoremStmtGroup t) {

        final LogHyp[] logHypArray = t.getLogHypArray();
        for (final LogHyp element : logHypArray)
            assignChapterSectionNbrs(element);
        assignChapterSectionNbrs(t.getTheorem());
    }

    private void commitInsertTheoremStmtGroup(final TheoremStmtGroup t,
        final int insertSectionNbr)
    {

        // 'x' is the actual section number to be used for
        // insertions. Note that only 'logic' theorems can
        // be added using Theorem Loader! The input section
        // number could be any category, so we reverse out
        // the input category code and add back in "logic".
        final int x = insertSectionNbr
            - Section
                .getSectionCategoryCd(/* old category cd */insertSectionNbr)
            + LangConstants.SECTION_LOGIC_CD; /* new category cd */

        final Section insertSection = getSection(x);
        if (insertSection == null)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_BM_UPDATE_W_MMT_SECTION_NOTFND, x,
                t.getTheoremLabel()));

        final LogHyp[] logHypArray = t.getLogHypArray();
        for (final LogHyp element : logHypArray)
            if (insertSection.assignChapterSectionNbrs(element))
                totalNbrMObjs++;
        if (insertSection.assignChapterSectionNbrs(t.getTheorem()))
            totalNbrMObjs++;
    }
}
