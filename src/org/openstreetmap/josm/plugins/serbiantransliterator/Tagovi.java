package org.openstreetmap.josm.plugins.serbiantransliterator;

enum Tagovi{
	NAME("name"), CIRILICA("name:sr"), LATINICA("name:sr-Latn"), ENGLESKI("name:en"), HASH("serbianTransliteratorHash");
	private String tag;

	private Tagovi(String Tagovi) {
		this.tag = Tagovi;
	}

	public String getTagKey() {
		return tag;
	}
}