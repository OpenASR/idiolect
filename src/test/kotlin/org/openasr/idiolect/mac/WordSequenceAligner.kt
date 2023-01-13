package org.openasr.idiolect.mac

import java.util.*

/*
Copyright 2011 Brian Romanowski. All rights reserved.
Portions Copyright 1999-2002 Carnegie Mellon University.
Portions Copyright 2002 Sun Microsystems, Inc.
Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
All Rights Reserved.  Use is subject to license terms.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of
     conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list
     of conditions and the following disclaimer in the documentation and/or other materials
     provided with the distribution.

THIS SOFTWARE IS PROVIDED BY BRIAN ROMANOWSKI ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BRIAN ROMANOWSKI OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors.
*/
/*
 * Notes on licensing:
 * This code was written while inspecting the Sphinx 4 edu.cmu.sphinx.util.NISTAlign source code and
 * the NIST sclite documentation.  To the degree that the source code here is copied from NISTAlign,
 * it is covered by the copyrights reproduced above.  Brian Romanowski holds the copyright to the rest.
 */ /**
 * Computes the word error rate (WER) and other statistics available from an alignment of a hypothesis string and a reference string.
 * The alignment and metrics are intended to be, by default, identical to those of the [NIST SCLITE tool](http://www.icsi.berkeley.edu/Speech/docs/sctk-1.2/sclite.htm).
 *
 *
 * This code was written while consulting the Sphinx 4 edu.cmu.sphinx.util.NISTAlign source code.
 *
 * @author romanows
 */
