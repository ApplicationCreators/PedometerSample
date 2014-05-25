package zenn.test.sample.testpedometer.io;

import java.util.ArrayList;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MusicFileHandler extends DefaultHandler{
	
	private String[] item_contents = {
			"id",
			"title",
			"difficulty",
			"length",
			"file",
			"medley_id",
			"medley_name",
			"music_id"
	};
	private HashSet<String> item_contents_hash;
	
	public MusicFileHandler() {
		item_contents_hash = new HashSet<String>();
		for(String string : item_contents)
			item_contents_hash.add(string);
	}
	
	public static class MusicItem{
		public String id;
		public String title;
		public String difficulty;
		public String length;
		public String file;
	}
	public static class MedleyItem{
		public String id;
		public String name;
		public ArrayList<String> ids;
	}
	
	private StringBuffer buf;
	
	private ArrayList<MusicItem> musicItems;
	private ArrayList<MedleyItem> medleyItems;
	private MusicItem item;
	private MedleyItem medley;
	
	private boolean inItem = false;
	
	// 各要素の始まりで呼び出される
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("body".equals(localName)){
			musicItems = new ArrayList<MusicFileHandler.MusicItem>();
			medleyItems = new ArrayList<MusicFileHandler.MedleyItem>();
		} else if ("music".equals(localName)){
			item = new MusicItem();
			inItem = true;
		} else if ("medley".equals(localName)){
			medley = new MedleyItem();
			medley.ids = new ArrayList<String>();
			inItem = true;
		} else if (inItem && item_contents_hash.contains(localName)){
			buf = new StringBuffer();
		}
	}
	// 各要素の終わりで呼び出される
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("music".equals(localName)){								//////////// 音楽
			musicItems.add(item);
			inItem = false;
		} else if ("id".equals(localName) && inItem){
			item.id = buf.toString();
		} else if ("title".equals(localName) && inItem){
			item.title = buf.toString();
		} else if("difficulty".equals(localName) && inItem){
			item.difficulty = buf.toString();
		} else if ("file".equals(localName) && inItem){
			item.file = buf.toString();
		} else if ("length".equals(localName) && inItem){
			item.length = buf.toString();
		} else if ("medley".equals(localName) && inItem){			/////////// メドレー
			medleyItems.add(medley);
			inItem = false;
		} else if ("medley_id".equals(localName) && inItem){
			medley.id = buf.toString();
		} else if ("medley_name".equals(localName) && inItem){
			medley.name = buf.toString();
		} else if ("music_id".equals(localName) && inItem){
			medley.ids.add(buf.toString());
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
	
	public ArrayList<MedleyItem> getMedleyItems(){
		return medleyItems;
	}
}
