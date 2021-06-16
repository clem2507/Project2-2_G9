package nlp;

import java.util.ArrayList;

/*** This class will score the similarity between a query and a (set of) sentences*/
public class ScoreUtils {

    /*** Token-based scoring algorithm
     *
     * @return Amount of tokens to be added to Q to get to S
     */
    public int tokenScore(String query, String sentence) {
        ArrayList<String> queryTokens = new ArrayList<>(Tokenizer.asTokenList(query));
        ArrayList<String> sentenceTokens = new ArrayList<>(Tokenizer.asTokenList(sentence));
        int i = 0;
        int tokenScore = 0;
        boolean matchQ;
        for (String q : queryTokens) {
            matchQ = false;
            while (!matchQ) {
                if (i == sentenceTokens.size()) {
                    return -1;
                }
                if (q.equals(sentenceTokens.get(i))) {
                    matchQ = true;
                    i++;
                }
                else {
                    tokenScore++;
                    i++;
                }
            }
        }
        return tokenScore + (sentenceTokens.size() - i);
    }

    /*** Sequence-based scoring algorithm. Will recognize concurrent missing tokens and mark them as missing sequences
     *
     * @return Amount of sequences that need to be added to Q to get to S
     */
    public int seqScore(String query, String sentence) {
        ArrayList<String> queryTokens = new ArrayList<>(Tokenizer.asTokenList(query));
        ArrayList<String> sentenceTokens = new ArrayList<>(Tokenizer.asTokenList(sentence));
        int i = 0;
        int seqScore = 0;
        boolean matchQ;
        boolean isSeq = false;
        for (String q : queryTokens) {
            matchQ = false;
            while (!matchQ) {
                if (i == sentenceTokens.size()) {
                    return -1;
                }
                if (q.equals(sentenceTokens.get(i))) {
                    matchQ = true;
                    i++;
                    if (isSeq) {
                        isSeq = false;
                        seqScore++;
                    }
                }
                else {
                    isSeq = true;
                    i++;
                }
            }
        }
        // Sequence at end of sentence
        if (sentenceTokens.size() - i > 0) {
            return seqScore + 1;
        }
        return seqScore;
    }
}
