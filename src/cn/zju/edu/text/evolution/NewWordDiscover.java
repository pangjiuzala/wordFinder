package cn.zju.edu.text.evolution;

import java.util.HashSet;
import java.util.Set;

import cn.zju.edu.common.TextUtils;
import cn.zju.edu.index.CnPreviewTextIndexer;
import cn.zju.edu.index.TextIndexer;
import cn.zju.edu.text.dic.CnDictionary;
import cn.zju.edu.text.selector.CnTextSelector;
import cn.zju.edu.text.selector.TextSelector;

public class NewWordDiscover {

	private CnDictionary dictionary;

	/**
	 * Minimum word length
	 */
	private final static int MIN_CANDIDATE_LEN = 2;

	/**
	 * Maximum word length
	 */
	private final static int MAX_CANDIDATE_LEN = 6;

	private static Set<Character> structuralLetterSet = new HashSet<Character>();

	private static char[] structuralLetters = { '��', '��', '��', '��', '��', '˭',
			'��', '��', '��', '��', '��', '��', 'Ҳ', '��', '��', '��', '��', '��', '��',
			'��', 'ѽ', '��', '��', 'Ŷ', '��', '��' };

	static {
		for (char c : structuralLetters) {
			structuralLetterSet.add(c);
		}
	}

	public NewWordDiscover() {
		dictionary = CnDictionary.Instance();
	}

	/**
	 * New word discover is based on statistic and entropy, better to sure
	 * document size is in 100kb level, or you may get a unsatisfied result.
	 * 
	 * @param document
	 * @return
	 */
	public Set<String> discover(String document) {

		Set<String> set = new HashSet<String>();
		TextIndexer indexer = new CnPreviewTextIndexer(document);
		TextSelector selector = new CnTextSelector(document, MIN_CANDIDATE_LEN,
				MAX_CANDIDATE_LEN);
		EntropyJudger judger = new EntropyJudger(indexer);
		String candidate;
		while (!selector.end()) {
			candidate = selector.next();
			if (TextUtils.isBlank(candidate)) {
				continue;
			}
			if (structuralLetterSet.contains(candidate.charAt(0))
					|| structuralLetterSet.contains(candidate.charAt(candidate
							.length() - 1))) {
				continue;
			}
			// Replace IF clause with "set.contains(candidate)" if you want to
			// find new word without any dictionary
			if (dictionary.contains(candidate) || set.contains(candidate)) {
				selector.select();
			} else if (judger.judge(candidate)) {
				set.add(candidate);
			}
		}
		return set;
	}
}
