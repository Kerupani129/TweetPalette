package net.kerupani129.tweetwhitecanvas.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class ResourceUtil {

	/**
	 * コンストラクタ
	 */
	private ResourceUtil() {}

	/**
	 * リソースの XML から 文字列 を取得
	 *
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Map<String, String> getStringMapFromXml(Resources res, int id) throws XmlPullParserException, IOException {

		XmlResourceParser xpp = res.getXml(id);

        int eventType = xpp.getEventType();
        int nest = 0;

        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = null;
        String name = null;

		while(eventType != XmlPullParser.END_DOCUMENT){

			switch (eventType) {
				case XmlPullParser.START_TAG: {
					String tag = xpp.getName();
					sb = new StringBuilder();
					name = xpp.getAttributeValue(null, "name");
					if ("string".equals(tag)) nest++;
					break;
				}
				case XmlPullParser.TEXT: {

					String text = xpp.getText();

					if (nest > 0) {
						sb.append(text);
					}
					break;
				}
				case XmlPullParser.END_TAG: {
					String tag = xpp.getName();
					if ("string".equals(tag)) {
						if (--nest == 0) {
							map.put(name, sb.toString());
						}
					}
					break;
				}
			}

            eventType = xpp.next();

		}

		return map;

	}

}
