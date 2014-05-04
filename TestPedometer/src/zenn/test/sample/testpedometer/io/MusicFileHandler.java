package zenn.test.sample.testpedometer.io;

import java.util.ArrayList;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.maps.ItemizedOverlay;

import android.content.res.AssetManager;

public class MusicFileHandler extends DefaultHandler{

	private String[] item_contents = {
			"title",
			"difficulty",
			"length",
			"file"
	};
	private HashSet<String> item_contents_hash;
	
	public MusicFileHandler() {
		item_contents_hash = new HashSet<String>();
		for(String string : item_contents)
			item_contents_hash.add(string);
	}
	
	
	public static class MusicItem{
		public String title;
		public String difficulty;
		public String length;
		public String file;
	}
	
	private StringBuffer buf;
	
	private ArrayList<MusicItem> musicItems;
	private MusicItem item;
	
	private boolean inItem = false;
	
	// 各要素の始まりで呼び出される
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("body".equals(localName)){
			musicItems = new ArrayList<MusicFileHandler.MusicItem>();
		} else if ("music".equals(localName)){
			item = new MusicItem();
			inItem = true;
		} else if (inItem && item_contents_hash.contains(localName)){
			buf = new StringBuffer();
		}
	}
	// 各要素の終わりで呼び出される
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("music".equals(localName)){
			musicItems.add(item);
			inItem = false;
		} else if ("title".equals(localName) && inItem){
			item.title = buf.toString();
		} else if("difficulty".equals(localName) && inItem){
			item.difficulty = buf.toString();
		} else if ("file".equals(localName) && inItem){
			item.file = buf.toString();
		} else if ("length".equals(localName) && inItem){
			item.length = buf.toString();
		}
		buf = null;
	}
	// 要素内のキャラクタデータについて呼び出される
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// バッファが初期化されていなければ何もしない
		if (buf != null){
			for(int i=start;i<start+length;i++){
				buf.append(ch[i]);
			}
		}
	}
	
	public ArrayList<MusicItem> getMusicItems() {
		return musicItems;
	}
}