class WordSequenceAligner
/**
 * Constructor.
 * Creates an object with default alignment penalties.
 */ @JvmOverloads constructor(
  /**
   * Substitution penalty for reference-hypothesis string alignment
   */
  private val substitutionPenalty: Int = DEFAULT_SUBSTITUTION_PENALTY,
  /**
   * Insertion penalty for reference-hypothesis string alignment
   */
  private val insertionPenalty: Int = DEFAULT_INSERTION_PENALTY,
  /**
   * Deletion penalty for reference-hypothesis string alignment
   */
  private val deletionPenalty: Int = DEFAULT_DELETION_PENALTY
) {
  /**
   * Result of an alignment.
   * Has a [.toString] method that pretty-prints human-readable metrics.
   *
   * @author romanows
   */
  inner class Alignment(
    reference: Array<String?>?,
    hypothesis: Array<String?>?,
    numSubstitutions: Int,
    numInsertions: Int,
    numDeletions: Int
  ) {
    /**
     * Reference words, with null elements representing insertions in the hypothesis sentence and upper-cased words representing an alignment mismatch
     */
    val reference: Array<String?>

    /**
     * Hypothesis words, with null elements representing deletions (missing words) in the hypothesis sentence and upper-cased words representing an alignment mismatch
     */
    val hypothesis: Array<String?>

    /**
     * Number of word substitutions made in the hypothesis with respect to the reference
     */
    val numSubstitutions: Int

    /**
     * Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference
     */
    val numInsertions: Int

    /**
     * Number of word deletions (necessary words missing) in the hypothesis with respect to the reference
     */
    val numDeletions: Int

    /**
     * Constructor.
     *
     * @param reference        reference words, with null elements representing insertions in the hypothesis sentence
     * @param hypothesis       hypothesis words, with null elements representing deletions (missing words) in the hypothesis sentence
     * @param numSubstitutions Number of word substitutions made in the hypothesis with respect to the reference
     * @param numInsertions    Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference
     * @param numDeletions     Number of word deletions (necessary words missing) in the hypothesis with respect to the reference
     */
    init {
      require(!(reference == null || hypothesis == null || reference.size != hypothesis.size || numSubstitutions < 0 || numInsertions < 0 || numDeletions < 0))
      this.reference = reference
      this.hypothesis = hypothesis
      this.numSubstitutions = numSubstitutions
      this.numInsertions = numInsertions
      this.numDeletions = numDeletions
    }

    val numCorrect: Int
      /**
       * Number of word correct words in the aligned hypothesis with respect to the reference.
       *
       * @return number of word correct words
       */
      get() = hypothesisLength - (numSubstitutions + numInsertions) // Substitutions are mismatched and not correct, insertions are extra words that aren't correct
    val isSentenceCorrect: Boolean
      /**
       * @return true when the hypothesis exactly matches the reference
       */
      get() = numSubstitutions == 0 && numInsertions == 0 && numDeletions == 0
    val referenceLength: Int
      /**
       * Get the length of the original reference sequence.
       * This is not the same as [.reference].length(), because that member variable may have null elements
       * inserted to mark hypothesis insertions.
       *
       * @return the length of the original reference sequence
       */
      get() = reference.size - numInsertions
    val hypothesisLength: Int
      /**
       * Get the length of the original hypothesis sequence.
       * This is not the same as [.hypothesis].length(), because that member variable may have null elements
       * inserted to mark hypothesis deletions.
       *
       * @return the length of the original hypothesis sequence
       */
      get() = hypothesis.size - numDeletions

    /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    override fun toString(): String {
      val ref = StringBuilder()
      val hyp = StringBuilder()
      ref.append("REF:\t")
      hyp.append("HYP:\t")
      for (i in reference.indices) {
        if (reference[i] == null) {
          for (j in 0 until hypothesis[i]!!.length) {
            ref.append("*")
          }
        } else {
          ref.append(reference[i])
        }
        if (hypothesis[i] == null) {
          for (j in 0 until reference[i]!!.length) {
            hyp.append("*")
          }
        } else {
          hyp.append(hypothesis[i])
        }
        if (i != reference.size - 1) {
          ref.append("\t")
          hyp.append("\t")
        }
      }
      val sb = StringBuilder()
      sb.append("\t")
      sb.append("# seq").append("\t")
      sb.append("# ref").append("\t")
      sb.append("# hyp").append("\t")
      sb.append("# cor").append("\t")
      sb.append("# sub").append("\t")
      sb.append("# ins").append("\t")
      sb.append("# del").append("\t")
      sb.append("acc").append("\t")
      sb.append("WER").append("\t")
      sb.append("# seq cor").append("\t")
      sb.append("\n")
      sb.append("STATS:\t")
      sb.append(1).append("\t")
      sb.append(referenceLength).append("\t")
      sb.append(hypothesisLength).append("\t")
      sb.append(numCorrect).append("\t")
      sb.append(numSubstitutions).append("\t")
      sb.append(numInsertions).append("\t")
      sb.append(numDeletions).append("\t")
      sb.append(numCorrect / referenceLength.toFloat()).append("\t")
      sb.append((numSubstitutions + numInsertions + numDeletions) / referenceLength.toFloat()).append("\t")
      sb.append(if (isSentenceCorrect) 1 else 0)
      sb.append("\n")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("-----\t")
      sb.append("\n")
      sb.append(ref).append("\n").append(hyp)
      return sb.toString()
    }
  }

  /**
   * Collects several alignment results.
   * Has a [.toString] method that pretty-prints a human-readable summary metrics for the collection of results.
   *
   * @author romanows
   */
  inner class SummaryStatistics(alignments: Collection<Alignment>) {
    /**
     * Number of correct words in the aligned hypothesis with respect to the reference
     */
    private var numCorrect = 0

    /**
     * Number of word substitutions made in the hypothesis with respect to the reference
     */
    private var numSubstitutions = 0

    /**
     * Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference
     */
    private var numInsertions = 0

    /**
     * Number of word deletions (necessary words missing) in the hypothesis with respect to the reference
     */
    private var numDeletions = 0

    /**
     * Number of hypotheses that exactly match the associated reference
     */
    private var numSentenceCorrect = 0

    /**
     * Total number of words in the reference sequences
     */
    var numReferenceWords = 0
      private set

    /**
     * Total number of words in the hypothesis sequences
     */
    var numHypothesisWords = 0
      private set

    /**
     * Number of sentences
     */
    var numSentences = 0
      private set

    /**
     * Constructor.
     *
     * @param alignments collection of alignments
     */
    init {
      for (a in alignments) {
        add(a)
      }
    }

    /**
     * Add a new alignment result
     *
     * @param alignment result to add
     */
    fun add(alignment: Alignment) {
      numCorrect += alignment.numCorrect
      numSubstitutions += alignment.numSubstitutions
      numInsertions += alignment.numInsertions
      numDeletions += alignment.numDeletions
      numSentenceCorrect += if (alignment.isSentenceCorrect) 1 else 0
      numReferenceWords += alignment.referenceLength
      numHypothesisWords += alignment.hypothesisLength
      numSentences++
    }

    val correctRate: Float
      get() = numCorrect / numReferenceWords.toFloat()
    val substitutionRate: Float
      get() = numSubstitutions / numReferenceWords.toFloat()
    val deletionRate: Float
      get() = numDeletions / numReferenceWords.toFloat()
    val insertionRate: Float
      get() = numInsertions / numReferenceWords.toFloat()
    val wordErrorRate: Float
      /**
       * @return the word error rate of this collection
       */
      get() = (numSubstitutions + numDeletions + numInsertions) / numReferenceWords.toFloat()
    val sentenceErrorRate: Float
      /**
       * @return the sentence error rate of this collection
       */
      get() = (numSentences - numSentenceCorrect) / numSentences.toFloat()

    /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    override fun toString(): String {
      val sb = StringBuilder()
      sb.append("# seq").append("\t")
      sb.append("# ref").append("\t")
      sb.append("# hyp").append("\t")
      sb.append("cor").append("\t")
      sb.append("sub").append("\t")
      sb.append("ins").append("\t")
      sb.append("del").append("\t")
      sb.append("WER").append("\t")
      sb.append("SER").append("\t")
      sb.append("\n")
      sb.append(numSentences).append("\t")
      sb.append(numReferenceWords).append("\t")
      sb.append(numHypothesisWords).append("\t")
      sb.append(correctRate).append("\t")
      sb.append(substitutionRate).append("\t")
      sb.append(insertionRate).append("\t")
      sb.append(deletionRate).append("\t")
      sb.append(wordErrorRate).append("\t")
      sb.append(sentenceErrorRate)
      return sb.toString()
    }
  }
  /**
   * Constructor.
   *
   * @param substitutionPenalty substitution penalty for reference-hypothesis string alignment
   * @param insertionPenalty    insertion penalty for reference-hypothesis string alignment
   * @param deletionPenalty     deletion penalty for reference-hypothesis string alignment
   */
  /**
   * Produce alignment results for several pairs of sentences.
   *
   * @param references reference sentences to align with the given hypotheses
   * @param hypotheses hypothesis sentences to align with the given references
   * @return collection of per-sentence alignment results
   * @see .align
   */
  fun align(references: List<Array<String>>, hypotheses: List<Array<String>>): List<Alignment> {
    require(references.size == hypotheses.size)
    if (references.size == 0) {
      return ArrayList()
    }
    val alignments: MutableList<Alignment> = ArrayList()
    val refIt = references.iterator()
    val hypIt = hypotheses.iterator()
    while (refIt.hasNext()) {
      alignments.add(align(refIt.next(), hypIt.next()))
    }
    return alignments
  }

  /**
   * Produces [Alignment] results from the alignment of the hypothesis words to the reference words.
   * Alignment is done via weighted string edit distance according to [.substitutionPenalty], [.insertionPenalty], [.deletionPenalty].
   *
   * @param reference  sequence of words representing the true sentence; will be evaluated as lowercase.
   * @param hypothesis sequence of words representing the hypothesized sentence; will be evaluated as lowercase.
   * @return results of aligning the hypothesis to the reference
   */
  fun align(reference: Array<String>, hypothesis: Array<String>): Alignment {
    // Values representing string edit operations in the backtrace matrix
    val OK = 0
    val SUB = 1
    val INS = 2
    val DEL = 3

    /*
         * Next up is our dynamic programming tables that track the string edit distance calculation.
         * The row address corresponds to an index within the sequence of reference words.
         * The column address corresponds to an index within the sequence of hypothesis words.
         * cost[0][0] addresses the beginning of two word sequences, and thus always has a cost of zero.
         */
    /** cost[3][2] is the minimum alignment cost when aligning the first two words of the reference to the first word of the hypothesis  */
    val cost = Array(reference.size + 1) { IntArray(hypothesis.size + 1) }

    /**
     * backtrace[3][2] gives information about the string edit operation that produced the minimum cost alignment between the first two words of the reference to the first word of the hypothesis.
     * If a deletion operation is the minimum cost operation, then we say that the best way to get to hyp[1] is by deleting ref[2].
     */
    val backtrace = Array(reference.size + 1) { IntArray(hypothesis.size + 1) }

    // Initialization
    cost[0][0] = 0
    backtrace[0][0] = OK

    // First column represents the case where we achieve zero hypothesis words by deleting all reference words.
    for (i in 1 until cost.size) {
      cost[i][0] = deletionPenalty * i
      backtrace[i][0] = DEL
    }

    // First row represents the case where we achieve the hypothesis by inserting all hypothesis words into a zero-length reference.
    for (j in 1 until cost[0].size) {
      cost[0][j] = insertionPenalty * j
      backtrace[0][j] = INS
    }

    // For each next column, go down the rows, recording the min cost edit operation (and the cumulative cost).
    for (i in 1 until cost.size) {
      for (j in 1 until cost[0].size) {
        var subOp: Int
        var cs: Int // it is a substitution if the words aren't equal, but if they are, no penalty is assigned.
        if (reference[i - 1].lowercase(Locale.getDefault()) == hypothesis[j - 1].lowercase(Locale.getDefault())) {
          subOp = OK
          cs = cost[i - 1][j - 1]
        } else {
          subOp = SUB
          cs = cost[i - 1][j - 1] + substitutionPenalty
        }
        val ci = cost[i][j - 1] + insertionPenalty
        val cd = cost[i - 1][j] + deletionPenalty
        val mincost = Math.min(cs, Math.min(ci, cd))
        if (cs == mincost) {
          cost[i][j] = cs
          backtrace[i][j] = subOp
        } else if (ci == mincost) {
          cost[i][j] = ci
          backtrace[i][j] = INS
        } else {
          cost[i][j] = cd
          backtrace[i][j] = DEL
        }
      }
    }

    // Now that we have the minimal costs, find the lowest cost edit to create the hypothesis sequence
    val alignedReference = LinkedList<String?>()
    val alignedHypothesis = LinkedList<String?>()
    var numSub = 0
    var numDel = 0
    var numIns = 0
    var i = cost.size - 1
    var j = cost[0].size - 1
    while (i > 0 || j > 0) {
      when (backtrace[i][j]) {
        OK -> {
          alignedReference.add(0, reference[i - 1].lowercase(Locale.getDefault()))
          alignedHypothesis.add(0, hypothesis[j - 1].lowercase(Locale.getDefault()))
          i--
          j--
        }

        SUB -> {
          alignedReference.add(0, reference[i - 1].uppercase(Locale.getDefault()))
          alignedHypothesis.add(0, hypothesis[j - 1].uppercase(Locale.getDefault()))
          i--
          j--
          numSub++
        }

        INS -> {
          alignedReference.add(0, null)
          alignedHypothesis.add(0, hypothesis[j - 1].uppercase(Locale.getDefault()))
          j--
          numIns++
        }

        DEL -> {
          alignedReference.add(0, reference[i - 1].uppercase(Locale.getDefault()))
          alignedHypothesis.add(0, null)
          i--
          numDel++
        }
      }
    }
    return Alignment(
      alignedReference.toArray<String>(arrayOf<String>()),
      alignedHypothesis.toArray<String>(arrayOf<String>()),
      numSub,
      numIns,
      numDel
    )
  }

  companion object {
    /**
     * Cost of a substitution string edit operation applied during alignment.
     * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings.
     */
    const val DEFAULT_SUBSTITUTION_PENALTY = 100

    /**
     * Cost of an insertion string edit operation applied during alignment.
     * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings.
     */
    const val DEFAULT_INSERTION_PENALTY = 75

    /**
     * Cost of a deletion string edit operation applied during alignment.
     * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings.
     */
    const val DEFAULT_DELETION_PENALTY = 75
  }
}
