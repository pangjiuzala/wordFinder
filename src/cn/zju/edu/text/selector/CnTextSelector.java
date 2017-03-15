package cn.zju.edu.text.selector;

import cn.zju.edu.common.TextUtils;

public class CnTextSelector extends CommonTextSelector {

	public CnTextSelector(String document, int minSelectLen, int maxSelectLen) {
		super(document, minSelectLen, maxSelectLen);
	}

	protected void adjustCurLen() {
		while (pos < docLen && !TextUtils.isCnLetter(document.charAt(pos))) {
			pos++;
		}
		for (int i = 0; i < maxSelectLen && pos + i < docLen; i++) {
			if (!TextUtils.isCnLetter(document.charAt(pos + i))) {
				curLen = i;
				if (curLen < minSelectLen) {
					pos++;
					adjustCurLen();
				}
				return;
			}
		}

		curLen = pos + maxSelectLen > docLen ? docLen - pos : maxSelectLen;
	}
}
